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
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ServiceRuntimeException;

/**
 *
 */
public class ComponentTestCase {

    private void initDomain(String compositePath) {
        System.out.println("Setting up");
        ServiceFinder.init(compositePath);
    }

    private void cleanupDomain() {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

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
        initDomain("component.composite");
        AService service = ServiceFinder.getService(AService.class, "AComponent/AService");
        Assert.assertEquals("some b component value", service.getBProperty());
        Assert.assertEquals("some b2 component value", service.getB2Property());
        cleanupDomain();
    }

    /**
     * Lines 96-97:
     * <p>
     * There can be zero or more component elements within a composite.
     */
    @Test
    public void components2() throws Exception {
        initDomain("zerocomponents.composite");
        cleanupDomain();
    }

    /**
     * Lines 142-143:
     * <p>
     * name (required) – the name of the component. The name must be unique
     * across all the components in the composite.
     */
    @Test(expected = ServiceRuntimeException.class)
    //@Ignore("TUSCANY-2455")
    public void components3() throws Exception {
        initDomain("nonuniquename.composite");
        cleanupDomain();
    }

    /**
     * Lines 154-158:
     * <p>
     * A component element has zero or one implementation element as its child,
     * which points to the implementation used by the component. A component
     * with no implementation element is not runnable, but components of this
     * kind may be useful during a "top-down" development process as a means of
     * defining the characteristics required of the implementation before the
     * implementation is written.
     */
    @Test
    public void components4() throws Exception {
        initDomain("zeroimplelements.composite");
        cleanupDomain();
    }

    /**
     * Lines 159-160:
     * <p>
     * The component element can have zero or more service elements as children
     * which are used to configure the services of the component.
     */
    @Test
    public void components5() throws Exception {
        initDomain("serviceelement.composite");
        cleanupDomain();
    }

    /**
     * Lines 174-179:
     * <p>
     * A service has zero or one interface, which describes the operations
     * provided by the service. The interface is described by an interface
     * element which is a child element of the service element. If no interface
     * is specified, then the interface specified for the service by the
     * implementation is in effect. If an interface is specified it must provide
     * a compatible subset of the interface provided by the implementation, i.e.
     * provide a subset of the operations defined by the implementation for the
     * service.
     */
    @Test
    public void components6() throws Exception {
        initDomain("servicewithinterface.composite");
        CService service = ServiceFinder.getService(CService.class, "CComponent");
        Assert.assertEquals("Some State", service.getState());
        cleanupDomain();
    }

    /**
     * Lines 180-182:
     * <p>
     * A service element has one or more binding elements as children. If no
     * bindings are specified, then the bindings specified for the service by
     * the implementation are in effect. If bindings are specified, then those
     * bindings override the bindings specified by the implementation.
     */
    @Test
    public void components7() throws Exception {
        initDomain("servicewithbinding.composite");
        CService service = ServiceFinder.getService(CService.class, "CComponent");
        Assert.assertEquals("Some State", service.getState());
        cleanupDomain();
    }
    
    
}
