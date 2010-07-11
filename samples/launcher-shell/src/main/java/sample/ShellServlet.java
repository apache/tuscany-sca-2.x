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
package sample;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.host.webapp.WebAppHelper;

public class ShellServlet extends HttpServlet {
    static final long serialVersionUID = 1L;

    Shell shell;

    //@Override
    public void init() {
        shell = new Shell(WebAppHelper.getNodeFactory());
    }

    //@Override
    public void destroy() {
        shell.stop();
    }

    //@Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        shell.run(new BufferedReader(new InputStreamReader(new URL(req.getParameter("conf")).openStream())), resp.getWriter());
    }
}

