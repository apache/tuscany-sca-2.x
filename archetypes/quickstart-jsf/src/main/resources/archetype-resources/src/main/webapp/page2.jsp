<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
    <head>
        <title>Hello World</title>
    </head>
    <body>
        <f:view>
            <h:form id="mainForm">
                <h2><h:outputText value="#{helloWorld.name}. We hope you enjoy SCA and JSF with Apache Tuscany and MyFaces"/></h2>
                <h:commandLink action="back">
                    <h:outputText value="Home"/>
                </h:commandLink>
            </h:form>
        </f:view>
    </body>
</html>