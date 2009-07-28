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

package org.apache.tuscany.sca.binding.jsonp.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONPServlet extends GenericServlet {
    private static final long serialVersionUID = 1L;

    protected transient RuntimeWire wire;
    protected transient Operation operation;
    protected transient ObjectMapper mapper;
    
    public JSONPServlet(RuntimeWire wire, Operation operation) {
        this.wire = wire;
        this.operation = operation;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        String jsonRequest = getJSONRequest(servletRequest);
        Object[] args = jsonToObjects(jsonRequest);
        Object response = invokeService(args);        
        String jsonResponse = getJSONResponse(servletRequest, response);
        servletResponse.getOutputStream().println(jsonResponse);
    }

    /**
     * Turn the request into JSON 
     */
    protected String getJSONRequest(ServletRequest servletRequest) throws IOException, JsonParseException, JsonMappingException {
        String jsonRequest = "[";
        
        List<DataType> types = operation.getInputType().getLogical();
        int typesIndex = 0;
        
        for (Enumeration<?> ns = servletRequest.getParameterNames(); ns.hasMoreElements(); ) {
            String name = (String) ns.nextElement();
            if (!name.startsWith("_") && !"callback".equals(name)) {
                if (jsonRequest.length() > 1) {
                    jsonRequest += ", ";
                }
                
                if (typesIndex < types.size() && String.class.equals(types.get(typesIndex).getGenericType())) {
                    jsonRequest += "\"" + servletRequest.getParameter(name) + "\"";
                } else {
                    jsonRequest += servletRequest.getParameter(name);
                }
                
            }
        }
        jsonRequest += "]";
        return jsonRequest;
    }

    /**
     * Turn the response object into JSON 
     */
    protected String getJSONResponse(ServletRequest servletRequest, Object response) throws IOException, JsonParseException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mapper.writeValue(os , response);
        String jsonResponse = os.toString();

        String callback = servletRequest.getParameter("callback");
        if (callback != null && callback.length() > 1) {
            jsonResponse = callback + "(" + jsonResponse + ");";
        }

        return jsonResponse;
    }

    /**
     * Turn the request JSON into objects 
     */
    protected Object[] jsonToObjects(String jsonRequest) throws IOException, JsonParseException, JsonMappingException {
        Class<?> c = new Object[0].getClass();
        Object[] args = (Object[])mapper.readValue(jsonRequest, c);
        return args;
    }

    /**
     * Send the request down the wire to invoke the service 
     */
    protected Object invokeService(Object[] args) {
        try {
            return wire.invoke(operation, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}