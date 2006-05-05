package org.apache.tuscany.spi.wire;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SourceInvocationConfiguration extends InvocationConfiguration {
    /**
     * Sets the head interceptor of the target-side configuration for the wire. Used when the runtime bridges source and target
     * chains.
     *
     * @param interceptor
     */
    void setTargetInterceptor(Interceptor interceptor);

    /**
     * Returns the head target-side interceptor. This will be the head interceptor of the "bridged" target configuration.
     */
    Interceptor getTargetInterceptor();

    /**
     * Sets the target-side request channel. Used when the runtime bridges source and target chains.
     */
    void setTargetRequestChannel(MessageChannel channel);

    /**
     * Sets the target-side response channel. Used when the runtime bridges source and target chains.
     */
    void setTargetResponseChannel(MessageChannel channel);

}
