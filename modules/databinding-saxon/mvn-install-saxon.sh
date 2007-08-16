#!/bin/sh

#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#  
#    http://www.apache.org/licenses/LICENSE-2.0
#    
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

# Download Saxon B 8.9, unzip, and install into local Maven repos

cd /tmp
wget http://prdownloads.sourceforge.net/saxon/saxonb8-9j.zip

unzip saxonb8-9j.zip

mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8.jar

#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-ant -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-ant.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-dom4j -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-dom4j.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-dom -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-dom.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-jdom -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-jdom.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-sql -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-sql.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-xom -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-xom.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-xpath -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-xpath.jar
#mvn install:install-file -DgroupId=net.sf.saxon -DartifactId=saxon-xqj -Dversion=8.9 -Dpackaging=jar -Dfile=/tmp/saxon8-xqj.jar

