package org.apache.tuscany.container.groovy;

/**
 * A monitor used to log events during non-blocking invocations
 * <p/>
 *
 * @version $$Rev: 424672 $$ $$Date: 2006-07-22 21:46:01 -0700 (Sat, 22 Jul 2006) $$
 */
public interface AsyncMonitor {

    /**
     * Logs an exception thrown during an invocation
     */
    void executionError(Exception e);

}
