/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.js.assembly.impl;

import junit.framework.TestCase;

/**
 * FIXME commented out until SCDL loading works
 * 
 * @version $Rev: 368822 $ $Date: 2006-01-13 18:54:38 +0000 (Fri, 13 Jan 2006) $
 */
public class JavaScriptImplementationTestCase extends TestCase {

    private JavaScriptImplementationImpl impl = (JavaScriptImplementationImpl) new JavaScriptAssemblyFactoryImpl()
            .createJavaScriptImplementation();
    private ClassLoader origLoader;

    public void testDummy(){} // remove when tests added back
    
//     public void testNoImplementationClass() {
//        impl.setScriptFile("no.such.script.js");
//        try {
//            impl.initialize(new AssemblyModelContextImpl(null, null));
//            impl.getComponentType();
//            fail("Expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            // ok
//        }
//    }

//     public void testHelloWorldWithSidefile() {
//        impl.setScriptFile("org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js");
//        impl.initialize(new AssemblyModelContextImpl(new AssemblyLoaderImpl(), ResourceLoaderFactory.getResourceLoader(Thread
//                .currentThread().getContextClassLoader())));
//        ComponentType type = impl.getComponentType();
//        Assert.assertNotNull(type);
//        List<Property> props = type.getProperties();
//        Assert.assertEquals(1, props.size());
//        Assert.assertTrue(props.get(0).getName().equals("text"));
//
//        Assert.assertTrue(type.getReferences().isEmpty());
//
//        List<Service> services = type.getServices();
//        Assert.assertEquals(1, services.size());
//        Assert.assertTrue(services.get(0).getName().equals("HelloWorldService"));
//    }

    // static {
    // // bootstrap this somehow
    // AssemblyPackage.eINSTANCE.getClass();
    // }

    protected void setUp() throws Exception {
        super.setUp();
        origLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JavaScriptImplementationTestCase.class.getClassLoader());
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(origLoader);
        super.tearDown();
    }
}
