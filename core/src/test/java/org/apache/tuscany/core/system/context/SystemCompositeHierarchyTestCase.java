/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.system.context;

import junit.framework.Assert;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.context.AbstractCompositeHierarchyTests;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;

import java.util.List;

/**
 * Performs testing of various hierarchical scenarios
 * 
 * @version $Rev$ $Date$
 */
public class SystemCompositeHierarchyTestCase extends AbstractCompositeHierarchyTests {



    protected CompositeContext createContextHierachy() throws Exception {
        List<ContextFactoryBuilder> mockBuilders = MockFactory.createSystemBuilders();
        CompositeContext parent = new SystemCompositeContextImpl("test.parent", null, null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(mockBuilders), new NullMonitorFactory());
        parent.registerModelObject(MockFactory.createCompositeComponent("test.child"));
        parent.start();
        CompositeContext child = (CompositeContext) parent.getContext("test.child");
        Assert.assertNotNull(child);
        return parent;
    }

}
