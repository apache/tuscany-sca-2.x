package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.event.Event;

/**
 * A basic implementation of a runtime event
 *
 * @version $$Rev: 415032 $$ $$Date: 2006-06-17 10:28:07 -0700 (Sat, 17 Jun 2006) $$
 */
public abstract class AbstractEvent implements Event {

    protected transient Object source;

    public AbstractEvent(Object source) {
        assert source != null : "Source id was null";
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
