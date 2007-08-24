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
<TITLE>BigBank- Stock purchase</TITLE>
</HEAD>
<BODY><P><FONT size="+1">Stock purchase</FONT><BR>
<BR>
</P>
<FORM method="post" action="FormServlet">
<input type="hidden" name="action"  value='stockPurchase' />
<TABLE border="0">
	<TBODY>
		<TR>
			<TD>Symbol </TD>
			<TD width="10%"></TD>
			<TD><INPUT type="text" name="symbol" size="6"></TD>
		</TR>
		<TR>
			<TD>Quantity</TD>
			<TD></TD>
			<TD><INPUT type="text" name="quantity" size="6"></TD>
		</TR>
		<TR>
			<TD></TD>
			<TD></TD>
			<TD></TD>
		</TR>
	</TBODY>
</TABLE>
<BR>
<INPUT type="submit" name="purchase" value="purchase">&nbsp;&nbsp;
<INPUT type="submit" name="cancel" value="cancel"></FORM>
<P><BR>
</P>
</BODY>
</HTML>
