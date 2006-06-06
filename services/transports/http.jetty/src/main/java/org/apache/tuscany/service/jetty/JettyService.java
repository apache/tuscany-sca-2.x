package org.apache.tuscany.service.jetty;

import java.io.File;
import java.io.IOException;

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

    /**
     * Returns the port the Jetty server is configured to listen on
     */
    int getPort();

    void registerComposite(File compositeLocation) throws IOException;

}
