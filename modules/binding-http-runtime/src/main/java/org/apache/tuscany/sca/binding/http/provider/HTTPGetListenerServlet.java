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

package org.apache.tuscany.sca.binding.http.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;

/**
 * Servlet responsible for dispatching HTTP GET requests to the
 * target component implementation.
 *
 * @version $Rev$ $Date$
 */
public class HTTPGetListenerServlet extends HttpServlet {
    private static final long serialVersionUID = 2865466417329430610L;
    
    private MessageFactory messageFactory;
    private Invoker getInvoker;
    
    /**
     * Constructs a new HTTPServiceListenerServlet.
     */
    public HTTPGetListenerServlet(Invoker getInvoker, MessageFactory messageFactory) {
        this.getInvoker = getInvoker;
        this.messageFactory = messageFactory;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {
            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        String id = path.substring(1);
        requestMessage.setBody(new Object[] {id});
        Message responseMessage = getInvoker.invoke(requestMessage);
        if (responseMessage.isFault()) {
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();
        
    }

}
