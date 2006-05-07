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
package org.apache.tuscany.container.java.context;

import junit.framework.TestCase;

/**
 * Tests {@link JavaAtomicContext} to ensure it handles component scopes properly
 * 
 * @version $Rev$ $Date$
 */
public class JavaAtomicContextScopeTestCase extends TestCase {

    public void testGetModuleInstance() throws Exception {
//        CompositeContext mc = new CompositeContextImpl();
//        mc.setName("mc");
//        JavaAtomicContext c = new JavaAtomicContext("foo", new PojoObjectFactory<ModuleScopeComponentImpl>(JavaIntrospectionHelper
//                .getDefaultConstructor(ModuleScopeComponentImpl.class), null, null), false, null, null, false);
//        GenericComponent service = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service);
//        service.setString("foo");
//        GenericComponent service2 = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service2);
//        Assert.assertSame(service, service2);
    }

    public void testGetSessionInstance() throws Exception {
//        CompositeContext mc = new CompositeContextImpl();
//        mc.setName("mc");
//        JavaAtomicContext c = new JavaAtomicContext("foo", new PojoObjectFactory<SessionScopeComponentImpl>(JavaIntrospectionHelper
//                .getDefaultConstructor(SessionScopeComponentImpl.class), null, null), false, null, null, false);
//        GenericComponent service = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service);
//        service.setString("foo");
//        GenericComponent service2 = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service2);
//        Assert.assertSame(service, service2);
    }

    public void testGetRequestInstance() throws Exception {
//        CompositeContext mc = new CompositeContextImpl();
//        mc.setName("mc");
//        JavaAtomicContext c = new JavaAtomicContext("foo", new PojoObjectFactory<RequestScopeComponentImpl>(JavaIntrospectionHelper
//                .getDefaultConstructor(RequestScopeComponentImpl.class), null, null), false, null, null, false);
//        GenericComponent service = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service);
//        service.setString("foo");
//        GenericComponent service2 = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service2);
//        Assert.assertSame(service, service2);
    }

    public void testGetStatelessInstance() throws Exception { 
//        CompositeContext mc = new CompositeContextImpl();
//        mc.setName("fooContext");
//        JavaAtomicContext c = new JavaAtomicContext("foo", new PojoObjectFactory<StatelessComponentImpl>(JavaIntrospectionHelper
//                .getDefaultConstructor(StatelessComponentImpl.class), null, null), false, null, null, true);
//        GenericComponent service = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service);
//        service.setString("foo");
//        GenericComponent service2 = (GenericComponent) c.getInstance(null);
//        Assert.assertNotNull(service2);
//        Assert.assertTrue(!"foo".equals(service2.getString()));
    }

}
