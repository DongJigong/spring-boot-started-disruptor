package com.changqin.fast.event.disruptor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.ExceptionHandler;

public class IgnoreExceptionHandler implements ExceptionHandler<Object> {
	private final Logger	logger	= LoggerFactory.getLogger(IgnoreExceptionHandler.class);

	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		logger.error("Exception processing: sequence:{},event:{},ex:{}", new Object[] { sequence, event, ExceptionUtils.getStackTrace(ex) });
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		logger.error("Exception:{} during onStart()", ExceptionUtils.getStackTrace(ex));
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		logger.error("Exception:{} during onShutdown()", ExceptionUtils.getStackTrace(ex));
	}
}
