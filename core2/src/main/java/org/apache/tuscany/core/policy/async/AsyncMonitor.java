package org.apache.tuscany.core.policy.async;

/**
 * A monitor used by {@link AsyncInterceptor]
 *
 * @version $$Rev$$ $$Date$$
 */
public interface AsyncMonitor {

    void executionError(Exception e);

}
