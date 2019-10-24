package com.changqin.fast.event.disruptor;

import java.util.concurrent.TimeUnit;

import com.changqin.fast.domain.message.DomainMessage;
import com.changqin.fast.event.EventResult;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EventResultDisruptor implements EventResult {
	protected String				topic;
	protected DomainMessage			domainMessage;
	protected boolean				over;
	protected Object				result;
	protected ValueEventProcessor	valueEventProcessor;

	public EventResultDisruptor(String topic, DomainMessage domainMessage,long timeout) {
		super();
		this.topic = topic;
		this.domainMessage = domainMessage;

		RingBuffer ringBuffer = RingBuffer.createSingleProducer(ValueEvent.EVENT_FACTORY, 2,new TimeoutBlockingWaitStrategy(timeout,TimeUnit.SECONDS));
		this.valueEventProcessor = new ValueEventProcessor(ringBuffer);
	}

	/**
	 * send event result
	 */
	@Override
	public void send(Object result) {
		valueEventProcessor.send(result);
	}

	@Override
	public Object get() {
		if (over)
			return result;
		ValueEvent ve = valueEventProcessor.waitFor();
		if (ve != null)
			result = ve.getValue();
		return result;
	}

	@Override
	public Object getBlockedValue() {
		if (over)
			return result;
		ValueEvent ve = valueEventProcessor.waitForBlocking();
		if (ve != null)
			result = ve.getValue();
		return result;
	}

	public String getTopic() {
		return topic;
	}

	public void setOver(boolean over) {
		this.over = over;
	}
}
