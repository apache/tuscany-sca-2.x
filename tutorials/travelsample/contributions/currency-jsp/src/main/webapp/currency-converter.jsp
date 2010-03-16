<!--
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
-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.osoa.org/sca/sca_jsp.tld" prefix="sca" %>

<sca:reference name="currencyConverter" type="com.tuscanyscatours.currencyconverter.CurrencyConverter"/>

<html>
<body>
<h2>SCA Tours Currency Converter JSP</h2>
Welcome to the SCA Tours Currency Converter:
<p>

<form method=post action="currency-converter.jsp">
Enter value in US Dollars
<input type=text name=dollars size=15>
<p>
<input type=submit>
</form>

<p>

<%
   String dollarsStr = request.getParameter( "dollars" );
   if ( dollarsStr != null) {
       double dollars = Double.parseDouble(dollarsStr);
       double converted = currencyConverter.convert("USD", "GBP", dollars);
       out.println(dollars + " US Dollars = " + converted + " GB Pounds");
   }
%>
</body>
</html>

