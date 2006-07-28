package org.apache.tuscany.service.jetty;

import org.apache.tuscany.spi.host.ServletHost;

import org.mortbay.jetty.Server;

/**
 * Implementations provide a Jetty transport service to the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public interface JettyService extends ServletHost {

    /**
     * Returns the active Jetty server
     */
    Server getServer();


}
