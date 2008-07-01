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

package org.apache.tuscany.sca.vtest.assembly.component;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class ComponentTestCase {

    /**
     * Lines 92-96:
     * <p>
     * Components are configured instances of implementations. Components
     * provide and consume services. More than one component can use and
     * configure the same implementation, where each component configures the
     * implementation differently.
     */
    @Test
    public void components1() throws Exception {
        System.out.println("Setting up");
        ServiceFinder.init("component.composite");
        AService service = ServiceFinder.getService(AService.class, "AComponent/AService");
        Assert.assertEquals("some b component value", service.getBProperty());
        Assert.assertEquals("some b2 component value", service.getB2Property());
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

    /**
     * Lines 96-97:
     * <p>
     * There can be zero or more component elements within a composite.
     */
    @Test
    public void components2() throws Exception {
        System.out.println("Setting up");
        ServiceFinder.init("zerocomponents.composite");
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

}
