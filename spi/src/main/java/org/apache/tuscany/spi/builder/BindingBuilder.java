/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.model.BoundService;

/**
 * Responsible for processing a service or reference in an assembly configured with a particular binding. The
 * builder will create and return corresponding {@link org.apache.tuscany.spi.context.ServiceContext} or
 * {@link org.apache.tuscany.spi.context.ReferenceContext}
 *
 * @version $Rev$ $Date$
 */
public interface BindingBuilder<B extends Binding> {
    Context build(CompositeContext parent, BoundService<B> boundService, DeploymentContext deploymentContext);

    Context build(CompositeContext parent, BoundReference<B> boundReference, DeploymentContext deploymentContext);
}
