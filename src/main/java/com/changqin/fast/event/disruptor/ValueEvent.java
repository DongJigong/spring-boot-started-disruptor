package com.changqin.fast.event.disruptor;

import com.lmax.disruptor.EventFactory;

public class ValueEvent {
	private Object	value;

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public final static EventFactory<ValueEvent>	EVENT_FACTORY	= new EventFactory<ValueEvent>() {
																		public ValueEvent newInstance() {
																			return new ValueEvent();
																		}
																	};
}
