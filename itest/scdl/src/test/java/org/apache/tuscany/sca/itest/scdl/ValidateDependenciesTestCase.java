/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.itest.scdl;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test case for verifying only SCDL jars are needed
 * 
 * Uses maven-dependency-plugin config in the pom.xml
 * 
 * Current required jars are:
 * geronimo-stax-api_1.0_spec-1.0.1.jar
 * jsr181-api-1.0-MR1.jar
 * junit-4.5.jar
 * tuscany-assembly-2.0-SNAPSHOT.jar
 * tuscany-assembly-xml-2.0-SNAPSHOT.jar
 * tuscany-assembly-xsd-2.0-SNAPSHOT.jar
 * tuscany-binding-jms-2.0-SNAPSHOT.jar
 * tuscany-binding-jsonp-2.0-SNAPSHOT.jar
 * tuscany-binding-rmi-2.0-SNAPSHOT.jar
 * tuscany-binding-ws-2.0-SNAPSHOT.jar
 * tuscany-common-java-2.0-SNAPSHOT.jar
 * tuscany-common-xml-2.0-SNAPSHOT.jar
 * tuscany-contribution-2.0-SNAPSHOT.jar
 * tuscany-extensibility-2.0-SNAPSHOT.jar
 * tuscany-implementation-bpel-2.0-SNAPSHOT.jar
 * tuscany-implementation-java-2.0-SNAPSHOT.jar
 * tuscany-interface-java-2.0-SNAPSHOT.jar
 * tuscany-interface-wsdl-2.0-SNAPSHOT.jar
 * tuscany-monitor-2.0-SNAPSHOT.jar
 * tuscany-sca-api-2.0-SNAPSHOT.jar
 * tuscany-scdl-2.0-SNAPSHOT.jar
 * tuscany-xsd-2.0-SNAPSHOT.jar
 * wsdl4j-1.6.2.jar
 * wstx-asl-3.2.4.jar
 * XmlSchema-1.4.2.jar
 * 
 * TODO: WS binding drags in all runtime
 */
public class ValidateDependenciesTestCase {

    @Test
    public void countDependencies() {

        File dependenciesDir = new File("target/dependency");
        Assert.assertTrue(dependenciesDir.exists());
        
        File[] dependencyFiles = dependenciesDir.listFiles();
        Assert.assertEquals(20, dependencyFiles.length);
    }
}
