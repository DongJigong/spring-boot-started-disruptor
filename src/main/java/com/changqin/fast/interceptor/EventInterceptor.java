package com.changqin.fast.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.changqin.fast.disruptor.Sender;
import com.changqin.fast.domain.message.DomainMessage;
import com.changqin.fast.event.EventFirer;

/**
 * @Author: Binwei.Chen
 * @Date: 13-12-04
 * @Time: 下午2:30
 * @Description: to write something
 */
public class EventInterceptor implements MethodInterceptor {
	private final Logger	logger	= LoggerFactory.getLogger(EventInterceptor.class);
	protected EventFirer	eventFirer;

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (!methodInvocation.getMethod().isAnnotationPresent(Sender.class)) {
			return methodInvocation.proceed();
		} else {
			Sender send = methodInvocation.getMethod().getAnnotation(Sender.class);
			Object result = null;
			try {

				result = methodInvocation.proceed();

				DomainMessage message = null;
				if (result == null) {
					message = new DomainMessage(null);
				} else if (!(DomainMessage.class.isAssignableFrom(result.getClass()))) {
					logger.error("method:{} that with @Send must defines return type is DomainMessage.", methodInvocation.getThis());
					return result;
				} else {
					message = (DomainMessage) result;
				}

				eventFirer.fire(message, send);
			} catch (Exception e) {
				logger.error("invoke error:{} ", ExceptionUtils.getStackTrace(e));
			}
			return result;
		}
	}

	public void setEventFirer(EventFirer eventFirer) {
		this.eventFirer = eventFirer;
	}
}
