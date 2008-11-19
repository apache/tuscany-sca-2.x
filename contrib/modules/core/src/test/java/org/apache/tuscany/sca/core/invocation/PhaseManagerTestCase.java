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

package org.apache.tuscany.sca.core.invocation;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class PhaseManagerTestCase {

    @Test
    public void testDiscovery() {
        PhaseManager pm = new PhaseManager("org.apache.tuscany.sca.invocation.PhaseTest");
        List<String> phases = pm.getAllPhases();
        System.out.println(phases.size());
        System.out.println(phases);
        // Assert.assertEquals(15, phases.size());
        Assert.assertEquals("reference.first", phases.get(0));

        int rt = phases.indexOf("reference.transaction");
        Assert.assertTrue(rt > phases.indexOf("reference.interface"));

        int st = phases.indexOf("service.transaction");
        Assert.assertTrue(st > phases.indexOf("service.binding"));

        int it = phases.indexOf("implementation.transaction");
        Assert.assertTrue(it < phases.indexOf("implementation.policy"));
    }
}
