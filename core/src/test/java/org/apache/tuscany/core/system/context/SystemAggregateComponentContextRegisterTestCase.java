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

import java.util.List;

import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.context.AggregateComponentContextRegisterTestCase;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;

/**
 * Tests registration of model objects for an system aggregate context
 * 
 * @version $Rev$ $Date$
 */
public class SystemAggregateComponentContextRegisterTestCase extends AggregateComponentContextRegisterTestCase {

    protected AggregateContext createContext() {
        List<ContextFactoryBuilder> builders = MockFactory.createSystemBuilders();
        return new SystemAggregateContextImpl("test.context", null,
                null, new DefaultScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory(), null, null);
    }
}
