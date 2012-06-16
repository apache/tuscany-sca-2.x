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

package org.apache.tuscany.sca.implementation.widget.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.web.javascript.ComponentJavaScriptGenerator;


/**
 * Implements an invoker for resource component implementations.
 *
 * @version $Rev$ $Date$
 */
class WidgetImplementationInvoker implements Invoker {
    private RuntimeComponent component;
    private ComponentJavaScriptGenerator javaScriptGenerator;
    private String widgetName;
    private String widgetFolderURL;
    private String widgetLocationURL;
    
    WidgetImplementationInvoker(RuntimeComponent component, ComponentJavaScriptGenerator javaScriptGenerator, String widgetName, String widgetFolderURL, String widgetLocationURL) {
        this.component = component;
        this.javaScriptGenerator = javaScriptGenerator;
        this.widgetName = widgetName + ".js";
        this.widgetFolderURL = widgetFolderURL;
        this.widgetLocationURL = widgetLocationURL;
    }
    
    public Message invoke(Message msg) {
        HTTPContext bindingContext = (HTTPContext) msg.getBindingContext();
        HttpServletRequest request = bindingContext.getHttpRequest();


        // Get the request path
        String pathInfo = request.getPathInfo();
        String path = null;
        
        if(pathInfo != null) {
            try {
                path = URLDecoder.decode(pathInfo, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                //ignore for now
            }            
        }

        // Get the resource id from the request message
        String id = path == null ? "" : path.substring(1);
        try {
            
            if (id.length() == 0) {

                // Return an input stream for the widget resource
                URL url = new URL(widgetLocationURL);
                //InputStream is = url.openStream();
                //msg.setBody(is);
                writeResponse(bindingContext, url.openStream());
                
            } else if (id.equals(widgetName)) {
                
                // Generate JavaScript header for use in the Widget
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(bos);
             
                javaScriptGenerator.generateJavaScriptCode(component, pw);
                
                InputStream is = new ByteArrayInputStream(bos.toByteArray());
                
                //msg.setBody(is);
                writeResponse(bindingContext, is);
                
            } else {

                // Return an input stream for a resource inside the
                // widget folder
                URL url = new URL(widgetFolderURL +'/' + id);
                InputStream is = url.openStream();
                //msg.setBody(is);
                writeResponse(bindingContext, is);
            }
        } catch (MalformedURLException e) {

            // Report exception as a fault
            msg.setFaultBody(e);
            
        } catch (IOException e) {

            // Report exception as a fault
            msg.setFaultBody(e);
        }
        return msg;
    }
    
    /**
     * Write the widget response to the http response outputstream
     * @param bindingContext
     * @param is
     * @throws IOException 
     */
    private static void writeResponse(HTTPContext bindingContext, InputStream is) throws IOException {
        OutputStream os = bindingContext.getHttpResponse().getOutputStream();
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
