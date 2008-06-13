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
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osoa.sca.annotations.EndsConversation;

public class PolicyTestCase{

    private SCADomain domain;
    private Target targetClient;

    @Before
    public void setUp() throws Exception {
        try {
            domain = SCADomain.newInstance("policy/PolicyOuterComposite.composite");
            targetClient = domain.getService(Target.class, "TargetClientComponent");
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @After
    public void tearDown() throws Exception {
        domain.close();
    }

    @Test
    public void test() throws Exception {
    	//Check that the binding policy sets do flow down to the component but not down to the
    	//component inside implementation.composite
    	Component outerComponent = ((DefaultSCADomain)domain).getComponent("OuterTargetServiceComponent");
    	
    	// The outer component service bindings should have policy sets attached
    	Assert.assertEquals(1, ((PolicySetAttachPoint)outerComponent.getServices().get(0).getBindings().get(0)).getPolicySets().size());
    	
    	Component component =((CompositeImpl)outerComponent.getImplementation()).getComponents().get(0);
    	
    	// The original inner component service binding should not have policy sets attached
    	Assert.assertEquals(0, ((PolicySetAttachPoint)component.getServices().get(0).getBindings().get(0)).getPolicySets().size());
        
        // The promoted inner component service binding should have policy sets attached
    	Assert.assertEquals(1, ((PolicySetAttachPoint)component.getServices().get(1).getBindings().get(0)).getPolicySets().size());
    	
        String result = targetClient.hello("Fred");

        System.out.println(result);
    }
}
