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
 
 <%@ page import="com.bigbank.account.AccountSummary" %>
 <%@ page import="com.bigbank.account.StockSummary" %>
 <%@ page session="true" %>
 <%@ page autoFlush="true" %>
<%@ taglib uri="/WEB-INF/bigbank-tags.tld" prefix="sca" %>
<sca:login profile="ProfileServiceComponent" url="login.html">
    <sca:service id="profile" name="ProfileServiceComponent"/>

    <html>
    <title>BigBank Account Summary</title>

    <body>

    Account Information for
    <FORM method="post" action='loginAction'>
    <jsp:getProperty name='profile' property='firstName'/>
    <jsp:getProperty name='profile' property='lastName'/>
    <input type="hidden" name="logoutHIDDEN"    value='logoutHIDDEN' />
    &nbsp;&nbsp;<INPUT type="submit" name='logout' value="logout">
    <br>
    </FORM>
    
    <table>
        <tr>
            <td><strong>Account</strong></td>
            <td>&nbsp;</td>
            <td><strong>Balance</strong></td>
        </tr>
        <sca:accountStatus accountService="AccountServiceComponent" profileService="ProfileServiceComponent" id="account">
        <tr>
			<FORM method="post" action='accountTransaction.jsp'>
			   <input type="hidden" name="account"    value='<%=((AccountSummary)pageContext.getAttribute("account")).getAccountNumber()%>' />
            <td>
                <jsp:getProperty name="account" property="accountNumber"/>
            </td>
            
            <td>
                <jsp:getProperty name="account" property="accountType"/>
            </td>
            <td>
                <jsp:getProperty name="account" property="balance"/>
            </td>
            <td>
           
            <INPUT type="submit" name='transaction' value="deposit">
            </td>
            <td>
            <INPUT type="submit" name='transaction' value="withdraw">
            </td>
			</FORM>           
        </tr>
        </sca:accountStatus>
       </table>
 
       
       
       <hr/>
       <FORM method="post" action='purchaseStock.jsp'>
       Stocks: &nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="submit" name='Purchase' value="Purchase"><br/>
       </FORM>   
       
        <table>

        <tr>
            <td><strong>Symbol</strong></td>
            <td><strong>Quantity</strong></td>
            <td><strong>Purchase Date</strong></td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td><strong>Purchase Price</strong></td>
            <td><strong>Current Price</strong></td>
            <td><strong>Company Name</strong></td>
            <td><strong>Today High</strong></td>
            <td><strong>Today Low</strong></td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td> <%-- sell button --%></td> 
        </tr>
        <sca:stockStatus id="stocksummary">
        <FORM method="post" action='stockSale.jsp' >        
        <tr>
            <td>
                <jsp:getProperty name="stocksummary" property="symbol"/>
            </td>
            <td>
                <jsp:getProperty name="stocksummary" property="quantity"/>
            </td>
            <td>
                <jsp:getProperty name="stocksummary" property="purchaseDate"/>
            </td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td>
                <jsp:getProperty name="stocksummary" property="purchasePrice"/>
            </td>
            
            <td>
                <jsp:getProperty name="stocksummary" property="currentPrice"/>
            </td>
            <td>
                <jsp:getProperty name="stocksummary" property="company"/>
            </td>
            
            <td>
                <jsp:getProperty name="stocksummary" property="highPrice"/>
            </td>
            <td>
                <jsp:getProperty name="stocksummary" property="lowPrice"/>
            </td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td>
               <INPUT type="submit" name='stocksale' value="sell"><br/>
               <input type="hidden" name="purchaseLotNumber"    value='<%=((StockSummary)pageContext.getAttribute("stocksummary")).getPurchaseLotNumber()%>' />
             </td>
        </tr>
        </FORM>   
        </sca:stockStatus>
       </table>
       
       <hr/>
       <FORM method="post" action='accountLog.jsp'>
       Account and Stock Logs: &nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="submit" name='Logs' value="Logs"><br/>
       </FORM>   

       
    </body>
    </html>
</sca:login>
