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

package org.apache.tuscany.sca.itest.base.dependencies;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test case for verifying the expected dependencies of the base jar
 * (Uses maven-dependency-plugin config in the pom.xml to generate the dependency list)
 *
 * *** NOTE: this is an API! If the dependencies change the API is changed and users will be broken.
 *           Don't just change the list without thinking about the consequencies
 * 
 * Current required jars are:
 *    asm-3.1.jar
 *    cglib-2.2.jar
 *    hazelcast-1.8.3.jar
 *    hazelcast-client-1.8.3.jar
 *    tuscany-base-2.0-M5.1.jar
 *    wsdl4j-1.6.2.jar
 *    XmlSchema-1.4.3.jar  
 *
 *    junit-4.8.1.jar (only from this testcase)
 *
 */
public class ValidateDependenciesTestCase {

    @Test
    public void countDependencies() {

        File dependenciesDir = new File("target/dependency");
        Assert.assertTrue(dependenciesDir.exists());
        
        File[] dependencyFiles = dependenciesDir.listFiles();
        Assert.assertEquals(8, dependencyFiles.length);
    }
}
