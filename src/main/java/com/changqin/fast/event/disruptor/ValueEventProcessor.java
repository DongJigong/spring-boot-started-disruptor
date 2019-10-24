package com.changqin.fast.event.disruptor;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;

public class ValueEventProcessor {
	protected RingBuffer<ValueEvent>	ringBuffer;

	private long						waitAtSequence	= 0;

	public ValueEventProcessor(RingBuffer<ValueEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void send(Object result) {
//		ringBuffer.setGatingSequences(new Sequence(-1));
		ringBuffer.addGatingSequences(new Sequence(-1));

		waitAtSequence = ringBuffer.next();
		ValueEvent ve = ringBuffer.get(waitAtSequence);
		ve.setValue(result);
		ringBuffer.publish(waitAtSequence);
	}

	
	/**
	 * 默认策略为超时策略
	 * @param
	 * @return
	 */
	public ValueEvent waitFor() {
		try {
			SequenceBarrier barrier = ringBuffer.newBarrier();
			long a = barrier.waitFor(waitAtSequence);
//			long a = barrier.waitFor(waitAtSequence, timeoutForReturnResult, TimeUnit.MILLISECONDS);
			return ringBuffer.get(a);
		} catch (AlertException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ValueEvent waitForBlocking() {
		try {
			SequenceBarrier barrier = ringBuffer.newBarrier();
			long a = barrier.waitFor(waitAtSequence);
			ValueEvent ve = ringBuffer.get(a);
			return ve;
		} catch (AlertException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getWaitAtSequence() {
		return waitAtSequence;
	}

	public void setWaitAtSequence(long waitAtSequence) {
		this.waitAtSequence = waitAtSequence;
	}
}
