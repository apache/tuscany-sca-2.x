<%--
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
--%>

<%@ page import="calculator.CalculatorService" %>
<%@ page import="org.apache.tuscany.host.embedded.SCARuntime"%>
<%@ page import="org.osoa.sca.ComponentContext" %>
<%@ page import="org.osoa.sca.ServiceReference" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

   SCARuntime.start("Calculator.composite");
   
   ComponentContext context = SCARuntime.getComponentContext("CalculatorServiceComponent");
   ServiceReference<CalculatorService> service = context.createSelfReference(CalculatorService.class);
   CalculatorService calculatorService = service.getService();
%>
<html>
<head><title>Calculator sample</title></head>

<body>
<table>
    <tr>
        <th>Expression</th><th>Result</th>
    </tr>
    <tr>
        <td>2 + 3</td><td><%= calculatorService.add(2, 3) %></td>
    </tr>
    <tr>
        <td>3 - 2</td><td><%= calculatorService.subtract(3, 2) %></td>
    </tr>
    <tr>
        <td>3 * 2</td><td><%= calculatorService.multiply(3, 2) %></td>
    </tr>
    <tr>
        <td>3 / 2</td><td><%= calculatorService.divide(3, 2) %></td>
    </tr>    
</table>
</body>
</html>

<%
   SCARuntime.stop();
%>