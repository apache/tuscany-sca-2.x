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
<%@ page import="org.apache.tuscany.sca.war.Installer" %>

<%
    Installer installer = (Installer) request.getAttribute("installer");
%>

<html>
  <body >

    <h2>Apache Tuscany Tomcat Integration</h2>

    The Tuscany Tomcat integration turns Tomcat into an SCA enabled runtime so it can run SCA contributions and SCA-enabled Web Applications.
    <p>

    Status: Tuscany is <B>
       <% if (Installer.isTuscanyHookRunning()) { %>
          installed and active
       <% } else if (Installer.isRestartRequired()) {%>
          installed but Tomcat needs to be restarted
       <% } else {%>
          not installed
       <% }%>
    </B> 
    in Tomcat.
    <p>    

    <% if (!Installer.isTuscanyHookRunning() && !Installer.isRestartRequired()) { %>
       <B>Install Tuscany</B><BR>
       To install Tuscany into Tomcat, click:
                <form action='installer' method='post'>
                <input type='submit' name='action' value='Install'>
                </form>
       <BR>
   <% } else {%>
       <B>Uninstall Tuscany</B><BR>
       If remove Tuscany from Tomcat, click:
                <form action='installer' method='post'>
                <input type='submit' name='action' value='Uninstall'>
                </form>
       <BR>
   <% }%>

    <p>
    <BR>

    <B> 
    <% if (installer != null) { %>
    <%=    installer.getStatus()  %>
    <% }%>
    </B> 

  </body>
</html>
