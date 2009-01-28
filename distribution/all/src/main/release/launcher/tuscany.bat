@echo off

REM bla

java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y -jar launcher.jar %1 %2 %3 %4 %5 %6 %7
REM java -jar launcher.jar %1 %2 %3 %4 %5 %6 %7
