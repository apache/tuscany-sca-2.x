@echo off

if "%1"=="/?" goto help

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

set _FORK=
if not %1==fork goto skipFork
set _FORK=start
shift
:skipFORK

set _CMD_LINE_ARGS=
:argsLoop
if %1a==a goto doneInit
set _CMD_LINE_ARGS=%_CMD_LINE_ARGS% %1
shift
goto argsLoop


:doneInit

%_FORK% java %_XDEBUG% -jar %TUSCANY_HOME%/bin/launcher.jar %_CMD_LINE_ARGS%

goto end

:help

echo Apache Tuscany SCA runtime launcher
echo TUSCANY [debug] [fork] contributions
echo     debug          enable Java remote debugging
echo     fork           start a new command prompt window to run the contributions
echo     contributions  list of SCA contribution file names seperated by spaces. All
echo                    deployable composites found in the contributions will be run.

goto end

:error
set ERROR_CODE=1


:end
@endlocal
exit /B %ERROR_CODE%

