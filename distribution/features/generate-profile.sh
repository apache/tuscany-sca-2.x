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

# After building a distribution, this script can be used to
# generate a Maven build profile that includes all the modules
# included directly or transitively in that distribution

echo "        <profile>"
echo "            <id>distribution</id>"
echo "            <modules>"

unzip -v target/*-SNAPSHOT.zip | awk '/(.*)(\/modules\/tuscany-)(.*)(\..ar$)/ { print gensub("(.*)(/modules/tuscany-)(.*)(-...-SNAPSHOT.jar)", "\\3", "g")}' | sort | awk '{ printf "                <module>../../../modules/%s</module>\n", $1 }'

echo "            </modules>"
echo "        </profile>"
echo ""

