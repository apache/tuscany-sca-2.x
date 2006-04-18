package org.apache.tuscany.core.wire;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SourceWireFactory<T> extends ProxyFactory<T>{

    /**
      * Returns the configuration information used to create a proxy
      */
     public WireSourceConfiguration getProxyConfiguration();

     /**
      * Sets the configuration information used to create a proxy
      */
     public void setProxyConfiguration(WireSourceConfiguration config);

    
}
