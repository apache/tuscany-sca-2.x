package org.apache.tuscany.container.groovy;

/**
 * A monitor used to log events during non-blocking invocations
 * <p/>
 *
 * @version $$Rev$$ $$Date$$
 */
public interface AsyncMonitor {

    /**
     * Logs an exception thrown during an invocation
     */
    void executionError(Exception e);

}
