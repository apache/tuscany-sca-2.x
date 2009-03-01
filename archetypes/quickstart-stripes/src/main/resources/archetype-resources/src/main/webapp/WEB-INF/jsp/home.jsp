#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<s:layout-render name="/WEB-INF/jsp/layout.jsp" title="Welcome">
  <s:layout-component name="body">
    <p>sayHello returns: ${symbol_dollar}{actionBean.hello}</p>
    <p>Congratulations, you've set up a Stripes SCA project!</p>
  </s:layout-component>
</s:layout-render>
