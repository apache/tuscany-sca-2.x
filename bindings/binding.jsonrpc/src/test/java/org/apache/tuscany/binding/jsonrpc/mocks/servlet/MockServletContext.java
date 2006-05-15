/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.jsonrpc.mocks.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MockServletContext implements ServletContext {

    public ServletContext getContext(String arg0) {

        return null;
    }

    public int getMajorVersion() {

        return 0;
    }

    public int getMinorVersion() {

        return 0;
    }

    public String getMimeType(String arg0) {

        return null;
    }

    public Set getResourcePaths(String arg0) {

        return null;
    }

    public URL getResource(String arg0) throws MalformedURLException {

        return null;
    }

    public InputStream getResourceAsStream(String arg0) {

        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {

        return null;
    }

    public RequestDispatcher getNamedDispatcher(String arg0) {

        return null;
    }

    public Servlet getServlet(String arg0) throws ServletException {

        return null;
    }

    public Enumeration getServlets() {

        return null;
    }

    public Enumeration getServletNames() {

        return null;
    }

    public void log(String arg0) {

    }

    public void log(Exception arg0, String arg1) {

    }

    public void log(String arg0, Throwable arg1) {

    }

    public String getRealPath(String arg0) {

        return null;
    }

    public String getServerInfo() {

        return null;
    }

    public String getInitParameter(String arg0) {

        return null;
    }

    public Enumeration getInitParameterNames() {

        return null;
    }

    HashMap<String, Object> attributes = new HashMap<String, Object>();

    public Object getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public void setAttribute(String arg0, Object arg1) {
        attributes.put(arg0, arg1);
    }

    public void removeAttribute(String arg0) {

    }

    public String getServletContextName() {

        return null;
    }

}
