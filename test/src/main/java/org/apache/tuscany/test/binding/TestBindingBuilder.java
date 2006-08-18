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
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * @version $Rev: 431036 $ $Date: 2006-08-12 06:58:50 -0700 (Sat, 12 Aug 2006) $
 */
public class TestBindingBuilder extends BindingBuilderExtension<TestBinding> {

    @SuppressWarnings("unchecked")
    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<TestBinding> definition,
                           DeploymentContext context) {
        Class<?> interfaze = definition.getServiceContract().getInterfaceClass();
        return new TestBindingService(definition.getName(), interfaze, parent, wireService);
    }

    protected Class<TestBinding> getBindingType() {
        return TestBinding.class;
    }
}
