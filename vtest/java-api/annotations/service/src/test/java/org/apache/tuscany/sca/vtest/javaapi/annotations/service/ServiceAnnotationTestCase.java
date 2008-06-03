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

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl.AObject;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl.FServiceImpl2;
import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the Service annotation described in section 1.2.1
 * including 1.3.1, 1.8.1, 1.8.3, 1.8.6, 1.8.15 and 1.8.17<br>
 * but not..<br>
 * <li>Lines 1531 to 1534</li>
 */
public class ServiceAnnotationTestCase {

    protected static String compositeName = "service.composite";
    protected static AService aService = null;
    protected static BService bService = null;
    protected static BService bService1 = null;
    protected static HService hService = null;
    protected static IService iService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            aService = ServiceFinder.getService(AService.class, "AComponent");
            bService = ServiceFinder.getService(BService.class, "BComponent");
            bService1 = ServiceFinder.getService(BService.class, "BComponent1");
            hService = ServiceFinder.getService(HService.class, "HComponent");
            iService = ServiceFinder.getService(IService.class, "IComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();

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
     * A remotable service is defined using the "@Remotable" annotation on the
     * Java interface that defines the service. Remotable services are intended
     * to be used for coarse grained services, and the parameters are passed
     * by-value.<br>
     * <p>
     * Lines 321 to 323:<br>
     * The "@Remotable" annotation on a Java interface indicates that the
     * interface is designed to be used for remote communication. Remotable
     * interfaces are intended to be used for coarse grained services.
     * Operations parameters and return values are passed by-value.<br>
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
     * Lines 996 to 1028:<br>
     * 1.8.1 "@AllowsPassByReference"<br>
     * ...<br>
     * Lines 1535 to 1540:<br>
     * Independent of whether the remotable service is called from outside of
     * the composite that contains it or from another component in the same
     * composite, the data exchange semantics are by-value.<br>
     * Implementations of remotable services may modify input data during or
     * after an invocation and may modify return data after the invocation. If a
     * remotable service is called locally or remotely, the SCA container is
     * responsible for making sure that no modification of input data or
     * post-invocation modifications to return data are seen by the caller.<br>
     * <p>
     * Test under Non-SCA <-> SCA
     * <li>BService is local service to test by reference</li>
     * <li>HService is remotable service to test "@AllowsPassByReference" at
     * method level</li>
     * <li>IService is remotable service to test "@AllowsPassByReference" at
     * class level</li>
     */
    @Test
    public void atService4() throws Exception {
        AObject b = new AObject();
        Assert.assertEquals("BService", bService.setAObject(b));
        Assert.assertEquals("BService", b.aString);

        AObject h1 = new AObject();
        Assert.assertEquals("HService", hService.setAObject1(h1));
        Assert.assertEquals("HService", h1.aString);
        h1.aString = "atService4";
        Assert.assertEquals("atService4", hService.getAObject1String());

        AObject h2 = new AObject();
        Assert.assertEquals("HService", hService.setAObject2(h2));
        Assert.assertNull(h2.aString);
        h2.aString = "atService4";
        Assert.assertEquals("HService", hService.getAObject2String());

        AObject h3 = hService.getAObject3();
        h3.aString = "atService4";
        Assert.assertEquals("HService", hService.getAObject3String());

        AObject i1 = new AObject();
        Assert.assertEquals("IService", iService.setAObject1(i1));
        Assert.assertEquals("IService", i1.aString);
        i1.aString = "atService4";
        Assert.assertEquals("atService4", iService.getAObject1String());

        AObject i2 = new AObject();
        Assert.assertEquals("IService", iService.setAObject2(i2));
        Assert.assertEquals("IService", i2.aString);
        i2.aString = "atService4";
        Assert.assertEquals("atService4", iService.getAObject2String());

        AObject i3 = iService.getAObject3();
        i3.aString = "atService4";
        Assert.assertEquals("atService4", iService.getAObject3String());
    }

    /**
     * Line 1624 to 1627:<br>
     * A class used as the implementation of a service is not required to have
     * an "@Service" annotation. If a class has no "@Service" annotation, then
     * the rules determining which services are offered and what interfaces
     * those services have are determined by the specific implementation type.
     * <br>
     */
    @Test
    public void atService5() throws Exception {
        CService cService = ServiceFinder.getService(CService.class, "CComponent");
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
        DService1 dService1 = ServiceFinder.getService(DService1.class, "DComponent/DService1");
        Assert.assertEquals("DService1", dService1.getName1());
        DService2 dService2 = ServiceFinder.getService(DService2.class, "DComponent/DService2");
        Assert.assertEquals("DService2", dService2.getName2());
        try {
            ServiceFinder.getService(DService3.class, "DComponent/DService3");
            fail("Should have failed to get this service");
        } catch (Exception e) {
            // Expect an exception
        }
    }

    /**
     * Line 1635 to 1636:<br>
     * A "@Service" annotation with no attributes is meaningless, it is the same
     * as not having the annotation there at all.<br>
     */
    @Test
    public void atService7() throws Exception {
        EService eService = ServiceFinder.getService(EService.class, "EComponent");
        Assert.assertEquals("EService", eService.getName());
    }

    /**
     * Line 1637 to 1638:<br>
     * The service names of the defined services default to the names of the
     * interfaces or class, without the package name.<br>
     */
    @Test
    public void atService8() throws Exception {
        FService fService = ServiceFinder.getService(FService.class, "FComponent");
        Assert.assertEquals("FService", fService.getName());
        FServiceImpl2 fServiceImpl2 = ServiceFinder.getService(FServiceImpl2.class, "FComponent2");
        Assert.assertEquals("FServiceImpl2", fServiceImpl2.getName());
        fService = ServiceFinder.getService(FService.class, "FComponent2");
        Assert.assertEquals("FServiceImpl2", fService.getName());
    }

    /**
     * Line 1639 to 1641:<br>
     * If a Java implementation needs to realize two services with the same
     * interface, then this is achieved through subclassing of the interface.
     * The subinterface must not add any methods. Both interfaces are listed in
     * the "@Service" annotation of the Java implementation class. <br>
     */
    @Test
    public void atService9() throws Exception {
        GService1 gService1 = ServiceFinder.getService(GService1.class, "GComponent/GService1");
        GService2 gService2 = ServiceFinder.getService(GService2.class, "GComponent/GService2");
        Assert.assertEquals("GService", gService1.getName());
        Assert.assertEquals("GService1", gService1.getServiceName());
        Assert.assertEquals("GService", gService2.getName());
        Assert.assertEquals("GService2", gService2.getServiceName());
    }

    /**
     * Lines 227 to 242:<br>
     * A local service can only be called by clients that are deployed within
     * the same address space as the component implementing the local service.<br>
     * ...<br>
     * The data exchange semantic for calls to local services is by-reference.<br>
     * ...<br>
     * Lines 996 to 1028:<br>
     * 1.8.1 "@AllowsPassByReference"<br>
     * ...<br>
     * Lines 1535 to 1540:<br>
     * Independent of whether the remotable service is called from outside of
     * the composite that contains it or from another component in the same
     * composite, the data exchange semantics are by-value.<br>
     * Implementations of remotable services may modify input data during or
     * after an invocation and may modify return data after the invocation. If a
     * remotable service is called locally or remotely, the SCA container is
     * responsible for making sure that no modification of input data or
     * post-invocation modifications to return data are seen by the caller.<br>
     * <p>
     * Test under SCA <-> SCA<br>
     * <li>AService is remotable service to test by value</li>
     * <li>CService is local service to test by-reference</li>
     * <li>HService is remotable service to test "@AllowsPassByReference" at
     * method level</li>
     * <li>IService is remotable service to test "@AllowsPassByReference" at
     * class level</li>
     */
    @Test
    public void atService10() throws Exception {
        Assert.assertEquals("None", bService1.testServices());
    }

    /**
     * Lines 1095 to 1124:<br>
     * 1.8.3. "@ComponentName"<br>
     * ...<br>
     * The "@ComponentName" annotation type is used to annotate a Java class
     * field or setter method that is used to inject the component name.<br>
     * ...<br>
     */
    @Test
    public void atService11() throws Exception {
        Assert.assertEquals("HComponent", hService.getComponentName());
        Assert.assertNull(iService.getComponentName1());
        Assert.assertEquals("IComponent", iService.getComponentName2());
    }

    /**
     * Lines 1164 to 1187:<br>
     * 1.8.6. "@Context"<br>
     * ...<br>
     * The "@Context" annotation type is used to annotate a Java class field or
     * a setter method that is used to inject a composite context for the
     * component. The type of context to be injected is defined by the type of
     * the Java class field or type of the setter method input argument, the
     * type is either ComponentContext or RequestContext.<br>
     * ...<br>
     * <p>
     * HService - "@Context" is used to annotate setter methods<br>
     * IService - "@Context" is used to annotate class fields<br>
     * <br>
     */
    @Test
    public void atService12() throws Exception {
        Assert.assertEquals("HService", hService.getServiceName1());
        Assert.assertEquals("HService", hService.getServiceName2());
        Assert.assertEquals("IService", iService.getServiceName1());
        Assert.assertEquals("IService", iService.getServiceName2());
    }
}
