@echo off

if not "%TUSCANY_HOME%"=="" goto gotHome
SET TUSCANY_HOME=%~dp0\..
if not "%TUSCANY_HOME%"=="" goto gotHome
echo.
echo cannot find TUSCANY_HOME please set TUSCANY_HOME variable to the Tuscany installation dir
echo.
goto error
:gotHome


set _XDEBUG=
if not %1==debug goto skipDebug
set _XDEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
shift
:skipDebug


set _CMD_LINE_ARGS=
:argsLoop
if %1a==a goto doneInit
set _CMD_LINE_ARGS=%_CMD_LINE_ARGS% %1
shift
goto argsLoop


:doneInit

java %_XDEBUG% -jar %TUSCANY_HOME%/bin/launcher.jar %_CMD_LINE_ARGS%

goto end


:error
set ERROR_CODE=1


:end
@endlocal
exit /B %ERROR_CODE%

