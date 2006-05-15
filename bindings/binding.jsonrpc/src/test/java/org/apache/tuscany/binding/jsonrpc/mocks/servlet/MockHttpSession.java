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

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class MockHttpSession implements HttpSession {

    public long getCreationTime() {
        return 0;
    }

    public String getId() {
        return null;
    }

    public long getLastAccessedTime() {
        return 0;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public void setMaxInactiveInterval(int arg0) {
    }

    public int getMaxInactiveInterval() {
        return 0;
    }

    @SuppressWarnings("deprecation")
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        return null;
    }

    public Object getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    public Object getValue(String arg0) {
        return null;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public String[] getValueNames() {
        return null;
    }

    HashMap<String, Object> attributes = new HashMap<String, Object>();

    public void setAttribute(String arg0, Object arg1) {
        attributes.put(arg0, arg1);
    }

    public void putValue(String arg0, Object arg1) {
    }

    public void removeAttribute(String arg0) {
    }

    public void removeValue(String arg0) {
    }

    public void invalidate() {
    }

    public boolean isNew() {
        return false;
    }

}
