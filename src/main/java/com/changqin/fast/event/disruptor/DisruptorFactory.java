package com.changqin.fast.event.disruptor;

import com.changqin.fast.container.ContainerWrapper;
import com.changqin.fast.domain.consumer.ConsumerMethodHolder;
import com.changqin.fast.domain.message.DomainEventDispatchHandler;
import com.changqin.fast.domain.message.DomainEventHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

public class DisruptorFactory implements EventFactory {

	private final Logger										logger	= LoggerFactory.getLogger(DisruptorFactory.class);
	protected final Map<String, TreeSet<DomainEventHandler>>	handlesMap;
	private String												RingBufferSize;

	private final ContainerWrapper								containerWrapper;

	public DisruptorFactory(DisruptorParams disruptorParams, ContainerWrapper containerWrapper) {
		this.RingBufferSize = disruptorParams.getRingBufferSize();
		this.containerWrapper = containerWrapper;
		this.handlesMap = new ConcurrentHashMap<String, TreeSet<DomainEventHandler>>();

	}

	public DisruptorFactory() {
		this.RingBufferSize = "2048";
		this.containerWrapper = null;
		this.handlesMap = new ConcurrentHashMap<String, TreeSet<DomainEventHandler>>();
	}

	private Disruptor createDw(final String topic) {
		WaitStrategy waitStrategy = new BlockingWaitStrategy();
//		ClaimStrategy claimStrategy = new MultiThreadedClaimStrategy(Integer.parseInt(RingBufferSize));
		Disruptor disruptor = new Disruptor(this,Integer.parseInt(RingBufferSize), new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread t = new Thread(runnable);
				t.setName(topic);
				return t;
			}
		}, ProducerType.MULTI, waitStrategy);
//		disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
		disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());

		return disruptor;
	}

	public Disruptor addEventMessageHandler(String topic, TreeSet<DomainEventHandler> handlers) {
		if (handlers.size() == 0)
			return null;
		Disruptor dw = createDw(topic);
		EventHandlerGroup eh = null;
		for (DomainEventHandler handler : handlers) {
			DomainEventHandlerAdapter dea = new DomainEventHandlerAdapter(handler);
			if (eh == null) {
				eh = dw.handleEventsWith(dea);
			} else {
				eh = eh.handleEventsWith(dea);
			}
		}
		return dw;
	}

	/**
	 * one event one EventDisruptor
	 *
	 * @param topic
	 * @return
	 */
	public Disruptor getDisruptor(String topic) {
		TreeSet handlers = handlesMap.get(topic);
		if (handlers == null) {
			handlers = loadEvenHandler(topic);
			handlers = loadOnEventConsumers(topic, handlers);
			if (handlers.size() == 0) {
				logger.error("no found the classes annotated with @Consumer or @Component or @Service topic:{}.", topic);
				return null;
			}
			handlesMap.put(topic, handlers);
		}
		Disruptor disruptor = addEventMessageHandler(topic, handlers);
		if (disruptor == null)
			return null;
		disruptor.start();
		return disruptor;
	}

	/**
	 * if there are many consumers, execution order will be alphabetical list by Name of @Consumer class.
	 *
	 * @param topic
	 * @return
	 */
	protected TreeSet<DomainEventHandler> loadEvenHandler(String topic) {
		TreeSet<DomainEventHandler> ehs = this.getTreeSet();
		Collection<String> consumers = (Collection<String>) containerWrapper.lookupConsumer(topic);
		if (consumers == null || consumers.size() == 0) {
			return ehs;
		}
		for (String consumerName : consumers) {
			DomainEventHandler eh = (DomainEventHandler) containerWrapper.lookupOriginal(consumerName);
			ehs.add(eh);
		}

		return ehs;

	}

	protected TreeSet<DomainEventHandler> loadOnEventConsumers(String topic, TreeSet<DomainEventHandler> ehs) {
		Collection consumerMethods = (Collection) containerWrapper.lookupOnEventConsumer(topic);
		if (consumerMethods == null)
			return ehs;
		for (Object o : consumerMethods) {
			ConsumerMethodHolder consumerMethodHolder = (ConsumerMethodHolder) o;
			DomainEventDispatchHandler domainEventDispatchHandler = new DomainEventDispatchHandler(consumerMethodHolder, containerWrapper);
			ehs.add(domainEventDispatchHandler);
		}
		return ehs;

	}

	private TreeSet<DomainEventHandler> getTreeSet() {
		return new TreeSet(new Comparator() {
			public int compare(Object num1, Object num2) {
				String inum1, inum2;
				inum1 = num1.getClass().getName();
				inum2 = num2.getClass().getName();
				if (inum1.compareTo(inum2) < 1) {
					return -1; // returning the first object
				} else {

					return 1;
				}
			}

		});
	}

	public EventDisruptor newInstance() {
		return new EventDisruptor();
	}
}