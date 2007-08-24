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

  <%@ page import="com.bigbank.account.AccountLogEntry" %>
  <%@ page import="com.bigbank.account.StockLogEntry" %>
  <%@ page session="true" %>
  <%@ page autoFlush="true" %>
  <%@ taglib uri="/WEB-INF/bigbank-tags.tld" prefix="sca" %>

    <html>
    <title>BigBank Account and Stock Log</title>

    <body>

    Account Log
    
    <table>
        <tr>
            <td><strong>Seq</strong></td>
            <td><strong>Account</strong></td>
            <td><strong>Action</strong></td>
            <td><strong>Amount</strong></td>
        </tr>
        <sca:accountLog accountService="AccountServiceComponent" profileService="ProfileServiceComponent" id="accountlogentry">
        <tr>
            <td>
                <jsp:getProperty name="accountlogentry" property="logSeqNo"/>
            </td>
            <td>
                <jsp:getProperty name="accountlogentry" property="accountNumber"/>
            </td>
            <td>
                <jsp:getProperty name="accountlogentry" property="actionType"/>
            </td>
            <td>
                <jsp:getProperty name="accountlogentry" property="amount"/>
            </td>
        </tr>
        </sca:accountLog>
       </table>
 
       
       
       <hr/>
       Stock Log
       
        <table>

        <tr>
            <td><strong>Seq</strong></td>
            <td><strong>Symbol</strong></td>
            <td><strong>Quantity</strong></td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td><strong>Action</strong></td>
            <td><strong>PurchaseLotNumber</strong></td>
        </tr>
        <sca:stockLog id="stocklogentry">
        <tr>
            <td>
                <jsp:getProperty name="stocklogentry" property="logSeqNo"/>
            </td>
            <td>
                <jsp:getProperty name="stocklogentry" property="symbol"/>
            </td>
            <td>
                <jsp:getProperty name="stocklogentry" property="quantity"/>
            </td>
            <td>&nbsp;&nbsp;</td>  <%-- spacer --%>
            <td>
                <jsp:getProperty name="stocklogentry" property="actionType"/>
            </td>
            
            <td>
                <jsp:getProperty name="stocklogentry" property="purchaseLotNumber"/>
            </td>
        </tr>
        </FORM>   
        </sca:stockLog>
       </table>

       
    </body>
    </html>
