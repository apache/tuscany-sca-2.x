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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import bigbank.webclient.services.profile.ProfileService;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.AccountService;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

public class FormServlet extends HttpServlet {

    // private ServletContext mContext;
    // public void init(ServletConfig pCfg) throws ServletException {
    // mContext = pCfg.getServletContext();
    // }

    @Override
    public void doPost(HttpServletRequest pReq, HttpServletResponse pResp) throws ServletException {

        try {
            final String action = pReq.getParameter("action");

            CompositeContext moduleContext = CurrentCompositeContext.getContext();
            AccountService accountServices = (AccountService) moduleContext.locateService(AccountService.class, "AccountServiceComponent");
            if (accountServices == null) {
                throw new ServletException("AccountServiceComponent");
            }
            ProfileService profileServices = null;
            if (!"createAccount".equals(action)) {
                profileServices = moduleContext.locateService(ProfileService.class, "ProfileServiceComponent");
                if (profileServices == null) {
                    throw new ServletException("ProfileServiceComponent not found.");
                }
                if (!profileServices.isLoggedIn()) {
                    throw new ServletException("User id '" + profileServices.getId() + "' not logged on.");
                }
            }

            if ("createAccount".equals(action)) {
                createAccount(pReq, pResp, accountServices);
            } else if ("account".equals(action)) {
                accountTransaction(pReq, pResp, accountServices);
            } else if ("stockPurchase".equals(action)) {
                stockPurchase(pReq, pResp, profileServices, accountServices);
            } else if ("stockSale".equals(action)) {
                stockSale(pReq, pResp, profileServices, accountServices);
            } else {
                throw new IllegalArgumentException("Unknown action in Form servlet '" + action + "'.");
            }
            // mContext.getRequestDispatcher("summary.jsp").forward(pReq, pResp);
            pResp.sendRedirect("summary.jsp");
        } catch (ServletException e) {
            e.printStackTrace();
            throw e;

        } catch (Exception e) {

            throw new ServletException(e);
        }

    }

    private void stockSale(HttpServletRequest req, HttpServletResponse resp, ProfileService profileServices, AccountService accountServices)
            throws ServletException {
        try {
            if (!"cancel".equals(req.getParameter("cancel"))) {

                int quantity = Integer.parseInt(req.getParameter("quantity"));
                int purchaseLotNumber = Integer.parseInt(req.getParameter("purchaseLotNumber"));
                accountServices.sellStock(purchaseLotNumber, quantity);
            }

        } catch (Exception e) {

            throw new ServletException("stockSale " + e.getMessage(), e);
        }

    }

    private void stockPurchase(HttpServletRequest req, HttpServletResponse resp, ProfileService profileServices, AccountService accountServices)
            throws ServletException {
        try {
            if (!"cancel".equals(req.getParameter("cancel"))) {

                String symbol = req.getParameter("symbol").trim().toUpperCase();
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                StockSummary stockSummry = AccountFactory.INSTANCE.createStockSummary();
                stockSummry.setSymbol(symbol);
                stockSummry.setQuantity(quantity);
                accountServices.purchaseStock(profileServices.getId(), stockSummry);
            }
        } catch (Exception e) {
            throw new ServletException("stockPurchase " + e.getMessage(), e);
        }
    }

    private void accountTransaction(HttpServletRequest req, HttpServletResponse resp, AccountService accountServices) throws ServletException {
        try {
            if (!"cancel".equals(req.getParameter("cancel"))) {
                String account = req.getParameter("account");
                String amount = req.getParameter("Amount");
                if ("deposit".equals(req.getParameter("actionType"))) {
                    accountServices.deposit(account, Float.parseFloat(amount));
                } else {
                    accountServices.withdraw(account, Float.parseFloat(amount));
                }
            }
        } catch (Exception e) {
            throw new ServletException("accountTransaction " + e.getMessage(), e);
        }

    }

    private void createAccount(HttpServletRequest pReq, HttpServletResponse pResp, AccountService accountServices) throws ServletException {
        try {
            CustomerProfileData customerProfileData = AccountFactory.INSTANCE.createCustomerProfileData();
            customerProfileData.setFirstName(pReq.getParameter("firstName"));
            customerProfileData.setLastName(pReq.getParameter("lastName"));
            customerProfileData.setAddress(pReq.getParameter("address"));
            customerProfileData.setEmail(pReq.getParameter("email"));
            customerProfileData.setLoginID(pReq.getParameter("loginID"));
            customerProfileData.setPassword(pReq.getParameter("password"));

            CustomerProfileData resp = accountServices.createAccount(customerProfileData, "savings".equals(pReq.getParameter("savings")), "checkings"
                    .equals(pReq.getParameter("checkings")));
            LoginServlet.login(resp.getLoginID(), resp.getPassword());

        } catch (IOException e) {
            throw new ServletException(e);
        }

    }
}
