@echo off
setlocal
rem set JDEBUG=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=3720,server=y,suspend=y
copy "%USERPROFILE%\.m2\repository\org\apache\tuscany\core\1.0-sandbox-SNAPSHOT\core-1.0-sandbox-SNAPSHOT.jar" lib  1>NUL
copy "%USERPROFILE%\.m2\repository\org\apache\tuscany\spi\1.0-sandbox-SNAPSHOT\spi-1.0-sandbox-SNAPSHOT.jar" lib 1>NUL
copy "%USERPROFILE%\.m2\repository\org\apache\tuscany\launcher\1.0-sandbox-SNAPSHOT\launcher-1.0-sandbox-SNAPSHOT.jar" lib 1>NUL
copy "%USERPROFILE%\.m2\repository\stax\stax-api\1.0\stax-api-1.0.jar" lib 1>NUL
copy "%USERPROFILE%\.m2\repository\woodstox\wstx-asl\2.9.3\wstx-asl-2.9.3.jar" lib 1>NUL
copy "%USERPROFILE%\.m2\repository\org\osoa\sca-api\1.0-sandbox-r0.92-SNAPSHOT\sca-api-1.0-sandbox-r0.92-SNAPSHOT.jar" lib 1>NUL
rem java %JDEBUG% -cp lib\launcher-1.0-sandbox-SNAPSHOT.jar;lib/sca-api-1.0-sandbox-SNAPSHOT.jar org.apache.tuscany.launcher.MainLauncherBooter  --classpath "%USERPROFILE%\.m2\repository\org\apache\tuscany\samples\sca\sample-eagerinit\1.0-sandbox-SNAPSHOT\sample-eagerinit-1.0-sandbox-SNAPSHOT.jar"  --main  eagerinit.EagerInitClient
java %JDEBUG% -jar lib\launcher-1.0-sandbox-SNAPSHOT.jar  --classpath "%USERPROFILE%\.m2\repository\org\apache\tuscany\samples\sca\sample-eagerinit\1.0-sandbox-SNAPSHOT\sample-eagerinit-1.0-sandbox-SNAPSHOT.jar"  --main  eagerinit.EagerInitClient
endlocal
