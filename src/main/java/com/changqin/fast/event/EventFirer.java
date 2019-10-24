package com.changqin.fast.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.changqin.fast.disruptor.Sender;
import com.changqin.fast.domain.message.DomainMessage;
import com.changqin.fast.event.disruptor.DisruptorFactory;
import com.changqin.fast.event.disruptor.EventDisruptor;
import com.changqin.fast.event.disruptor.EventResultDisruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

@SuppressWarnings("rawtypes")
public class EventFirer {
	private final Logger					logger	= LoggerFactory.getLogger(EventFirer.class);
	private DisruptorFactory				disruptorFactory;
	protected final Map<String, Disruptor>	topicDisruptors;

	public EventFirer(DisruptorFactory disruptorFactory) {
		super();
		this.disruptorFactory = disruptorFactory;
		this.topicDisruptors = new ConcurrentHashMap<String, Disruptor>();
	}

	
	public void fire(DomainMessage domainMessage, String topic) {
		try {
			Disruptor disruptor = topicDisruptors.get(topic);

			if (disruptor == null) {
				disruptor = disruptorFactory.getDisruptor(topic);
				if (null == disruptor) {
					logger.error("not create disruptor for topic:{}.", topic);
					return;
				}
				topicDisruptors.put(topic, disruptor);
			}
			
			if (topic != null && (topic.startsWith("get") || topic.startsWith("check"))) {
				domainMessage.setResultEvent(new EventResultDisruptor(topic, domainMessage,10000));
			}

			RingBuffer ringBuffer = disruptor.getRingBuffer();
			long sequence = ringBuffer.next();

			EventDisruptor eventDisruptor = (EventDisruptor) ringBuffer.get(sequence);
			if (eventDisruptor == null)
				return;
			eventDisruptor.setTopic(topic);
			eventDisruptor.setDomainMessage(domainMessage);
			ringBuffer.publish(sequence);
		} catch (Exception e) {
			logger.error("fire event:{} error:{} ", domainMessage.getEventSource(), ExceptionUtils.getStackTrace(e));
		}
	}

	public void fire(DomainMessage domainMessage, Sender send) {
		String topic = send.value();
		fire(domainMessage, topic);
	}
}
