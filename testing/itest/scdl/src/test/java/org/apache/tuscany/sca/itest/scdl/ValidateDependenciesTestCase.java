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
 * 
 * activation-1.1.jar
 * asm-3.1.jar
 * geronimo-stax-api_1.0_spec-1.0.1.jar
 * jaxb-api-2.1.jar
 * jaxb-impl-2.1.12.jar
 * jaxws-api-2.1.jar
 * jsr181-api-1.0-MR1.jar
 * jsr250-api-1.0.jar
 * junit-4.8.1.jar
 * tuscany-assembly-2.0-Beta1.jar
 * tuscany-assembly-xml-2.0-Beta1.jar
 * tuscany-assembly-xsd-2.0-Beta1.jar
 * tuscany-binding-http-2.0-Beta1.jar
 * tuscany-binding-jms-2.0-Beta1.jar
 * tuscany-binding-jsonp-2.0-Beta1.jar
 * tuscany-binding-rmi-2.0-Beta1.jar
 * tuscany-binding-ws-2.0-Beta1.jar
 * tuscany-builder-2.0-Beta1.jar
 * tuscany-common-java-2.0-Beta1.jar
 * tuscany-common-xml-2.0-Beta1.jar
 * tuscany-contribution-2.0-Beta1.jar
 * tuscany-core-spi-2.0-Beta1.jar
 * tuscany-databinding-2.0-Beta1.jar
 * tuscany-databinding-jaxb-2.0-Beta1.jar
 * tuscany-deployment-2.0-Beta1.jar
 * tuscany-extensibility-2.0-Beta1.jar
 * tuscany-implementation-java-2.0-Beta1.jar
 * tuscany-interface-java-2.0-Beta1.jar
 * tuscany-interface-java-jaxws-2.0-Beta1.jar
 * tuscany-interface-wsdl-2.0-Beta1.jar
 * tuscany-monitor-2.0-Beta1.jar
 * tuscany-sca-api-2.0-Beta1.jar
 * tuscany-xsd-2.0-Beta1.jar
 * wsdl4j-1.6.2.jar
 * wstx-asl-3.2.4.jar
 * XmlSchema-1.4.3.jar
 * 
 * TODO: WS binding drags in all runtime
 */
public class ValidateDependenciesTestCase {

    @Test
    public void countDependencies() {

        File dependenciesDir = new File("target/dependency");
        Assert.assertTrue(dependenciesDir.exists());
        
        File[] dependencyFiles = dependenciesDir.listFiles();
        Assert.assertEquals(36, dependencyFiles.length);
    }
}
