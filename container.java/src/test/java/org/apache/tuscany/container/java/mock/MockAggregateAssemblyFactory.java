/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.builder.JavaComponentContextBuilder;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponentImpl;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.model.assembly.ExtensibleModelObject;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Creates test artifacts such as runtime configurations
 * 
 * @version $Rev$ $Date$
 */
public class MockAggregateAssemblyFactory {

    private MockAggregateAssemblyFactory() {
    }

    /**
     * Creats an assembly containing a module-scoped component definition, a session-scoped component definition, and a
     * request-scoped component definition
     * 
     * @param ctx the parent module context
     */
    public static List<ExtensibleModelObject> createAssembly(AggregateContext ctx) throws BuilderException {
        try {
            JavaComponentContextBuilder builder = new JavaComponentContextBuilder();
            SimpleComponent component = MockAssemblyFactory.createComponent("TestService1", ModuleScopeComponentImpl.class,
                    ScopeEnum.MODULE_LITERAL);
            SimpleComponent sessionComponent = MockAssemblyFactory.createComponent("TestService2",
                    SessionScopeComponentImpl.class, ScopeEnum.SESSION_LITERAL);
            SimpleComponent requestComponent = MockAssemblyFactory.createComponent("TestService3",
                    SessionScopeComponentImpl.class, ScopeEnum.REQUEST_LITERAL);
            builder.setParentContext(ctx);
            builder.setModelObject(component);
            builder.build();
            builder.setModelObject(sessionComponent);
            builder.build();
            builder.setModelObject(requestComponent);
            builder.build();
            List<ExtensibleModelObject> configs = new ArrayList();
            configs.add(component);
            configs.add(sessionComponent);
            configs.add(requestComponent);
            return configs;
        } catch (NoSuchMethodException e) {
            throw new BuilderConfigException(e);
        }
    }
}
