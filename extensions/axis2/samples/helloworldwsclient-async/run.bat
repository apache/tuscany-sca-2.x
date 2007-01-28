@echo off
rem set java_debug_set=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=3720,server=y,suspend=y
mkdir target\standalone
pushd target\standalone
jar -xf "%USERPROFILE%\.m2\repository\org\apache\tuscany\standalone\1.0-SNAPSHOT\standalone-1.0-SNAPSHOT-bin.zip" 
popd
rem move target\standalone\extension\axiom-api-1.0.jar target\standalone\boot
rem move target\standalone\extension\axiom-impl-1.0.jar target\standalone\boot
rem move target\standalone\extension\axis2-kernel-1.0.jar target\standalone\boot
rem move target\standalone\extension\common-2.2.1-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\commons-codec-1.3.jar target\standalone\boot
rem move target\standalone\extension\commons-httpclient-3.0.jar target\standalone\boot
rem move target\standalone\extension\ecore-2.2.1-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\ecore-change-2.2.1-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\ecore-xmi-2.2.1-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\junit-3.8.1.jar target\standalone\boot
rem move target\standalone\extension\neethi-1.0.1.jar target\standalone\boot
rem move target\standalone\extension\sdo-api-1.0-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\tuscany-sdo-impl-1.0-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\XmlSchema-1.0.2.jar target\standalone\boot
rem move target\standalone\extension\xsd-2.2.1-SNAPSHOT.jar target\standalone\boot
rem move target\standalone\extension\wstx-asl-2.9.3.jar target\standalone\boot
rem move target\standalone\extension\commons-logging-1.0.3.jar target\standalone\boot


move target\standalone\extension\axiom-api-SNAPSHOT.jar  target\standalone\boot
move target\standalone\extension\axiom-impl-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\axis2-common-SNAPSHOT.jar  target\standalone\boot
move target\standalone\extension\axis2-core-SNAPSHOT.jar target\standalone\boot 
move target\standalone\extension\woden-SNAPSHOT.jar target\standalone\boot 
move target\standalone\extension\wsdl-1.0-SNAPSHOT.jar target\standalone\boot 
move target\standalone\extension\wsdl4j-1.5.2.jar target\standalone\boot 
move target\standalone\extension\avalon-framework-4.1.3.jar target\standalone\boot
move target\standalone\extension\common-2.2.1-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\commons-codec-1.2.jar target\standalone\boot
move target\standalone\extension\commons-httpclient-3.0.jar target\standalone\boot
move target\standalone\extension\commons-logging-1.1.jar target\standalone\boot
move target\standalone\extension\ecore-2.2.1-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\ecore-change-2.2.1-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\ecore-xmi-2.2.1-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\junit-3.8.1.jar target\standalone\boot
move target\standalone\extension\log4j-1.2.12.jar target\standalone\boot
move target\standalone\extension\logkit-1.0.1.jar target\standalone\boot
move target\standalone\extension\neethi-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\sdo-api-1.0-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\servlet-api-2.3.jar target\standalone\boot
move target\standalone\extension\tuscany-sdo-impl-1.0-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\XmlSchema-SNAPSHOT.jar target\standalone\boot
move target\standalone\extension\xsd-2.2.1-SNAPSHOT.jar target\standalone\boot



java %java_debug_set% -jar target\standalone\bin\launcher.jar  --classpath "%USERPROFILE%\.m2\repository\org\apache\tuscany\samples\sca\sample-helloworldwsclient\1.0-SNAPSHOT\sample-helloworldwsclient-1.0-SNAPSHOT.jar" %*
