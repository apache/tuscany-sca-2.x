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

import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.scdl.SCDLUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for reading and writing SCDL
 */
public class ContributionTestCase {

    @Test
    public void testRead() throws Exception {
        Contribution contribution = SCDLUtils.readContribution("../../samples/calculator/target/sample-calculator.jar");
        
        // Ferkle around in the contribution verifying it looks as expected
        Assert.assertNotNull(contribution);
        List<Composite> deployables = contribution.getDeployables();
        Assert.assertEquals(1, deployables.size());
        Composite calculatorComposte = deployables.get(0);
        Assert.assertEquals("Calculator", calculatorComposte.getName().getLocalPart());
        Assert.assertEquals(5, calculatorComposte.getComponents().size());
        Component calcComp = calculatorComposte.getComponent("CalculatorServiceComponent");
        Assert.assertNotNull(calcComp);
        Assert.assertEquals(4, calcComp.getReferences().size());
        Reference ref = calcComp.getReference("addService");
        Assert.assertEquals("AddServiceComponent", ref.getTargets().get(0).getName());
        Implementation impl = calcComp.getImplementation();
        Assert.assertTrue(impl instanceof JavaImplementation);
        Assert.assertEquals("calculator.CalculatorServiceImpl", ((JavaImplementation)impl).getJavaClass().getName());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}
