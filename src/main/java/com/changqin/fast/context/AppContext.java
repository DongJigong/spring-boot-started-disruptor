package com.changqin.fast.context;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.changqin.fast.container.ContainerWrapper;
import com.changqin.fast.disruptor.Consumer;
import com.changqin.fast.disruptor.OnEvent;
import com.changqin.fast.domain.consumer.ConsumerMethodHolder;

@SuppressWarnings("rawtypes")
@Component
public class AppContext implements ApplicationContextAware, ApplicationListener {

	@Autowired
	private ContainerWrapper	containerWrapper;
	private ApplicationContext	applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			injectSpringToContainer();
		}
	}

	public void injectSpringToContainer() {
		Map<String, Object> consumers = applicationContext.getBeansWithAnnotation(Consumer.class);
		Map<String, Object> components = applicationContext.getBeansWithAnnotation(Component.class);
		Map<String, Object> services = applicationContext.getBeansWithAnnotation(Service.class);

		// 注册consumer
		if (null != consumers) {
			for (Object consumer : consumers.values()) {
				Consumer consumerAnnotation = consumer.getClass().getAnnotation(Consumer.class);
				containerWrapper.registerConsumer(consumerAnnotation.value(), consumer.getClass().getName());
				containerWrapper.registerOriginal(consumer.getClass().getName(), consumer);
			}
		}

		// 注册components
		if (null != components) {
			injectEvent(components);
		}

		// 注册services
		if (null != services) {
			injectEvent(services);
		}
	}

	private void injectEvent(Map<String, Object> services) {
		for (Object service : services.values()) {
			// 鍒ゆ柇Service瀵硅薄涓槸鍚︽湁浣跨敤@OnEvent娉ㄩ噴鐨勬柟娉?
			Method[] methods = service.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(OnEvent.class)) {
					OnEvent onEvent = method.getAnnotation(OnEvent.class);
					containerWrapper.registerOriginal(service.getClass().getName(), service);

					ConsumerMethodHolder consumerMethodHolder = new ConsumerMethodHolder(service.getClass().getName(), method);
					containerWrapper.registerOnEventConsumer(onEvent.value(), consumerMethodHolder);
				}
			}
		}
	}

	public void setContainerWrapper(ContainerWrapper containerWrapper) {
		this.containerWrapper = containerWrapper;
	}
}
