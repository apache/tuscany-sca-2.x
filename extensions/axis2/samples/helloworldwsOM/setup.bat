@echo off
pushd target
jar -xf  ..\apache-tomcat-5.5.17.zip 
popd
copy target\sample-helloworldws-1.0-SNAPSHOT.war target\apache-tomcat-5.5.17\webapps
rem pushd target\apache-tomcat-5.5.17\webapps
rem md sample-helloworldws-1.0-SNAPSHOT 
rem cd sample-helloworldws-1.0-SNAPSHOT
rem jar -xf ..\sample-helloworldws-1.0-SNAPSHOT.war
pushd target\apache-tomcat-5.5.17\shared
rem jar -xf  "%USERPROFILE%\.m2\repository\org\apache\tuscany\web\1.0-SNAPSHOT\web-1.0-SNAPSHOT-bin.zip"
jar -xf  "%USERPROFILE%\.m2\repository\org\apache\tuscany\web\1.0-SNAPSHOT\web-1.0-SNAPSHOT-bin.zip"
popd
rem hack
mkdir target\apache-tomcat-5.5.17\shared\extension
copy target\apache-tomcat-5.5.17\shared\lib\axis2-1.0-SNAPSHOT.jar target\apache-tomcat-5.5.17\shared\extension 
