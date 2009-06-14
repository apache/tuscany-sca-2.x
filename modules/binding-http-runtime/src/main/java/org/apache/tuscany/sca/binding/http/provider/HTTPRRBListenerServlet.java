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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.http.HTTPBindingContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;

public class HTTPRRBListenerServlet extends HttpServlet {

    private static final long serialVersionUID = 6688524143716091739L;

    transient private Binding binding;
    transient private Invoker bindingInvoker;
    transient private MessageFactory messageFactory;

    /**
     * Constructs a new HTTPServiceListenerServlet.
     */
    public HTTPRRBListenerServlet(Binding binding, Invoker bindingInvoker, MessageFactory messageFactory) {
        this.binding = binding;
        this.bindingInvoker = bindingInvoker;
        this.messageFactory = messageFactory;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Decode using the charset in the request if it exists otherwise
        // use UTF-8 as this is what all browser implementations use.
        // The JSON-RPC-Java JavaScript client is ASCII clean so it
        // although here we can correctly handle data from other clients
        // that do not escape non ASCII data
        String charset = request.getCharacterEncoding();
        if (charset == null) {
            charset = "UTF-8";
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));

        // Read the request
        CharArrayWriter data = new CharArrayWriter();
        char[] buf = new char[4096];
        int ret;
        while ((ret = in.read(buf, 0, 4096)) != -1) {
            data.write(buf, 0, ret);
        }
        
        HTTPBindingContext bindingContext = new HTTPBindingContext();
        bindingContext.setHttpRequest(request);
        bindingContext.setHttpResponse(response);

        // Dispatch the service interaction to the service invoker
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setBindingContext(bindingContext);
        requestMessage.setBody(new Object[]{data});
        
        Message responseMessage = bindingInvoker.invoke(requestMessage);
        
        // return response to client
        if (responseMessage.isFault()) {            
            // Turn a fault into an exception
            //throw new ServletException((Throwable)responseMessage.getBody());
            Throwable e = (Throwable)responseMessage.getBody();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        } else {
            byte[] bout;
            bout = responseMessage.<Object>getBody().toString().getBytes("UTF-8");
            response.getOutputStream().write(bout);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } 
    }
    
}
