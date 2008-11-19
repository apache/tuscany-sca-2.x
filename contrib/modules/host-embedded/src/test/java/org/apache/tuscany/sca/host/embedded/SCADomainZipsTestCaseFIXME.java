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

package org.apache.tuscany.sca.host.embedded;

import junit.framework.TestCase;
import sample.Helloworld;



/**
 * Test SCADomain.newInstance and invocation of a service.
 * 
 * @version $Rev: 608205 $ $Date: 2008-01-02 20:29:05 +0000 (Wed, 02 Jan 2008) $
 */
public class SCADomainZipsTestCaseFIXME extends TestCase {

    private SCADomain domain;
    
    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("myDomain", "src/test/resources/helloworld.jar", "META-INF/sca-deployables/Helloworld.composite" );
    }

    public void testInvoke() throws Exception {
        Helloworld service = domain.getService(Helloworld.class, "HelloworldComponent");
        assertEquals("Hello Petra", service.sayHello("Petra"));
    }

    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
