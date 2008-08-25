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

# This generates a Maven assembly component that can be included in a Maven
# assembly to include the source of the modules in a source distribution.

echo "<!--"
echo "    * Licensed to the Apache Software Foundation (ASF) under one"
echo "    * or more contributor license agreements.  See the NOTICE file"
echo "    * distributed with this work for additional information"
echo "    * regarding copyright ownership.  The ASF licenses this file"
echo "    * to you under the Apache License, Version 2.0 (the"
echo "    * "License"); you may not use this file except in compliance"
echo "    * with the License.  You may obtain a copy of the License at"
echo "    *"
echo "    *   http://www.apache.org/licenses/LICENSE-2.0"
echo "    *"
echo "    * Unless required by applicable law or agreed to in writing,"
echo "    * software distributed under the License is distributed on an"
echo "    * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY"
echo "    * KIND, either express or implied.  See the License for the"
echo "    * specific language governing permissions and limitations"
echo "    * under the License."
echo "-->"
echo "<component>"
echo "    <dependencySets>"
echo ""
echo "        <!-- Add all the Tuscany modules to the modules directory -->"
echo "        <dependencySet>"
echo "            <outputDirectory>modules</outputDirectory>"
echo "            <unpack>false</unpack>"
echo "            <useTransitiveFiltering>true</useTransitiveFiltering>"
echo "            <includes>"
echo "                <include>org.apache.tuscany.sca:*</include>"
echo "            </includes>"
echo "            <excludes>"
mvn -o dependency:list | awk '/.INFO.    (.*.tuscany.sca):(tuscany-)(.*):(.*):(.*):(.*)/ { print gensub("(.INFO.    )(.*)(:)(.*)(:)(.*)(:)(.*)(:)(.*)", "\\2:\\4", "g") }' | sort | awk '{ printf "                <exclude>%s</exclude>\n", $1 }'
echo "            </excludes>"
echo "        </dependencySet>"
echo ""
echo "        <!-- Add all the dependent jars to the lib directory -->"
echo "        <dependencySet>"
echo "            <outputDirectory>lib</outputDirectory>"
echo "            <unpack>false</unpack>"
echo "            <useTransitiveFiltering>true</useTransitiveFiltering>"
echo "            <excludes>"
mvn -o dependency:list | awk '/.INFO.    (.*):(.*):(.*):(.*):(.*)/ && !/(.*)(.tuscany.sca):(.*)/ { print gensub("(.INFO.    )(.*)(:)(.*)(:)(.*)(:)(.*)(:)(.*)", "\\2:\\4", "g") }' | sort | awk '{ printf "                <exclude>%s</exclude>\n", $1 }'
echo "                <exclude>org.apache.tuscany.sca:*</exclude>"
echo "            </excludes>"
echo "        </dependencySet>"
echo "    </dependencySets>"


echo "</component>"
echo ""

