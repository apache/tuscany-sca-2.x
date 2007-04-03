<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%--
 * SCATestService.java
 * written by Chris Ortiz
 * interface class for basic test service  
 * version .1     9/22/2006
 *
 *
 --%>


<HTML>
<HEAD>
    <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
             pageEncoding="ISO-8859-1" session="true" autoFlush="true"
             isThreadSafe="true" isErrorPage="false"
             import="org.apache.tuscany.sca.itest.SCATestToolService"
             import="java.io.PrintWriter"
             import="java.io.StringWriter"
             import="org.osoa.sca.CurrentCompositeContext"
             import="org.osoa.sca.CompositeContext"
            %>
    <META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <META name="GENERATOR" content="IBM Software Development Platform">
    <TITLE>SCA FVT Test Service Driver</TITLE>
</HEAD>

<BODY bgcolor="#c8d8f8">
<H2>SCA FVT Test Tool Client</H2>

<form action="SCATestTool.jsp" method="get"><HR><BR>Choose Binding type:<BR><BR>
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="radio" name="bind" value="WS"> WS Binding
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="radio" name="bind" value="EJB"> EJB Binding
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="radio" name="bind" value="SCA" checked> Default Binding<BR><BR><HR>
        <BR>Check Tests to run:<BR><BR>
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" name="test" value="Ping First Composite" checked> Ping First Composite
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" name="test" value="Ping Second Composite"> Ping Second Composite<BR><BR>
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" name="test" value="Data Type Test"> Data Type Test<BR><BR>
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" name="test" value="Async One Way"> Async One Way&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" name="test" value="Async Callback"> Async Call Back<BR><BR>


    <BR>
    <INPUT type="submit" value="Run Test"></FORM>
<BR>

    <%

        String bindingchoice = request.getParameter("bind");
        String[] selected = request.getParameterValues("test");
       
       try {
           SCATestToolService scaTestTool;
           if (bindingchoice.equals("WS")) {
               CompositeContext compositeContext = CurrentCompositeContext.getContext();
               scaTestTool = (SCATestToolService) compositeContext.locateService(SCATestToolService.class, "SCATestToolWSReference");
           } else if (bindingchoice.equals("EJB")) {
               CompositeContext compositeContext = CurrentCompositeContext.getContext();
               scaTestTool = (SCATestToolService) compositeContext.locateService(SCATestToolService.class, "SCATestToolEJBReference");
           } else {
             // assume default binding
               CompositeContext compositeContext = CurrentCompositeContext.getContext();
               scaTestTool = (SCATestToolService) compositeContext.locateService(SCATestToolService.class, "SCATestToolSCAReference");
           }
           if (selected != null && selected.length != 0) {
               for (int i = 0; i < selected.length; i++) {
                   String value = null;
        	   if (null != selected[i] && selected[i].equals("Ping First Composite")) {
                       value = scaTestTool.doOneHopPing("brio");
                   } else if (null != selected[i] && selected[i].equals("Ping Second Composite")){
                       value = scaTestTool.doTwoHopPing("brio");
                   } else if (null != selected[i] && selected[i].equals("Data Type Test")){
                       value = scaTestTool.doDataTypeTest("brio");
                   } else if (null != selected[i] && selected[i].equals("Async One Way")){
                       value = "Ut Oh! Test tool not complete for oneway testing";
                   } else if (null != selected[i] && selected[i].equals("Async Callback")){
                       value = "Ut Oh! Test tool not complete for async testing";
                   } else {
                       value = "Ut Oh! unknown test";
                   }

%>

<HR>
Results from <%=selected[i]%> test with <%=bindingchoice%> binding:
<P><%=value%>
    <%
               } //for
           } //if

       }catch(Exception e){
                  e.printStackTrace();
                  StringWriter sw= new StringWriter();
				  PrintWriter pw= new PrintWriter(sw);

				  e.printStackTrace(pw);
				  pw.flush();
				
%>
    Whoops!<BR clear="all">
<PRE>
    Exception &quot;<%=e.getClass().getName()%>&quot; Exception message: &quot;<%=e.getMessage()%>&quot;<BR clear="all">
    <%=sw.toString() %>
</PRE>
<%        }   //catch
  
%>

</BODY>
</HTML>