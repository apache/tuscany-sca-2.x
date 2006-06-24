/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ServiceProcessorTestCase extends TestCase {

    private ServiceProcessor processor;
    private PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;

    public void testMultipleInterfaces() throws Exception {
        processor.visitClass(FooMultiple.class, type, null);
        assertEquals(2, type.getServices().size());
        JavaMappedService service = type.getServices().get("ServiceProcessorTestCase$Baz");
        JavaServiceContract contract = (JavaServiceContract) service.getServiceContract();
        assertEquals(Baz.class, contract.getInterfaceClass());
        assertEquals(Bar.class, contract.getCallbackClass());
        assertEquals("ServiceProcessorTestCase$Bar", contract.getCallbackName());
        assertNotNull(type.getServices().get("ServiceProcessorTestCase$Bar"));
    }

    public void testSingleInterfaces() throws Exception {
        processor.visitClass(FooSingle.class, type, null);
        assertEquals(1, type.getServices().size());
        assertNotNull(type.getServices().get("ServiceProcessorTestCase$Baz"));
    }

    public void testMultipleNoService() throws Exception {
        processor.visitClass(FooMultipleNoService.class, type, null);
        assertEquals(0, type.getServices().size());
    }

    public void testRemotableNoService() throws Exception {
        processor.visitClass(FooRemotableNoService.class, type, null);
        assertEquals(1, type.getServices().size());
        JavaMappedService service = type.getServices().get("ServiceProcessorTestCase$BazRemotable");
        JavaServiceContract contract = (JavaServiceContract) service.getServiceContract();
        assertEquals(BazRemotable.class, contract.getInterfaceClass());
    }

    public void testNonInterface() throws Exception {
        try {
            processor.visitClass(BadImpl.class, type, null);
            fail();
        } catch (InvalidServiceType e) {
            //expected
        }
    }

    public void testNoInterfaces() throws Exception {
        try {
            processor.visitClass(BadDefinition.class, type, null);
            fail();
        } catch (IllegalServiceDefinitionException e) {
            //expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        processor = new ServiceProcessor();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
    }

    @Callback(Bar.class)
    private interface Baz {
    }

    private interface Bar {
    }

    @Remotable
    private interface BazRemotable {
    }

    @Service(interfaces = {Baz.class, Bar.class})
    private class FooMultiple implements Baz, Bar {

    }

    @Service(Baz.class)
    private class FooSingle implements Baz, Bar {

    }

    private class FooMultipleNoService implements Baz, Bar {

    }

    private class FooRemotableNoService implements BazRemotable, Bar {

    }

    @Service(FooSingle.class)
    private class BadImpl extends FooSingle {

    }


    @Service()
    private class BadDefinition extends FooSingle {

    }

}
