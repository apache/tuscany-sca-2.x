<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.osoa.org/sca/sca_jsp.tld" prefix="sca" %>

<sca:reference name="service" type="${package}.HelloworldService" />

<html>
  <body >

    <h2>${artifactId}</h2>

    Calling HelloworldService sayHello("world") returns:

    <p>

    <%= service.sayHello("world") %>

  </body>
</html>
