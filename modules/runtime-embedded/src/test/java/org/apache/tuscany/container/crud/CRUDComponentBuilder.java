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
package org.apache.tuscany.container.crud;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;

/**
 * Builds a Java-based atomic context from a component definition
 * 
 * @version $$Rev$$ $$Date: 2007-03-28 09:03:01 -0700 (Wed, 28 Mar
 *          2007) $$
 */
public class CRUDComponentBuilder extends ComponentBuilderExtension<CRUDImplementation> {

    @SuppressWarnings("unchecked")
    public AtomicComponent build(ComponentDefinition<CRUDImplementation> definition, DeploymentContext context)
        throws BuilderConfigException {
        CRUDAtomicComponent component = new CRUDAtomicComponent(definition.getUri(), context.getGroupId(), definition
            .getImplementation().getDirectory());
        return component;
    }

    protected Class<CRUDImplementation> getImplementationType() {
        return CRUDImplementation.class;
    }

}
