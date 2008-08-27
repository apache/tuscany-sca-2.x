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

# This script can be used to generate Maven build POM and assembly files
# for all the distributions

echo "Generate Maven files: core"
cd core; ../etc/generate-filtered-dependencies.sh > src/main/components/bin-filtered-dependencies.xml; ../etc/generate-maven-files.sh

echo "Generate Maven files: ejava"
cd ../ejava; ../etc/generate-maven-files.sh

echo "Generate Maven files: manager"
cd ../manager; ../etc/generate-maven-files.sh

echo "Generate Maven files: process"
cd ../process; ../etc/generate-maven-files.sh

echo "Generate Maven files: web20"
cd ../web20; ../etc/generate-maven-files.sh

echo "Generate Maven files: webservice"
cd ../webservice; ../etc/generate-maven-files.sh

echo "Generate Maven files: all"
cd ../all; ../etc/generate-maven-files.sh

