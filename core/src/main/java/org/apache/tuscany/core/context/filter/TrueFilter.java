package org.apache.tuscany.core.context.filter;

import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.Event;

/**
 * An event filter that always returns a true condition
 * 
 * @version $$Rev$$ $$Date$$
 */
public class TrueFilter implements EventFilter {

    public boolean match(Event event) {
        return true;
    }
}
