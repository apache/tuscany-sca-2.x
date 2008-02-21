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

<%@ page import="org.apache.tuscany.sca.host.embedded.SCADomain"%>
<%@ page import="helloworld.HelloWorldService" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
   SCADomain scaDomain = (SCADomain) application.getAttribute("org.apache.tuscany.sca.SCADomain");
   HelloWorldService helloWorldService = (HelloWorldService)scaDomain.getService(HelloWorldService.class, "HelloWorldClient");
%>
<html>
<head><title>HelloWorld JMS sample</title></head>

<body>

If this sample is working correctly you should see "Hello World" on the next line...
<p>
<%= helloWorldService.sayHello("world") %>
<p>
If you do not see "Hello World" on the line above then there has been a problem.
<p>
The sample requires JMS resources be manually configured in the server environment, these are:
a JMS connection factory named "ConnectionFactory", and a destination queues named "HelloWorldService".
See the sample README file for more information.

</body>
</html>
