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
package crud;

import java.net.URI;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;

/**
 * Builds a Java-based atomic context from a component definition
 * 
 * @version $$Rev$$ $$Date: 2007-03-28 09:03:01 -0700 (Wed, 28 Mar
 *          2007) $$
 */
public class CRUDComponentBuilder extends ComponentBuilderExtension<CRUDImplementation> {

    @SuppressWarnings("unchecked")
    public AtomicComponent build(Component definition, DeploymentContext context) throws BuilderConfigException {
        URI id = URI.create(context.getComponentId() + definition.getName());
        CRUDAtomicComponent component = new CRUDAtomicComponent(id, context.getGroupId(),
                                                                (CRUDImplementation)definition.getImplementation());
        return component;
    }

    protected Class<CRUDImplementation> getImplementationType() {
        return CRUDImplementation.class;
    }

}
