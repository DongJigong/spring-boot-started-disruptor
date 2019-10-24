package com.changqin.fast.event.disruptor;

import com.changqin.fast.domain.message.DomainEventHandler;
import com.lmax.disruptor.EventHandler;
import javafx.event.Event;

@SuppressWarnings({"rawtypes"})
public class DomainEventHandlerAdapter implements EventHandler<EventDisruptor> {
    private DomainEventHandler handler;


    public DomainEventHandlerAdapter(DomainEventHandler handler) {
        super();
        this.handler = handler;
    }

    public void onEvent(final EventDisruptor event, long sequence, final boolean endOfBatch) throws Exception {
        handler.onEvent(event, endOfBatch);
    }
}
