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
package sample;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;


/**
 * Test for implementation.composite using implementation.composite
 */
public class NestedTestCase extends TestCase {

    /**
     * Reference to the domain
     */
    private SCADomain domain; 

    /**
     * Tear down the domain
     */
    @Override
    protected void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * This tests having:
     * 
     *   AComponent -> implementation.composite(BComposite)
     *   BComposite -> implementation.composite(CComposite)
     *   
     * This test fails.
     * 
     * @throws Exception Failed
     */
    public void testAComponent() throws Exception {
        domain = SCADomain.newInstance("AComposite.composite");
        
        System.out.println("Deployed names = " + domain.getComponentManager().getComponentNames());
        
        C c = domain.getService(C.class, "AComponent");

        String result = c.cOp();
        System.out.println("Method call returned [" + result + "]");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.indexOf("C:cOp()") != -1);
        Assert.assertTrue(result.indexOf("X:xOp()") != -1);
        Assert.assertTrue(result.indexOf("Y:yOp()") != -1);
    }


    /**
     * This tests having:
     * 
     *   BComposite -> implementation.composite(CComposite)
     * 
     * This test works.
     * 
     * @throws Exception Failed
     */
    public void testBComponent() throws Exception {
        domain = SCADomain.newInstance("BComposite.composite");
        
        System.out.println("Deployed names = " + domain.getComponentManager().getComponentNames());
          
        C c = domain.getService(C.class, "BComponent");
        
        String result = c.cOp();
        System.out.println("Method call returned [" + result + "]");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.indexOf("C:cOp()") != -1);
        Assert.assertTrue(result.indexOf("X:xOp()") != -1);
        Assert.assertTrue(result.indexOf("Y:yOp()") != -1);
    }
}
