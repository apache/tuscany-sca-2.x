package org.apache.tuscany.service.jetty;

import org.mortbay.jetty.RequestLog;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface TransportMonitor extends RequestLog {

    void started(int port);

    void shutdown(int port);

    void startError(int port, Exception e);

    void shutdownError(int port, Exception e);

    void requestHandleError(Exception e);
}
