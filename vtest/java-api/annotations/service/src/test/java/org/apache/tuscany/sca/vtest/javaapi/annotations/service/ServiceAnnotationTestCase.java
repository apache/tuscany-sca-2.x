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

package org.apache.tuscany.sca.vtest.javaapi.annotations.service;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.BService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.CService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService1;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService2;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService3;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.EService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.FService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl.AObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 * This test class tests the Service annotation described in section 1.2.1 and
 * 1.8.17
 */
public class ServiceAnnotationTestCase {

    protected static SCADomain domain;
    protected static String compositeName = "service.composite";
    protected static AService aService = null;
    protected static BService bService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            domain = SCADomain.newInstance(compositeName);
            aService = domain.getService(AService.class, "AComponent");
            bService = domain.getService(BService.class, "BComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        if (domain != null)
            domain.close();

    }

    /**
     * Line 215:<br>
     * <li>As a Java interface</li>
     * <p>
     * Line 1622 to 1623:<br>
     * The
     * 
     * @Service annotation type is used on a component implementation class to
     *          specify the SCA services offered by the implementation.<br>
     */
    @Test
    public void atService1() throws Exception {
        Assert.assertEquals("AService", aService.getName());
    }

    /**
     * Line 216:<br>
     * <li>As a Java class</li>
     * <p>
     * Line 1631:<br>
     * <li>value – A shortcut for the case when the class provides only a
     * single service interface.</li>
     */
    @Test
    public void atService2() throws Exception {
        Assert.assertEquals("BService", bService.getName());
    }

    /**
     * Lines 222 to 224:<br>
     * A remotable service is defined using the
     * 
     * @Remotable annotation on the Java interface that defines the service.
     *            Remotable services are intended to be used for coarse grained
     *            services, and the parameters are passed by-value.<br>
     *            <p>
     *            Lines 321 to 323:<br>
     *            The
     * @Remotable annotation on a Java interface indicates that the interface is
     *            designed to be used for remote communication. Remotable
     *            interfaces are intended to be used for coarse grained
     *            services. Operations parameters and return values are passed
     *            by-value.<br>
     */
    @Test
    public void atService3() throws Exception {
        AObject o = new AObject();
        Assert.assertEquals("AService", aService.setAObject(o));
        Assert.assertNull(o.aString);
    }

    /**
     * Lines 227 to 242:<br>
     * A local service can only be called by clients that are deployed within
     * the same address space as the component implementing the local service.<br>
     * ...<br>
     * The data exchange semantic for calls to local services is by-reference.<br>
     * ...<br>
     */
    @Test
    public void atService4() throws Exception {
        AObject o = new AObject();
        Assert.assertEquals("BService", bService.setAObject(o));
        Assert.assertEquals("BService", o.aString);
    }

    /**
     * Line 1624 to 1627:<br>
     * A class used as the implementation of a service is not required to have
     * an
     * 
     * @Service annotation. If a class has no
     * @Service annotation, then the rules determining which services are
     *          offered and what interfaces those services have are determined
     *          by the specific implementation type.<br>
     */
    @Test
    public void atService5() throws Exception {
        CService cService = domain.getService(CService.class, "CComponent");
        Assert.assertEquals("CService", cService.getName());
    }

    /**
     * Line 1623 to 1624:<br>
     * The class need not be declared as implementing all of the interfaces
     * implied by the services, but all methods of the service interfaces must
     * be present.<br>
     * <p>
     * Line 1629 to 1630:<br>
     * <li>interfaces – The value is an array of interface or class objects
     * that should be exposed as services by this component.</li>
     */
    @Test
    public void atService6() throws Exception {
        DService1 dService1 = domain.getService(DService1.class, "DComponent/DService1");
        Assert.assertEquals("DService1", dService1.getName1());
        DService2 dService2 = domain.getService(DService2.class, "DComponent/DService2");
        Assert.assertEquals("DService2", dService2.getName2());
        try {
            domain.getService(DService3.class, "DComponent/DService3");
            fail("Should have failed to get this service");
        } catch (Exception e) {
            // Expect an exception
        }
    }

    /**
     * Line 1635 to 1636:<br>
     * A
     * 
     * @Service annotation with no attributes is meaningless, it is the same as
     *          not having the annotation there at all.<br>
     */
    @Test
    @Ignore
    // Tuscany-2191. To run this test you must also un-comment the empty
    // @Service Annotation in EServiceImpl
    public void atService7() throws Exception {
        EService eService = domain.getService(EService.class, "EComponent");
        Assert.assertEquals("EService", eService.getName());
    }

    /**
     * Line 1637 to 1638:<br>
     * The service names of the defined services default to the names of the
     * interfaces or class, without the package name.<br>
     * <p>
     * This test tests
     * 
     * @Service with the full package name.
     */
    @Test
    public void atService8() throws Exception {
        FService fService = domain.getService(FService.class, "FComponent");
        Assert.assertEquals("FService", fService.getName());
    }

    /**
     * Line 1639 to 1641:<br>
     * If a Java implementation needs to realize two services with the same
     * interface, then this is achieved through subclassing of the interface.
     * The subinterface must not add any methods. Both interfaces are listed in
     * the
     * 
     * @Service annotation of the Java implementation class. <br>
     */
    @Test
    public void atService9() throws Exception {
        GService1 gService1 = domain.getService(GService1.class, "GComponent/GService1");
        GService2 gService2 = domain.getService(GService2.class, "GComponent/GService2");
        Assert.assertEquals("GService", gService1.getName());
        Assert.assertEquals("GService1", gService1.getServiceName());
        Assert.assertEquals("GService", gService2.getName());
        Assert.assertEquals("GService2", gService2.getServiceName());
    }
}
