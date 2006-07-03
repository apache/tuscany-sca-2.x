package org.apache.tuscany.service.jetty;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;

/**
 * @version $Rev$ $Date$
 */
public class ConsoleMonitor implements TransportMonitor {
    public void started(int port) {
        System.out.println("Started Jetty port [" + port + "]");
    }

    public void shutdown(int port) {
        System.out.println("Shutdown Jetty port [" + port + "]");
    }

    public void startError(int port, Exception e) {
        System.out.println("Error starting Jetty port [" + port + "]");
        System.out.println(e);
    }

    public void shutdownError(int port, Exception e) {

    }

    public void requestHandleError(Exception e) {
        System.out.println("Error handling Jetty request");
        System.out.println(e);
    }

    public void log(Request request, Response response) {
        System.out.println("Received request");

    }

    public void start() throws Exception {

    }

    public void stop() throws Exception {

    }

    public boolean isRunning() {
        return false;
    }

    public boolean isStarted() {
        return false;
    }

    public boolean isStarting() {
        return false;
    }

    public boolean isStopping() {
        return false;
    }

    public boolean isFailed() {
        return false;
    }
}
