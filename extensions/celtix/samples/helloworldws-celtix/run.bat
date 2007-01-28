echo on
rem set java_debug_set=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=3720,server=y,suspend=y
mkdir target\standalone
pushd target\standalone
jar -xf "%USERPROFILE%\.m2\repository\org\apache\tuscany\standalone\1.0-SNAPSHOT\standalone-1.0-SNAPSHOT-bin.zip" 
jar -xf "%USERPROFILE%\.m2\repository\org\apache\tuscany\bindings\celtix\1.0-SNAPSHOT\celtix-1.0-SNAPSHOT-bin.zip" 
popd
move target\standalone\extension\*.jar target\standalone\boot
copy "%USERPROFILE%\.m2\repository\org\apache\tuscany\bindings\celtix\1.0-SNAPSHOT\celtix-1.0-SNAPSHOT.jar" target\standalone\extension
java %java_debug_set% -jar target\standalone\bin\launcher.jar  --classpath "%USERPROFILE%\.m2\repository\org\apache\tuscany\samples\sca\sample-helloworldws-celtix\1.0-SNAPSHOT\sample-helloworldws-celtix-1.0-SNAPSHOT.jar" %*
