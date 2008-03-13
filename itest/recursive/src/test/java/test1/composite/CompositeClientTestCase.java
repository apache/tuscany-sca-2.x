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

package test1.composite;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Simple client program that invokes the components that we wired together.
 */
public class CompositeClientTestCase extends TestCase {
    
    private SCADomain scaDomain;
    private Target target;

    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("test1/OuterComposite.composite");
        target = scaDomain.getService(Target.class, "Test1TargetComponent/Service_Two");
    }

    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }    

    public void testComposite() throws Exception {
        String res = target.hello("Wang Feng");
        assertEquals("TargetTwo: Hello Wang Feng!", res);
    }
    
    // Test for problem in TUSCANY-2010
    public void testURLs() throws Exception {
        try {
            System.out.println("Component URI: " + scaDomain.getComponentManager().getComponent("Test1TargetComponent").getURI());
            System.out.println("  Service Name: " + scaDomain.getComponentManager().getComponent("Test1TargetComponent").getServices().get(0).getName());
            System.out.println("    Binding Name: " + scaDomain.getComponentManager().getComponent("Test1TargetComponent").getServices().get(0).getBindings().get(0).getName());
            System.out.println("    Binding URI: " + scaDomain.getComponentManager().getComponent("Test1TargetComponent").getServices().get(0).getBindings().get(0).getURI());
            assertEquals("/Test1TargetComponent/Service_One", scaDomain.getComponentManager().getComponent("Test1TargetComponent").getServices().get(0).getBindings().get(0).getURI());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
