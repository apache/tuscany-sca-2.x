package org.apache.tuscany.sca.host.webapp;

import java.util.Enumeration;

import javax.servlet.ServletContext;

/**
 * The interface that represents a given scope (Webapp vs Servlet) that provides the configuration of the Tuscany node
 */
public interface WebContextConfigurator {
    String getInitParameter(String name);

    Enumeration<String> getInitParameterNames();

    ServletContext getServletContext();

    void setAttribute(String name, Object value);

    <T> T getAttribute(String name);

    String getName();
}