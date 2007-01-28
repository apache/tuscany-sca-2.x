<%@ page import="org.osoa.sca.CompositeContext" %>
<%@ page import="org.osoa.sca.CurrentCompositeContext" %>
<%@ page import="sample.TestBean" %>
<%@ page import="sample.TestComponent"%>
<%--

Demonstrates accessing the current application context
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Tuscany Spring sample</title></head>

<body>
<h1>A Sample Spring application</h1>
<%
    CompositeContext context = CurrentCompositeContext.getContext();
    // locates a bean in the Spring context

    TestBean testBean = context.locateService(TestBean.class, "testBean");%>
<p>Client side bean: <b><%= testBean.getName() %></b></p>
<p>Injected server component: <b><%= testBean.getTestComponent().getName() %></b></p>
<p>Invoke of server component echo: <b><%= testBean.getTestComponent().echo("hello") %></b></p>
</body>
</html>