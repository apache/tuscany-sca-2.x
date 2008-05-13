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
package org.apache.tuscany.sca.contribution.groovy;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs a distributed domain in a single VM by using and in memory implementation of the distributed domain
 *
 * @version $Rev$ $Date$
 */
public class HelloWorldTestCase {

    private static HelloWorld helloWorld;
    private static SCADomain scaDomain;

    @BeforeClass
    public static void init() throws Exception {
        scaDomain = SCADomain.newInstance("org/apache/tuscany/sca/contribution/groovy/helloworld.composite");
        helloWorld = scaDomain.getService(HelloWorld.class, "HelloWolrdComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (scaDomain != null) {
            scaDomain.close();
        }
    }

    @Test
    public void testCalculator() throws Exception {
        Assert.assertEquals("Hello Petra", helloWorld.sayHello("Petra"));
    }
}
