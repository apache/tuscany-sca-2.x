package org.apache.tuscany.core.host;

import javax.servlet.Servlet;

/**
 * Service interface implemented by host environments that allow Servlets
 * to be registered.
 * <p/>
 * This interface allows an SCA system component to register a servlet to handle
 * inbound requests.
 *
 * @version $Rev$ $Date$
 */
public interface ServletHost {
    /**
     * Register a mapping for an instance of a Servlet.
     * This requests that the servlet container direct all requests to the
     * designated mapping to the supplied Servlet instance.
     *
     * @param mapping the uri-mapping for the Servlet
     * @param servlet the Servlet that should be invoked
     */
    void registerMapping(String mapping, Servlet servlet);

    /**
     * Unregister a servlet mapping.
     * This directs the servlet contain not to direct any more requests to
     * a previously registered Servlet.
     *
     * @param mapping the uri-mapping for the Servlet
     */
    void unregisterMapping(String mapping);

}
