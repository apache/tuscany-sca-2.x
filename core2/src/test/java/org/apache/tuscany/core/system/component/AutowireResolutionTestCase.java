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
package org.apache.tuscany.core.system.component;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verfies specific autowire resolution scenarios
 *
 * @version $Rev$ $Date$
 */
public class AutowireResolutionTestCase extends MockObjectTestCase {
    private SystemCompositeComponentImpl<?> context;
    private Mock parent;
    private Mock autowire;

    public void testConstruction() {
        assertEquals("test", context.getName());
        assertSame(parent.proxy(), context.getParent());
    }

    public void testResolveToSelf() {
        assertSame(context, context.resolveInstance(CompositeComponent.class));
        assertSame(context, context.resolveInstance(SystemCompositeComponent.class));
    }

    public void testResolvedByAutowire() {
        Foo foo = new Foo() {
        };
        autowire.expects(once()).method("resolveInstance").with(eq(Foo.class)).will(returnValue(foo));
        assertSame(foo, context.resolveInstance(Foo.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = mock(CompositeComponent.class);
        autowire = mock(AutowireComponent.class);
        context = new SystemCompositeComponentImpl("test",
            (CompositeComponent) parent.proxy(),
            (AutowireComponent) autowire.proxy());
    }

    public static interface Foo {
    }
}
