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
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.binding.http.provider.HTTPContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Handles the default wire format for the http binding
 * 
 * 1- determine the request and response format (xml, json, etc) from the 
 *    binding config or content type header and accept headers
 *    - TODO: need a way to configure the databinding framework based on that format
 * 2- get the request contents from the HttpServletRequest
 *    - for a post its just the request body
 *    - for a get need to convert the query string into a body based on the format (xml, json, etc)
 * 3- send the request on down the wire
 * 4- set the response contents in the HttpServletResponse 
 *    (the databinding should already have put it in the correct format)
 * 
 */
public class HTTPDefaultWireFormatServiceInterceptor implements Interceptor {

    private Invoker next;
    private String jsonpCallbackName = "callback";
    
    public HTTPDefaultWireFormatServiceInterceptor(RuntimeEndpoint endpoint) {
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
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private Message invokeRequest(Message msg) throws IOException {
        HTTPContext context = msg.getBindingContext();
        HttpServletRequest servletRequest = context.getRequest();
        if ("GET".equals(servletRequest.getMethod())) {
            msg.setBody(getRequestFromQueryString(msg.getOperation(), servletRequest));
        } else {
            msg.setBody(getRequestFromPost(msg.getOperation(), servletRequest));
        }
        return msg;
    }
    
    /**
     * The data binding seems to be expecting an Object array of json strings so if the
     * post data is a json array convert that to an array of strings
     * TODO: should this be being done by the data binding framework? 
     */
    private Object[] getRequestFromPost(Operation operation, HttpServletRequest servletRequest) throws IOException {
        List<Object> os = new ArrayList<Object>();
        String data = read(servletRequest);
        if (data.length() > 0) {
            if (data.startsWith("[") && data.endsWith("]")) {
                data = data.substring(1, data.length()-1);
                StringTokenizer st = new StringTokenizer(data, ",");
                while (st.hasMoreElements()) {
                    os.add(st.nextElement());
                }
            } else {
                os.add(data);
            }
        }
        return os.toArray();
    }

    private Message invokeResponse(Message msg) throws IOException {
        HTTPContext context = msg.getBindingContext();
        HttpServletRequest servletRequest = context.getRequest();
        HttpServletResponse servletResponse = context.getResponse();
        
        if (msg.isFault()) {            
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.valueOf(msg.getBody()));
        } else {
            String response = getResponseAsString(servletRequest, servletResponse, msg.getBody());
            servletResponse.getOutputStream().println(response);
        }
        
        return msg;
    }

    /**
     * Turn the request into a string array of JSON structures. The data binding
     * layer will then convert each of the individual parameters into the appropriate
     * types for the implementation interface
     *
     * From ML thread: http://apache.markmail.org/message/ix3vvyomronellmi
     * 1- if the binding configuration contains a mapping from query parameter name to operation parameter then use that. 
     * 2- if the service interface or impl uses jaxrs annotations to name the parameters then use that mapping
     * 3- if the query parameters are name arg0, arg1 etc than use those names for the mapping,
     * 4- otherwise use the order in the query string. 
     */
    protected Object[] getRequestFromQueryString(Operation operation, ServletRequest servletRequest) {
        
        List<DataType> types = operation.getInputType().getLogical();
        int typesIndex = 0;
        
        List<String> jsonRequestArray = new ArrayList<String>();
        
        for (String name : getOrderedParameterNames(servletRequest)) {
            String jsonRequest = "";
            // quote string parameters so clients work in the usual javascript way               
            if (typesIndex < types.size() && String.class.equals(types.get(typesIndex++).getGenericType())) {
                String x = servletRequest.getParameter(name);
                if (x.startsWith("\"") || x.startsWith("'")) {
                    jsonRequest += x;
                } else {
                    if (x.contains("\"")) {
                        jsonRequest += "'" + x + "'";
                    } else {
                        jsonRequest += "\"" + x + "\"";
                    }
                }
            } else {               
                jsonRequest += servletRequest.getParameter(name);
            }  
            jsonRequestArray.add(jsonRequest);
        }

        return jsonRequestArray.toArray();
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
                // ignore system and jsonpCallbackName parameters
                if (!name.startsWith("_") && !name.equals(jsonpCallbackName)) {
                    sortedNames.add(name);    
                }
            }
            orderedNames.addAll(sortedNames);
        }
        return orderedNames;
    }
    
    /**
     * The databinding layer will have converted the return type into a JSON string so simply 
     * add wrap it for return.
     */
    protected String getResponseAsString(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object response) {
        String jsonResponse = response == null ? "" : response.toString();

        if ("GET".equals(servletRequest.getMethod())) {
            // handle JSONP callback name padding
            String callback = servletRequest.getParameter(jsonpCallbackName);
            if (callback != null && callback.length() > 1) {
                jsonResponse = callback + "(" + jsonResponse + ");";
            }
        }

        return jsonResponse;
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
            return sb.toString().trim();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
