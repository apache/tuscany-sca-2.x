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
package org.apache.tuscany.sca.binding.notification.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Receives notification in HTTP request and dispatches it down the wire
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings("serial")
public class NotificationServlet extends HttpServlet {
    
    private NotificationServletHandler handler;
    private NotificationServletStreamHandler servletStreamHandler;
    
    public NotificationServlet(NotificationServletHandler handler) {
        this.handler = handler;
        this.servletStreamHandler = null;
    }

    public NotificationServlet(NotificationServletStreamHandler servletStreamHandler) {
        this.handler = null;
        this.servletStreamHandler = servletStreamHandler;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        if (handler != null) {
            byte[] requestBody = IOUtils.readFully(request.getInputStream(), request.getContentLength());
            byte[] handlersResponse = handler.handle(headers, requestBody);
            if (handlersResponse != null) {
                response.getOutputStream().write(handlersResponse);
                response.getOutputStream().flush();
            }
        }
        else {
            try {
                servletStreamHandler.handle(headers, request.getInputStream(), request.getContentLength(), response.getOutputStream());
            }
            catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
    
    public interface NotificationServletHandler {
        public byte[] handle(Map<String, String> headers, byte[] payload);
    }
    
    public interface NotificationServletStreamHandler {
        public void handle(Map<String, String> headers, ServletInputStream istream, int contentLength, ServletOutputStream ostream);
    }
}
