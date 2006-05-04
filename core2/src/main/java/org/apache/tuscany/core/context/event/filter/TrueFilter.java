package org.apache.tuscany.core.context.event.filter;

import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;

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
