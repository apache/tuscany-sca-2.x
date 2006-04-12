package org.apache.tuscany.core.context.filter;

import org.apache.tuscany.core.context.EventFilter;
import org.apache.tuscany.core.context.event.Event;

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
