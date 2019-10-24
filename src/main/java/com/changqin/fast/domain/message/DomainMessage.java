package com.changqin.fast.domain.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.changqin.fast.event.EventResult;

public class DomainMessage {
	private final Logger	logger					= LoggerFactory.getLogger(DomainMessage.class);
	protected Object		eventSource;
	protected EventResult	resultEvent;

	public DomainMessage(Object eventSource) {
		super();
		this.eventSource = eventSource;
	}

	public Object getEventSource() {
		return eventSource;
	}

	public void setEventSource(Object eventSource) {
		this.eventSource = eventSource;
	}

	public void setResultEvent(EventResult resultEvent) {
		this.resultEvent = resultEvent;
	}

	public EventResult getResultEvent() {
		return resultEvent;
	}

	/**
	 * get a Event Result until time out value
	 * 
	 * @return Event Result
	 */
	public Object getEventResult() {
		if (resultEvent == null) {
			logger.error("eventMessage:{} is null.", eventSource.getClass());
			return null;
		} else
			return resultEvent.get();
	}

	/**
	 * * Blocking until get a Event Result
	 * 
	 * @return
	 */
	public Object getBlockEventResult() {
		if (resultEvent == null) {
			logger.error("eventMessage:{} is null.", eventSource.getClass());
			return null;
		} else
			return resultEvent.getBlockedValue();
	}

	public void setEventResult(Object eventResultValue) {
		if (resultEvent != null)
			resultEvent.send(eventResultValue);
		else
			logger.error("eventMessage:{} is null.", eventSource.getClass());
	}
}
