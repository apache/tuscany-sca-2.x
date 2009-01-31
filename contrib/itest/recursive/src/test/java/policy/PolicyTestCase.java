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
package policy;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.impl.CompositeImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.DefaultSCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PolicyTestCase {

    private SCADomain domain;
    private Target targetClient;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("policy/PolicyOuterComposite.composite");
        targetClient = domain.getService(Target.class, "TargetClientComponent");
    }
   
    @After
    public void tearDown() throws Exception {
        domain.close();
    }

    @Test
    public void test() throws Exception {
        try {
        	//Check that the implementation policy sets don't flow down to the components
        	//implementations that are themselves composites (implementation.composite)
        	Component outerComponent = ((DefaultSCADomain)domain).getComponent("OuterTargetServiceComponent");
        	
        	Assert.assertEquals(0, outerComponent.getPolicySets().size());
        	
        	for (Component component :((CompositeImpl)outerComponent.getImplementation()).getComponents()){
        		Assert.assertEquals(0, component.getPolicySets().size());
        	}
        	
        	// debugging
            String result = targetClient.hello("Fred");

            System.out.println(result);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
