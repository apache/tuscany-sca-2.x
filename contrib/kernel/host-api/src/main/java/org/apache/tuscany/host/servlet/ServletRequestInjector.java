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
package org.apache.tuscany.host.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Interface to a system component that dispatches servlet requests to the Tuscany runtime
 */
public interface ServletRequestInjector {

    /**
     * Dispatch servlet requests to the Tuscany runtime
     *
     * @param req the ServletRequest object that contains the request the client made of the servlet
     * @param res the ServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if the request cannot be handled
     * @throws IOException      if an input or output error occurs while handling the request
     */
    void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;

}
