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

package org.apache.tuscany.sca.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A minimal implementation of a servlet that serves documents in a document root
 * directory.
 * 
 * A servlet host implementation is not required to use this implementation and can map
 * the URI and document root to a more complete and more efficient implementation of  
 * a resource servlet, for example the Tomcat or Jetty default servlets.
 *
 * @version $Rev$ $Date$
 */
public class DefaultResourceServlet extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 2865466417329430610L;
    
    private String documentRoot;
    
    /**
     * Constructs a new ResourceServlet
     * @param documentRoot the document root
     */
    public DefaultResourceServlet(String documentRoot) {
        this.documentRoot = documentRoot;
    }
    
    /**
     * Returns the document root.
     * @return the document root
     */
    public String getDocumentRoot() {
        return documentRoot;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Determine the resource path
        String requestPath = request.getPathInfo();
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        URL url = new URL(documentRoot + '/' + requestPath);
        
        // Write the resource
        InputStream is = url.openStream();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
    }
}
