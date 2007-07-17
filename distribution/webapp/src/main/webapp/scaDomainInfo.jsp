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
<%@ page import="org.apache.tuscany.sca.host.embedded.management.ComponentManager"%>
<%@ page import="org.apache.tuscany.sca.assembly.ComponentService"%>
<%@ page import="org.apache.tuscany.sca.assembly.Binding"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
   SCADomain scaDomain = (SCADomain) application.getAttribute("org.apache.tuscany.sca.SCADomain");
   ComponentManager componentManager = scaDomain.getComponentManager();
%>
<html>
<head><title>Apache Tuscany WebApp Runtime</title></head>

<body>
Apache Tuscany WebApp Runtime
<br>
Components in SCA Domain:
   <%
        java.util.Iterator i = componentManager.getComponentNames().iterator();
        while (i.hasNext()) {
            String compName = i.next().toString();

      	%><br><%=compName%><br><%

            org.apache.tuscany.sca.assembly.Component comp = componentManager.getComponent(compName);
            java.util.Iterator j = comp.getServices().iterator();
            while (j.hasNext()) {
                ComponentService compService = (ComponentService)j.next();

                %><%=" - Service: " + compService.getName()%><br><%

                java.util.Iterator k = compService.getBindings().iterator();
                while (k.hasNext()) {
                    Binding b = (Binding)k.next();
                    String bindingType = b.getClass().getName();

                    %><%="-- Binding: " + b.getName() + "(" + bindingType.substring(bindingType.lastIndexOf('.')+1) + ") URI: " + b.getURI()%><br><%
                }
            }
        }
   %>
<br>

</body>
</html>

