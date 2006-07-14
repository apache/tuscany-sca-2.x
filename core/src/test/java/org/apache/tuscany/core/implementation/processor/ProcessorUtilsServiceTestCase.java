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
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.model.InteractionScope;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class ProcessorUtilsServiceTestCase extends TestCase {

    public void testCreateConversationalService() {
        JavaMappedService service = ProcessorUtils.createService(Foo.class);
        assertTrue(Foo.class.equals(service.getServiceContract().getInterfaceClass()));
        assertTrue(service.isRemotable());
        assertEquals(InteractionScope.CONVERSATIONAL, service.getServiceContract().getInteractionScope());
        JavaServiceContract serviceContract = (JavaServiceContract) service.getServiceContract();
        assertTrue(Bar.class.equals(serviceContract.getCallbackClass()));
        assertTrue("ProcessorUtilsServiceTestCase$Bar".equals(serviceContract.getCallbackName()));
    }

    public void testCreateDefaultService() {
        JavaMappedService service = ProcessorUtils.createService(Baz.class);
        assertTrue(Baz.class.equals(service.getServiceContract().getInterfaceClass()));
        assertTrue(!service.isRemotable());
        assertEquals(InteractionScope.NONCONVERSATIONAL, service.getServiceContract().getInteractionScope());
    }


    @Callback(Bar.class)
    @Remotable
    @Scope("CONVERSATIONAL")
    public interface Foo {

    }

    public interface Bar {

    }

    public interface Baz{

    }
}
