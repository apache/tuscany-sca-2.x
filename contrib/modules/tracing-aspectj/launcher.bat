@echo off
set TUSCANY_HOME=C:\Apache\tuscany-sca-1.3
set ASPECTJ_WEAVER=%HOMEPATH%\.m2\repository\org\aspectj\aspectjweaver\1.6.1\aspectjweaver-1.6.1.jar
set CP=%ASPECTJ_WEAVER%;%TUSCANY_HOME%\lib\tuscany-sca-manifest.jar;%TUSCANY_HOME%\samples\calculator\target\sample-calculator.jar
java -javaagent:"%ASPECTJ_WEAVER%" -cp "%CP%;target\classes" calculator.CalculatorClient