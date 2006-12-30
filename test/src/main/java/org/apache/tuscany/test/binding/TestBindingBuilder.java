/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class TestBindingBuilder extends BindingBuilderExtension<TestBindingDefinition> {

    @SuppressWarnings("unchecked")
    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition definition,
                                TestBindingDefinition bindingDefinition,
                                DeploymentContext ctx) {
        return new TestBindingServiceBinding(definition.getName(), parent);
    }

    public Reference build(CompositeComponent parent,
                           BoundReferenceDefinition<TestBindingDefinition> definition,
                           DeploymentContext ctx) {
        String name = definition.getName();
        return new TestBindingReference(name, parent);
    }

    protected Class<TestBindingDefinition> getBindingType() {
        return TestBindingDefinition.class;
    }
}
