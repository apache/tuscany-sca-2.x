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

import java.lang.reflect.Method;

import org.osoa.sca.annotations.Destroy;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class DestroyProcessorTestCase extends TestCase {

    public void testDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor();
        PojoComponentType type = new PojoComponentType();
        Method method = Foo.class.getMethod("destroy");
        processor.visitMethod(null, method, type, null);
        assertNotNull(type.getDestroyMethod());
    }

    public void testBadDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor();
        PojoComponentType type = new PojoComponentType();
        Method method = Bar.class.getMethod("badDestroy", String.class);
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalDestructorException e) {
            // expected
        }
    }

    public void testTwoDestroy() throws Exception {
        DestroyProcessor processor = new DestroyProcessor();
        PojoComponentType type = new PojoComponentType();
        Method method = Bar.class.getMethod("destroy");
        Method method2 = Bar.class.getMethod("destroy2");
        processor.visitMethod(null, method, type, null);
        try {
            processor.visitMethod(null, method2, type, null);
            fail();
        } catch (DuplicateDestructorException e) {
            // expected
        }
    }


    private class Foo {

        @Destroy
        public void destroy() {
        }
    }


    private class Bar {

        @Destroy
        public void destroy() {
        }

        @Destroy
        public void destroy2() {
        }

        @Destroy
        public void badDestroy(String foo) {
        }


    }
}
