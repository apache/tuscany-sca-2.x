package org.apache.tuscany.spi.event;

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
