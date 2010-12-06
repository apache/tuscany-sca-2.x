/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.binding.http.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Handles the xml wire format for the http binding
 */
public class HTTPXMLWireFormatServiceInterceptor implements Interceptor {

    private Invoker next;
    private DOMHelper domHelper;

    public HTTPXMLWireFormatServiceInterceptor(RuntimeEndpoint endpoint, DOMHelper domHelper) {
        this.domHelper = domHelper;
    }

    @Override
    public void setNext(Invoker next) {
        this.next = next;
    }

    @Override
    public Invoker getNext() {
        return next;
    }

    @Override
    public Message invoke(Message msg) {
        try {
            return invokeResponse(getNext().invoke(invokeRequest(msg)));
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private Message invokeRequest(Message msg) throws IOException, SAXException {
        HTTPContext context = msg.getBindingContext();
        HttpServletRequest servletRequest = context.getHttpRequest();
        if ("GET".equals(servletRequest.getMethod())) {
            msg.setBody(getRequestFromQueryString(msg.getOperation(), servletRequest));
        } else {
            msg.setBody(new Object[]{domHelper.load(read(servletRequest))});
        }
        return msg;
    }

    private Message invokeResponse(Message msg) throws IOException {
        HTTPContext context = msg.getBindingContext();
        HttpServletResponse servletResponse = context.getHttpResponse();

        servletResponse.setContentType("text/xml");
        
        Object o = msg.getBody();
        if (msg.isFault()) {
            String xml = domHelper.saveAsString((Node)((FaultException)o).getFaultInfo());
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, xml);
        } else {
            String xml = "";
            if (o instanceof Element) {
               xml = domHelper.saveAsString((Node)o);
            } else if ((o instanceof Object[]) && ((Object[])o)[0] instanceof Node) {
                xml = domHelper.saveAsString((Node)((Object[])o)[0]);
            } else if (o != null) {
                throw new IllegalStateException("expecting Node payload: " + o);
            }
            servletResponse.getOutputStream().println(xml);
        }

        return msg;
    }

    /**
     * Turn the query request into XML.
     */
    protected Object[] getRequestFromQueryString(Operation operation, ServletRequest servletRequest) throws IOException, SAXException {
        List<Object> xmlRequestArray = new ArrayList<Object>();
        for (String name : getOrderedParameterNames(servletRequest)) {
            xmlRequestArray.add(domHelper.load("<" + name + ">" + servletRequest.getParameter(name) + "</" + name + ">"));
        }
        return xmlRequestArray.toArray();
    }

    /**
     * Get the request parameter names in the correct order.
     * Either the query parameters are named arg0, arg1, arg2 etc or else use the order
     * from the order in the query string. Eg, the url:
     *   http://localhost:8085/HelloWorldService/sayHello2?first=petra&last=arnold&callback=foo"
     * should invoke:
     *   sayHello2("petra", "arnold")
     * so the parameter names should be ordered: "first", "last"
     */
    protected List<String> getOrderedParameterNames(ServletRequest servletRequest) {
        List<String> orderedNames = new ArrayList<String>();
        Set<String> parameterNames = servletRequest.getParameterMap().keySet();
        if (parameterNames.contains("arg0")) {
            for (int i=0; i<parameterNames.size(); i++) {
                String name = "arg" + i;
                if (parameterNames.contains(name)) {
                    orderedNames.add(name);
                } else {
                    break;
                }
            }
        } else {
            final String queryString = ((HttpServletRequest)servletRequest).getQueryString();
            SortedSet<String> sortedNames = new TreeSet<String>(new Comparator<String>(){
                public int compare(String o1, String o2) {
                    int i = queryString.indexOf(o1);
                    int j = queryString.indexOf(o2);
                    return i - j;
                }});
            for (String name : parameterNames) {
                sortedNames.add(name);    
            }
            orderedNames.addAll(sortedNames);
        }
        return orderedNames;
    }

    protected static String read(HttpServletRequest servletRequest) throws IOException {
        InputStream is = servletRequest.getInputStream();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
