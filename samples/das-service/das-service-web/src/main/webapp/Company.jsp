<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 -->

<%@ page import="java.util.*" %>

<%@ page import="org.apache.tuscany.host.embedded.SCADomain"%>

<%@ page import="commonj.sdo.*" %>
<%@ page import="das.*" %>

<html>
<head>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%

   SCADomain domain = (SCADomain) application.getAttribute("org.apache.tuscany.sca.SCADomain"); 
   if (domain == null) {
    System.out.println("domain == NULL");
   }


   DASService dasService = domain.getService(DASService.class, "DASServiceComponent");
   
   if (dasService == null) {
       System.out.println("DASService == NULL");
   }

   List companyList = null;

   try{
		dasService.configureService(getClass().getClassLoader().getResourceAsStream("CompanyConfig.xml"));
		DataObject root = dasService.executeCommand("all companies", null);
   	    companyList = root.getList("COMPANY");
   }catch(Exception e){
       //TODO: handle case where dasService can't be initiated properly
   }

%>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DASService Client Test</title>
</head>
<body>

<H2>Tuscany DAS Service WEB Client Application Example</H2>

<!-- Do Fill -->
<table border>
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
		</tr>
	</thead>
	<tbody>

		<%
			java.util.Iterator i = companyList.iterator();
			while (i.hasNext()) {
				DataObject company = (DataObject)i.next();
			%>
				<tr>
					<td><%=company.getInt("ID")%></td>
					<td><%=company.getString("NAME")%></td>
				<tr>
		<%	
		}
		%>
	</tbody>
</table>



</form>
</body>
</html>
