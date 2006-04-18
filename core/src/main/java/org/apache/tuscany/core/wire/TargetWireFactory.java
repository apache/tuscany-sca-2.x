package org.apache.tuscany.core.wire;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface TargetWireFactory<T> extends ProxyFactory<T> {

        /**
        * Returns the configuration information used to create a wire
        */
       public WireTargetConfiguration getProxyConfiguration();

       /**
        * Sets the configuration information used to create a wire
        */
       public void setProxyConfiguration(WireTargetConfiguration config);

}
