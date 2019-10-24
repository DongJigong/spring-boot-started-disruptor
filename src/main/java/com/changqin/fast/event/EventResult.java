package com.changqin.fast.event;

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
