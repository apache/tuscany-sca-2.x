/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.annotation;

import java.util.Arrays;

import org.osoa.sca.annotations.Constructor;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorAnnotationTest extends TestCase {
    public void testSingleName() throws NoSuchMethodException {
        Constructor ann = Foo1.class.getConstructor(String.class).getAnnotation(Constructor.class);
        assertNotNull(ann);
        String[] names = ann.value();
        assertTrue(Arrays.equals(new String[]{"prop"}, names));
    }

    public void testMultipleNames() throws NoSuchMethodException {
        Constructor ann = Foo1.class.getConstructor(String.class, String.class).getAnnotation(Constructor.class);
        assertNotNull(ann);
        String[] names = ann.value();
        assertTrue(Arrays.equals(new String[]{"prop", "ref"}, names));
    }

    public static class Foo1 {
        @Constructor({"prop", "ref"})
        public Foo1(String prop, String ref) {
        }

        @Constructor("prop")
        public Foo1(String prop) {
        }
    }

    public static class Foo2 {
        public Foo2(@Autowire String prop,
                    @Autowire String ref) {
        }
    }
}
