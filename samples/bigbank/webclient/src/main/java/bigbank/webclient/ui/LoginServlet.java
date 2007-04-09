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
package bigbank.webclient.ui;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import bigbank.webclient.services.profile.LoginService;

public class LoginServlet extends HttpServlet {

    @Override
    public void init(ServletConfig pCfg) throws ServletException {

    }

    @Override
    public void doPost(HttpServletRequest pReq, HttpServletResponse pResp) throws ServletException {

        if ("logout".equals(pReq.getParameter("logout")) || "logoutHIDDEN".equals(pReq.getParameter("logoutHIDDEN"))) {
            HttpSession sess = pReq.getSession();
            if (sess != null) {
                sess.invalidate();
            }
            try {
                pResp.sendRedirect("login.html");
            } catch (IOException e) {

                e.printStackTrace();
                throw new ServletException(e);
            }

        } else {
            pReq.getSession(); // make sure session started.
            String login = pReq.getParameter("login");
            String password = pReq.getParameter("password");
            try {
                int resp = login(login, password);
                if (resp == LoginService.SUCCESS) {

                    pResp.sendRedirect("summary.jsp");
                } else {

                    pResp.sendRedirect("login.html");
                }
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }

    static int login(final String login, final String password) throws ServletException {

        CompositeContext moduleContext = CurrentCompositeContext.getContext();
        LoginService loginMgr = moduleContext.locateService(LoginService.class, "LoginServiceComponent");

        if (loginMgr == null) {
            throw new ServletException("LoginManager not found");
        }

        try {
            return loginMgr.login(login, password);
        } catch (RemoteException e) {

            throw new ServletException(e);
        }

    }

}
