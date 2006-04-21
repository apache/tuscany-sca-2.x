package org.apache.tuscany.binding.jsonrpc.mocks.servlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MockServletConfig implements ServletConfig {

    public String getServletName() {
        return null;
    }

    ServletContext servletContext = new MockServletContext();

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getInitParameter(String arg0) {
        return null;
    }

    public Enumeration getInitParameterNames() {
        return null;
    }

}
