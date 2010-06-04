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
 * junit-4.8.1.jar
 * tuscany-assembly-2.0.jar
 * tuscany-assembly-xml-2.0.jar
 * tuscany-assembly-xsd-2.0.jar
 * tuscany-binding-jms-2.0.jar
 * tuscany-binding-jsonp-2.0.jar
 * tuscany-binding-rmi-2.0.jar
 * tuscany-builder-2.0.jar
 * tuscany-common-java-2.0.jar
 * tuscany-common-xml-2.0.jar
 * tuscany-contribution-2.0.jar
 * tuscany-deployment-2.0.jar
 * tuscany-extensibility-2.0.jar
 * tuscany-implementation-java-2.0.jar
 * tuscany-interface-java-2.0.jar
 * tuscany-monitor-2.0.jar
 * tuscany-sca-api-2.0.jar
 * wstx-asl-3.2.4.jar
 * 
 * TODO: WS binding drags in all runtime
 */
public class ValidateDependenciesTestCase {

    @Test
    public void countDependencies() {

        File dependenciesDir = new File("target/dependency");
        Assert.assertTrue(dependenciesDir.exists());
        
        File[] dependencyFiles = dependenciesDir.listFiles();
        Assert.assertEquals(28, dependencyFiles.length);
    }
}
