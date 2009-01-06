This README describes the JAX-WS client files for the SCA Assembly test invocation interface.

The test invocation interface is in the TestInvocation.java interface file.
This is presented as a Web service by the SCA runtime, for invocation by a general Web service client.
The WSDL is captured in the file TestClient.wsdl in the resources directory for the testcases.

The JAX-WS client files for invoking the Web service contained in TestClient.wsdl are contained in this
package.  They are generated from the WSDL using the wsimport tool (a standard part of JDK 1.6.x):

wsimport TestClient.wsdl -keep -p testClient

"-keep" is used to ensure that the generated .java files are retained (otherwise all that is created are 
the binary .class files).

"-p testClient" is used to force the generated Java files to belong to the package "testClient" - this package
name is used to keep a clean separation between the client files and the test service files


Mike Edwards, 6th January 2009.