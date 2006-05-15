/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.extension;

import junit.framework.TestCase;
import org.apache.tuscany.core.extension.ExternalServiceContextFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ExternalService;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ExternalServiceBuilderSupportTestCase extends TestCase {

    public void testGenericReflection() throws Exception {
        ExternalServiceBuilderSupportTestCase.TestExternalServiceBuilder b = new ExternalServiceBuilderSupportTestCase.TestExternalServiceBuilder();
        assertEquals(ExternalServiceBuilderSupportTestCase.TestBinding.class, b.getImplementationClass());
    }

    public void testNegativeGenericReflection() throws Exception {
        try {
            new ExternalServiceBuilderSupportTestCase.NonGenericFactoryBuilder();
            fail("AssertionError expected on non-genericized subclass of " + ContextFactoryBuilderSupport.class.getName());
        } catch (AssertionError e) {
            // indicates success
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    private class TestExternalServiceBuilder extends ExternalServiceBuilderSupport<ExternalServiceBuilderSupportTestCase.TestBinding> {


        public Class getImplementationClass() {
            return bindingClass;
        }

        protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {
            return null;
        }
    }

    private class NonGenericFactoryBuilder extends ExternalServiceBuilderSupport {


        public Class getImplementationClass() {
            return bindingClass;
        }


        protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {
            return null;
        }
    }

    private interface TestBinding extends Binding {

    }
}
