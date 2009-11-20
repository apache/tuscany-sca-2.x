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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jws.WebService;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Service;

/**
 * @version $Rev$ $Date$
 */
public class ServiceProcessorTestCase {
    private ServiceProcessor processor;
    private JavaImplementation type;

    @Test
    public void testMultipleInterfaces() throws Exception {
        processor.visitClass(FooMultiple.class, type);
        assertEquals(2, type.getServices().size());
        org.apache.tuscany.sca.assembly.Service service = ModelHelper.getService(type, Baz.class.getSimpleName());
        assertEquals(Baz.class, ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass());
        assertEquals(Bar.class, ((JavaInterface)service.getInterfaceContract().getCallbackInterface()).getJavaClass());
        assertNotNull(ModelHelper.getService(type, Bar.class.getSimpleName()));
    }

    @Test
    public void testSingleInterfaces() throws Exception {
        processor.visitClass(FooSingle.class, type);
        assertEquals(1, type.getServices().size());
        assertNotNull(ModelHelper.getService(type, Baz.class.getSimpleName()));
    }

    @Test
    public void testMultipleNoService() throws Exception {
        processor.visitClass(FooMultipleNoService.class, type);
        assertEquals(0, type.getServices().size());
    }

    /**
     * Verifies a service with a callback annotation is recognized
     */
    @Test
    public void testMultipleWithCallbackAnnotation() throws Exception {
        processor.visitClass(FooMultipleWithCalback.class, type);
        assertEquals(1, type.getServices().size());
    }


    @Test
    public void testMultipleWithWebServiceAnnotation() throws Exception {
        processor.visitClass(FooMultipleWithWebService.class, type);
        assertEquals(2, type.getServices().size());
    }
    
    @Test
    public void testRemotableNoService() throws Exception {
        processor.visitClass(FooRemotableNoService.class, type);
        assertEquals(1, type.getServices().size());
        org.apache.tuscany.sca.assembly.Service service = ModelHelper.getService(type, BazRemotable.class.getSimpleName());
        assertEquals(BazRemotable.class, ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass());
    }

    @Test
    public void testNonInterface() throws Exception {
        processor.visitClass(FooServiceUsingClassImpl.class, type);
    }

    @Test
    public void testMultiplenamedInterfaces() throws Exception {
        processor.visitClass(FooMultipleNamed.class, type);
        assertEquals(2, type.getServices().size());
        org.apache.tuscany.sca.assembly.Service sbaz = type.getService("BazName");
        assertNotNull(sbaz);
        assertEquals(Baz.class, ((JavaInterface)sbaz.getInterfaceContract().getInterface()).getJavaClass());
        org.apache.tuscany.sca.assembly.Service sbar = type.getService("BarName");
        assertNotNull(sbar);
        assertEquals(Bar.class, ((JavaInterface)sbar.getInterfaceContract().getInterface()).getJavaClass());
    }

    @Test
    public void testBadService() throws Exception {
        try {
            processor.visitClass(BadService.class, type);
            fail();
        } catch (IntrospectionException e) {
            assertTrue(e.getMessage().startsWith("JCA90059"));
        }
    }

    @Test
    public void testBadServiceNames() throws Exception {
        try {
            processor.visitClass(BadServiceNames.class, type);
            fail();
        } catch (IntrospectionException e) {
            assertTrue(e.getMessage().startsWith("JCA90050"));
        }
    }

    @Test
    public void testBadServiceDuplicateNames() throws Exception {
        try {
            processor.visitClass(BadServiceDuplicateNames.class, type);
            fail();
        } catch (IntrospectionException e) {
            assertTrue(e.getMessage().startsWith("JCA90060"));
        }
    }

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        processor = new ServiceProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(registry));
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        type = javaImplementationFactory.createJavaImplementation();
    }

    @Callback(Bar.class)
    private interface Baz {
    }

    private interface Bar {
    }

    private interface Bar2 {
    }
    
    @WebService
    private interface Bar3 {
    }

    @Remotable
    private interface BazRemotable {
    }

    @Service({Baz.class, Bar.class})
    private class FooMultiple implements Baz, Bar {

    }

    @Service(Baz.class)
    private class FooSingle implements Baz, Bar {

    }

    private class FooMultipleNoService implements Bar, Bar2 {

    }

    private class FooMultipleWithCalback implements Baz, Bar {

    }
    
    private class FooMultipleWithWebService implements BazRemotable, Bar3 {
    }
    
    private class FooRemotableNoService implements BazRemotable, Bar {

    }

    @Service(FooSingle.class)
    private class FooServiceUsingClassImpl extends FooSingle {

    }

    @Service(value={Baz.class, Bar.class}, names={"BazName", "BarName"})
    private class FooMultipleNamed implements Baz, Bar {

    }

    @Service(value={})
    private class BadService implements Baz {

    }

    @Service(value={Baz.class, Bar.class}, names={"BazName"})
    private class BadServiceNames implements Baz, Bar {

    }

    @Service(value={Baz.class, Bar.class}, names={"BazName", "BazName"})
    private class BadServiceDuplicateNames implements Baz, Bar {

    }
}
