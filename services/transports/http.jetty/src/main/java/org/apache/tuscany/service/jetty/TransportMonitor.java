package org.apache.tuscany.service.jetty;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface TransportMonitor {

    void started(int port);

    void shutdown(int port);

    void startError(int port, Exception e);

    void shutdownError(int port, Exception e);

    void requestHandleError(Exception e);
}
