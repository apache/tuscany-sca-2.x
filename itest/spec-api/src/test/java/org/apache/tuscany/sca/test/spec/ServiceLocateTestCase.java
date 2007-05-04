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
package org.apache.tuscany.sca.test.spec;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.apache.tuscany.host.embedded.SCARuntimeActivator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

public class ServiceLocateTestCase {

    ComponentContext context;
    BasicService basicService;

    @Ignore
    @Test
    public void negate() {

        assertEquals(-99, basicService.negate(99));

    }

    @Ignore
    @Test
    public void delegateNegate() {

        assertEquals(-99, basicService.delegateNegate(99));

    }

    @Ignore
    @Test
    public void locateService() {
       
        
 //       SCARuntime.start("BasicService.composite");
        SCARuntimeActivator.start("BasicService.composite");
        BasicService localBasicService = SCARuntimeActivator.locateService(BasicService.class, "BasicServiceComponent");

        assertEquals(-99, localBasicService.delegateNegate(99));
        
        SCARuntimeActivator.stop();

    }
    
    
    
    @Before
    public void init() throws Exception {

/*        SCARuntime.start("BasicService.composite");
        context = SCARuntime.getComponentContext("BasicServiceComponent");
        ServiceReference<BasicService> service = context.createSelfReference(BasicService.class);
        basicService = service.getService();*/

    }

    @After
    public void destroy() throws Exception {
//        SCARuntime.stop();
    }
}
