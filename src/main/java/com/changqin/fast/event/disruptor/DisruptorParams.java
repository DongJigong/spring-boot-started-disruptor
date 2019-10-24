package com.changqin.fast.event.disruptor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Binwei.Chen
 * @Date: 13-12-04
 * @Time: 下午2:30
 * @Description: to write something
 */
public class DisruptorParams {


	private String	RingBufferSize;

	public DisruptorParams(String ringBufferSize) {
		super();
		RingBufferSize = ringBufferSize;
	}

	public String getRingBufferSize() {
		return RingBufferSize;
	}

	public void setRingBufferSize(String ringBufferSize) {
		RingBufferSize = ringBufferSize;
	}
}
