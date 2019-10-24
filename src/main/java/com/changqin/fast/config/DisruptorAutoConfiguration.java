package com.changqin.fast.config;

import com.changqin.fast.container.ContainerWrapper;
import com.changqin.fast.container.DefaultContainerWrapper;
import com.changqin.fast.event.EventFirer;
import com.changqin.fast.event.disruptor.DisruptorFactory;
import com.changqin.fast.event.disruptor.DisruptorParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisruptorAutoConfiguration {

	@Value("${disruptor.ringBuffer}")
	private String ringBuffer;

	@Bean
	public DisruptorParams disruptorParams(){
		return new DisruptorParams(ringBuffer);
	}

	@Bean
	public DisruptorFactory defaultDisruptorFactory(ContainerWrapper containerWrapper) {
		return new DisruptorFactory(disruptorParams(),containerWrapper);
	}

	@Bean
	public ContainerWrapper containerWrapper(){
		return new DefaultContainerWrapper();
	}
	
	@Bean
	public EventFirer eventFirer(DisruptorFactory disruptorFactory) {
		return new EventFirer(disruptorFactory);
	}

}
