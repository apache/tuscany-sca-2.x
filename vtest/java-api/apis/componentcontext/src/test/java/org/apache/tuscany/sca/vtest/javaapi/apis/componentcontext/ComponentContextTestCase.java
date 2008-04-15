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

package org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the ComponentContext interface described in 1.7.1 of the SCA Java Annotations & APIs Specification 1.0.
 */
public class ComponentContextTestCase {

    protected static SCADomain domain;
    protected static String compositeName = "ab.composite";
    protected static AComponent a;
    protected static BService b;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            domain = SCADomain.newInstance(compositeName);
            a = domain.getService(AComponent.class, "AComponent");
            b = domain.getService(BService.class, "BComponent/BService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * L776 <br>
     * getURI() - Returns the absolute URI of the component within the SCA domain.
     * 
     * @throws Exception
     */
    @Test
    public void testGetURI() throws Exception {
        Assert.assertEquals("AComponent", a.getContextURI());
    }

    /**
     * L778 <br>
     * getService(Class&lt;B&gt; businessInterface, String referenceName) ? Returns a proxy for the reference defined by the current component.
     * 
     * @throws Exception
     */
    @Test
    public void testGetService() throws Exception {
        Assert.assertEquals(a.getServiceBName(), "ServiceB");
    }

    /**
     * L780 <br>
     * getServiceReference(Class&lt;B&gt; businessInterface, String referenceName) ? Returns a ServiceReference defined by the current component.
     * 
     * @throws Exception
     */
    @Test
    public void testGetServiceReference() throws Exception {
        Assert.assertEquals(a.getServiceReferenceBName(), "ServiceB");
    }

    /**
     * L783 <br>
     * createSelfReference(Class&lt;B&gt; businessInterface) ?
     * Returns a ServiceReference that can be used to invoke this component over the designated service.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSelfReference() throws Exception {
        Assert.assertEquals(a.getSelfReferenceName(), "ComponentA");
    }

    /**
     * L785 <br>
     * getSelfReference(Class&lt;B&gt; businessInterface, String serviceName) -
     * Returns a ServiceReference that can be used to invoke this component over the designated service.
     * Service name explicitly declares the service name to invoke.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSelfReferenceWithServiceName() throws Exception {
        Assert.assertEquals("ServiceC", b.getSelfReferenceWithServiceName());
    }

    /**
     * L788 <br>
     * getProperty (Class&lt;B&gt; type, String propertyName) - Returns the value of an SCA property defined by this component.
     * 
     * @throws Exception
     */
    @Test
    public void testGetProperty() throws Exception {
        Assert.assertEquals("PropertyA", a.getProperty());
    }

    /**
     * L793 <br>
     * getRequestContext() -
     * Returns the context for the current SCA service request, or null if there is no current request or if the context is unavailable.
     * 
     * @throws Exception
     */
    @Test
    public void testGetRequestContext() throws Exception {
        Assert.assertEquals("AComponent", a.getRequestContextServiceName());
    }

    /**
     * L790, L794 <br>
     * cast(B target) - Casts a type-safe reference to a CallableReference.
     * 
     * @throws Exception
     */
    @Test
    public void testCast() throws Exception {
        Assert.assertEquals("ServiceB", a.getCastCallableReferenceServiceName());
        Assert.assertEquals("ServiceB", a.getCastServiceReferenceServiceName());

        String check = "";
        try {
            a.illegalCast();
        } catch (IllegalArgumentException iae) {
            check = "IllegalCast";
        }
        Assert.assertEquals("IllegalCast", check);
    }

}
