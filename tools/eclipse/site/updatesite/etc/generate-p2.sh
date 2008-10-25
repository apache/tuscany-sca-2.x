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

# After building the updatesite Zip, this script can be used to generate the
# Eclipse Ganymede P2 metadata for the site.

rm -rf target/p2-tmp
mkdir target/p2-tmp
cd target/p2-tmp
unzip ../apache-tuscany-sca-1.4-SNAPSHOT-updatesite.zip

eclipse -nosplash -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -updateSite tuscany-sca-1.4-SNAPSHOT-updatesite/ -site file:tuscany-sca-1.4-SNAPSHOT-updatesite/site.xml -metadataRepository file:tuscany-sca-1.4-SNAPSHOT-updatesite/ -metadataRepositoryName "Apache Tuscany SCA Eclipse Update Site." -artifactRepository file:tuscany-sca-1.4-SNAPSHOT-updatesite/ -artifactRepositoryName "Eclipse Ganymede Artifacts" -noDefaultIUs -vmargs -Xmx256m

zip ../apache-tuscany-sca-1.4-SNAPSHOT-updatesite.zip tuscany-sca-1.4-SNAPSHOT-updatesite/artifacts.xml tuscany-sca-1.4-SNAPSHOT-updatesite/content.xml

