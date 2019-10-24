package com.changqin.fast.event.disruptor;

import com.changqin.fast.domain.message.DomainMessage;

public interface DisruptorListener {
	void action(DomainMessage domainMessage);
}
