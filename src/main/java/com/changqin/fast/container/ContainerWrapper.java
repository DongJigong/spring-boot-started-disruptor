package com.changqin.fast.container;

import java.util.Collection;

import com.changqin.fast.domain.consumer.ConsumerMethodHolder;

@SuppressWarnings("rawtypes")
public interface ContainerWrapper {
	Collection lookupConsumer(String topic);

	Collection lookupOnEventConsumer(String topic);

	void registerConsumer(String topic, String className);

	void registerOnEventConsumer(String topic, ConsumerMethodHolder o);

	Object lookupOriginal(String className);

	void registerOriginal(String className, Object o);
}
