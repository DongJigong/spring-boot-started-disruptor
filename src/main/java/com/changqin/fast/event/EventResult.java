package com.changqin.fast.event;

/**
 * @Author: Binwei.Chen
 * @Date: 13-12-04
 * @Time: 下午2:30
 * @Description: to write something
 */
public interface EventResult {

	/**
	 * get a Event Result until time out value: timeoutForReturnResult
	 * 
	 * @return
	 */
	Object get();

	/**
	 * Blocking until get a Event Result
	 * 
	 * @return
	 */
	Object getBlockedValue();

	void send(Object eventResult);
}
