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


    <p>
    <h2>What does this do?</h2>

     This Tuscany install makes two updates to Tomcat:
     <UL>
       <LI>copies the jars from the tomcat-lib directory in this webapp into the Tomcat lib directory 
       <LI>updates the Tomcat conf/server.xml to include a &lt;Listener&gt; definition for Tuscany
     </UL>
     Those changes cause the Tuscany listener to be called at Tomcat startup and that locates all the Host
     defiinitions and patches them to use the TuscanyStandardContext. This enables support for deploying
     SCA enabled webapps and plain SCA jar, zip, or folder contributions to the Host in the same way 
     that .war files are deployed. 
    <p>
    The SCA domain used for running the contributions defaults to "vm:default". This can be configured 
    by using an initilization parameter, most easiliy by defining that parameter in a context.xml file.
    The Tomcat file conf/context.xml file defines the global defaults so that can be used for setting the 
    deafult domain for all SCA contributions. Individual contributions can also use their own context.xml
    files to override that default. 
    See the <a href="http://tomcat.apache.org/tomcat-6.0-doc/config/context.html">Tomcat doc</a> for more information on using context.xml files. 
    <p>
    An example of setting the domain as a context.xml parameter:
       <br>&lt;Context&gt;
          <br>. . .
          <br>&lt;Parameter name="org.apache.tuscany.sca.defaultDomainURI" value="tribes:myDomain" override="false"/&gt;
          <br>. . .
       <br>&lt;/Context&gt;
    <p>
    For more information visit the Tuscany website page on <a href="http://tuscany.apache.org/tuscany-tomcat-distribution.html">Tomcat Integration</a>.
    <p>
    Note also that this is work in progress so is liable to change as Tuscany 2.0 is developed. Feedback is welcome and appreciated so if you've any comments or requests on this Tomcat integration please email <href="mailto:dev@tuscany.apache.org">dev@tuscany.apache.org</a>.
    <p>

  </body>
</html>
