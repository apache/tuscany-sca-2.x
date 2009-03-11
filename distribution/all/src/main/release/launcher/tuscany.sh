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

#!/bin/bash

# copied from tomcat catalina.sh
# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
 ls=`ls -ld "$PRG"`
 link=`expr "$ls" : '.*-> \(.*\)$'`
 if expr "$link" : '/.*' > /dev/null; then
   PRG="$link"
 else
   PRG=`dirname "$PRG"`/"$link"
 fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set CATALINA_HOME if not already set
[ -z "$TUSCANY_HOME" ] && TUSCANY_HOME=`cd "$PRGDIR/.." ; pwd`

if [ "$1" = "/?" ] ; then
   echo "Apache Tuscany SCA runtime launcher"
   echo "TUSCANY [debug] contributions"
   echo "    debug          enable Java remote debugging"
   echo "    contributions  list of SCA contribution file names seperated by spaces. All"
   echo "                   deployable composites found in the contributions will be run."
   exit 1
fi

_XDEBUG=""
if [ "$1" = "debug" ] ; then
  _XDEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y"
  shift
fi

java $_XDEBUG -jar $TUSCANY_HOME/bin/launcher.jar "$@"
