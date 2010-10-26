@echo off

REM # Licensed to the Apache Software Foundation (ASF) under one
REM # or more contributor license agreements.  See the NOTICE file
REM # distributed with this work for additional information
REM # regarding copyright ownership.  The ASF licenses this file
REM # to you under the Apache License, Version 2.0 (the
REM # "License"); you may not use this file except in compliance
REM # with the License.  You may obtain a copy of the License at
REM # 
REM #   http://www.apache.org/licenses/LICENSE-2.0
REM # 
REM # Unless required by applicable law or agreed to in writing,
REM # software distributed under the License is distributed on an
REM # "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM # KIND, either express or implied.  See the License for the
REM # specific language governing permissions and limitations
REM # under the License. 

REM TODO: can't get these to work yet when using the tribes domaim URI in quotes
if "%1".=="/?". goto help
if "%1".=="-help". goto help
if "%1".=="-help". goto help

if not "%TUSCANY_HOME%"=="" goto gotHome
SET TUSCANY_HOME=%~dp0\..
if not "%TUSCANY_HOME%"=="" goto gotHome
echo.
echo cannot find TUSCANY_HOME please set TUSCANY_HOME variable to the Tuscany installation dir
echo.
goto error
:gotHome


set _XDEBUG=
if not "%1"=="debug" goto skipDebug
set _XDEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
shift
:skipDebug

set _FORK=
if not "%1"=="fork" goto skipFork
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
echo TUSCANY [debug] [fork] [domainURI] contributions
echo     debug          enable Java remote debugging
echo     fork           start a new command prompt window to run the contributions
echo     domainURI      config URI for the domain, the format is:
echo                        vm:domainName
echo                    or
echo                        "tribes:domainName?routes=ip1,ip2,..."
echo                    NOTE that the tribes URI needs to be in quotes
echo     contributions  list of SCA contribution file names seperated by spaces. All
echo                    deployable composites found in the contributions will be run.

goto end

:error
set ERROR_CODE=1


:end
@endlocal
exit /B %ERROR_CODE%

