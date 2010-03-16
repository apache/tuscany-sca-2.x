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
package com.tuscanyscatours.currencyconverter.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Reference;

import com.tuscanyscatours.currencyconverter.CurrencyConverter;

public class CurrencyConverterServlet extends HttpServlet {

    @Reference
    protected CurrencyConverter currencyConverter;

    @Override
    public void init(ServletConfig config) {
        if (currencyConverter == null) {
            // The Currency Converter reference will only be injected from the @Reference 
            // annotation in containers supporting SCA "deep" integration. In other 
            // environments in can be looked up from the ComponentContext.
            ComponentContext context =
                (ComponentContext)config.getServletContext().getAttribute("org.osoa.sca.ComponentContext");
            currencyConverter = context.getService(CurrencyConverter.class, "currencyConverter");
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Writer out = response.getWriter();
        out.write("<html><body><h2>SCA Tours Currency Converter Servlet</h2>");
        out.write("Welcome to the SCA Tours Currency Converter Servlet<p>");
        out.write("<form method=post action=\"CurrencyConverterServlet\">");
        out.write("Enter value in US Dollars");
        out.write("<input type=text name=dollars size=15><p>");
        out.write("<input type=submit>");
        out.write("</form><p>");

        String dollarsStr = request.getParameter("dollars");
        if (dollarsStr != null) {
            double dollars = Double.parseDouble(dollarsStr);
            double converted = currencyConverter.convert("USD", "GBP", dollars);
            out.write(dollars + " US Dollars = " + converted + " GB Pounds");
        }

        out.write("</body></html>");
        out.flush();
        out.close();
    }
}
