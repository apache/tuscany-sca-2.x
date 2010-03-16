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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://www.osoa.org/sca/sca_jsp.tld" prefix="sca"%>

<sca:reference name="service"
	type="com.tuscanyscatours.payment.creditcard.CreditCardPayment" />


<%@page import="com.tuscanyscatours.payment.creditcard.CreditCardPaymentService"%>
<%@page import="com.tuscanyscatours.payment.creditcard.ObjectFactory"%>
<%@page import="com.tuscanyscatours.payment.creditcard.CreditCardDetailsType"%>
<%@page import="com.tuscanyscatours.payment.creditcard.CreditCardTypeType"%>
<%@page import="com.tuscanyscatours.payment.creditcard.PayerType"%><html>
<body>

<h2>SCATours Credit Card Payment Web Application</h2>

<p>
<form action="creditcard.jsp" method="get">Card Type: <select
	name="type">
	<option value="Visa" selected="selected">Visa</option>
	<option value="MasterCard">MasterCard</option>
	<option value="Amex">Amex</option>
	<option value="Discover">Discover</option>
</select> <br />
Card Number: <input type="text" name="cardNumber" /><br />
Card Holder: <input type="text" name="cardHolder" /><br />
Amount (EUR): <input type="text" name="amount" /><br />
<p>
<input type="submit" name="charge" value="Submit" /></form>

<%
    String c = request.getParameter("charge");
    if (c != null) {
        ObjectFactory objectFactory = new ObjectFactory();
        CreditCardDetailsType ccDetails = objectFactory.createCreditCardDetailsType();
        ccDetails.setCreditCardType(CreditCardTypeType.fromValue(request.getParameter("type")));
        ccDetails.setCreditCardNumber(request.getParameter("cardNumber"));
        ccDetails.setCVV2("123");
        ccDetails.setExpMonth(12);
        ccDetails.setExpYear(2011);
        PayerType ccOwner = objectFactory.createPayerType();
        ccOwner.setName(request.getParameter("cardHolder"));
        ccDetails.setCardOwner(ccOwner);
        float amount = Float.parseFloat(request.getParameter("amount"));
%>
<%=service.authorize(ccDetails, amount)%>
<%
    }
%>

</body>
</html>
