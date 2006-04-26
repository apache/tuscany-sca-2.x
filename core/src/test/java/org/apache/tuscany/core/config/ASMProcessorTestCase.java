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
package org.apache.tuscany.core.config;

import java.io.IOException;

import junit.framework.TestCase;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.objectweb.asm.ClassReader;

/**
 * @version $Rev$ $Date$
 */
public class ASMProcessorTestCase extends TestCase {
    private AssemblyFactory factory;

    public void testFoo() throws IOException {
        ClassReader reader = new ClassReader(Bean1.class.getResourceAsStream("Bean1.class"));
        SCAVisitor visitor = new SCAVisitor(factory.createComponentInfo());
        reader.accept(visitor, true);
        visitor.getComponentType();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new AssemblyFactoryImpl();
    }

}
