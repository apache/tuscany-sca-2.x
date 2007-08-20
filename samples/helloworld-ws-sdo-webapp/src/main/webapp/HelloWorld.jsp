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
<%@ page import="org.apache.tuscany.test.contribution.HelloWorld" %>
<%@ page import="org.apache.tuscany.test.contribution.HelloworldFactory"%>
<%@ page import="org.apache.tuscany.test.contribution.Party"%>
<%@ page import="org.apache.tuscany.test.contribution.Person"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    SCADomain scaDomain = (SCADomain) application.getAttribute("org.apache.tuscany.sca.SCADomain");
    HelloWorld helloWorld = scaDomain.getService(HelloWorld.class, "HelloWorldServiceComponent");
%>
<html>
<head><title>Hello World SDO sample</title></head>

<body>
<%
	Party party = HelloworldFactory.INSTANCE.createParty();
	Person person = HelloworldFactory.INSTANCE.createPerson();
	person.setFirstName("John");
	person.setLastName("Smith");
	party.getPeople().add(person);
	person = HelloworldFactory.INSTANCE.createPerson();
	person.setFirstName("Jane");
	person.setLastName("Doe");
	party.getPeople().add(person);
%>
<%= helloWorld.getGreetings(party) %>
</body>
</html>
