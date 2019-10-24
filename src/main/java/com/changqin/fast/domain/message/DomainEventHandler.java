package com.changqin.fast.domain.message;

import com.changqin.fast.event.disruptor.EventDisruptor;

public interface DomainEventHandler<T> {
	void onEvent(EventDisruptor event, final boolean endOfBatch) throws Exception;
}
