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
package org.apache.tuscany.hessian.destination;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.tuscany.spi.wire.Wire;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.tuscany.hessian.InvocationException;
import org.apache.tuscany.hessian.TypeNotFoundException;

/**
 * A Destination implementation which receives requests over HTTP using a servlet engine
 *
 * @version $Rev$ $Date$
 */
public class HttpDestination extends AbstractDestination implements Servlet {

    public HttpDestination(Wire wire, ClassLoader loader)
        throws TypeNotFoundException {
        super(wire, loader);
    }

    public void service(ServletRequest req, ServletResponse resp)
        throws ServletException, IOException {
        try {
            invoke(new HessianInput(req.getInputStream()), new HessianOutput(resp.getOutputStream()));
        } catch (InvocationException e) {
            throw new ServletException(e);
        }
    }

    public void init(ServletConfig servletConfig) throws ServletException {

    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {

    }
}
