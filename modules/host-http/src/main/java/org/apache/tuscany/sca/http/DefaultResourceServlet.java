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


/**
 * A minimal implementation of a servlet that serves documents in a document root
 * directory.
 * 
 * A servlet host implementation is not required to use this implementation and can map
 * the URI and document root to a more complete and more efficient implementation of  
 * a resource servlet, for example the Tomcat or Jetty default servlets.
 * 
 * @deprecated use org.apache.tuscany.sca.host.http
 *
 * @version $Rev$ $Date$
 */
@Deprecated
public class DefaultResourceServlet extends org.apache.tuscany.sca.host.http.DefaultResourceServlet {
    private static final long serialVersionUID = 4118826069821911041L;

    /**
     * Constructs a new ResourceServlet
     * @param documentRoot the document root
     */
    public DefaultResourceServlet(String documentRoot) {
        super(documentRoot);
    }
    
}
