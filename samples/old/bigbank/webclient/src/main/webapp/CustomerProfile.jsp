<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%--
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
 --%>

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META http-equiv="Content-Style-Type" content="text/css">
<%-- LINK href="theme/Master.css" rel="stylesheet" type="text/css" --%>
<TITLE>BigBank- Customer Account</TITLE>
</HEAD>
<BODY><P><FONT size="+1">Customer Account</FONT><BR>
<BR>
</P>
<FORM method="post" action="FormServlet">
<input type="hidden" name="action"    value='createAccount' />
<TABLE border="0">
	<TBODY>
		<TR>
			<TD>First name </TD>
			<TD width="10%"></TD>
			<TD><INPUT type="text" name="firstName" size="20"></TD>
		</TR>
		<TR>
			<TD>Last name</TD>
			<TD></TD>
			<TD><INPUT type="text" name="lastName" size="20"></TD>
		</TR>
		<TR>
			<TD>Address</TD>
			<TD></TD>
			<TD><INPUT type="text" name="address" size="36" maxlength="170"></TD>
		</TR>
		<TR>
			<TD>email</TD>
			<TD></TD>
			<TD><INPUT type="text" name="email" size="16" maxlength="39"></TD>
		</TR>
		<TR>
			<TD>&nbsp;</TD>
			<TD></TD>
			<TD></TD>
		</TR>
		<TR>
			<TD>Checkings</TD>
			<TD></TD>
			<TD><INPUT type="checkbox" name="checkings" value="checkings" checked></TD>
		</TR>
		<TR>
			<TD>Savings</TD>
			<TD></TD>
			<TD><INPUT type="checkbox" name="savings" value="savings" checked></TD>
		</TR>
		<TR>
			<TD>&nbsp;</TD>
			<TD></TD>
			<TD></TD>
		</TR>
		<TR>
			<TD>Logon ID</TD>
			<TD></TD>
			<TD><INPUT type="text" name="loginID" size="20"></TD>
		</TR>
		<TR>
			<TD>Password</TD>
			<TD></TD>
			<TD><INPUT type="password" name="password" size="20"></TD>
		</TR>
		<TR>
			<TD></TD>
			<TD></TD>
			<TD></TD>
		</TR>
	</TBODY>
</TABLE>
<BR>
<INPUT type="submit" name="update" value="update">&nbsp;&nbsp;
<INPUT type="button" name="cancel" value="cancel"></FORM>
<P><BR>
</P>
</BODY>
</HTML>
