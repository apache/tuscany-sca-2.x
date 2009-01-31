 # Licensed to the Apache Software Foundation (ASF) under one
 # or more contributor license agreements.  See the NOTICE file
 # distributed with this work for additional information
 # regarding copyright ownership.  The ASF licenses this file
 # to you under the Apache License, Version 2.0 (the
 # "License"); you may not use this file except in compliance
 # with the License.  You may obtain a copy of the License at
 #
 #   http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing,
 # software distributed under the License is distributed on an
 # "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 # KIND, either express or implied.  See the License for the
 # specific language governing permissions and limitations
 # under the License.


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
