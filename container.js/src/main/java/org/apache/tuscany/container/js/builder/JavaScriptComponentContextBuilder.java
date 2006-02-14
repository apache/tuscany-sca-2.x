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
package org.apache.tuscany.container.js.builder;

import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.config.JavaScriptComponentRuntimeConfiguration;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.SimpleComponent;

public class JavaScriptComponentContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {
    public JavaScriptComponentContextBuilder() {
    }

    public void build(AssemblyModelObject modelObject, AggregateContext context) throws BuilderException {
        if (modelObject instanceof SimpleComponent) {
            SimpleComponent component = (SimpleComponent) modelObject;
            ComponentImplementation impl = component.getComponentImplementation();
            if (impl instanceof JavaScriptImplementation) {
                JavaScriptComponentRuntimeConfiguration config = new JavaScriptComponentRuntimeConfiguration(component, (JavaScriptImplementation) impl);
                component.getComponentImplementation().setRuntimeConfiguration(config);
            }
        }
    }

}
