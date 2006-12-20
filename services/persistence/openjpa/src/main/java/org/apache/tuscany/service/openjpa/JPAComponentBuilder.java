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
package org.apache.tuscany.service.openjpa;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;

import static org.apache.tuscany.service.openjpa.Constants.PERSISTENCE_UNIT;

/**
 * @version $Rev$ $Date$
 */
public class JPAComponentBuilder extends ComponentBuilderExtension {
    protected Class getImplementationType() {
        return null;
    }

    public Component build(CompositeComponent parent, ComponentDefinition definition, DeploymentContext ctx)
        throws BuilderConfigException {
//        String name = definition.getName();
//        ScopeContainer moduleScope = ctx.getCompositeScope();
//        Integer initLevel = definition.getInitLevel();
        ComponentType<?, ?, ?> type = definition.getImplementation().getComponentType();
        Property<?> persistenceUnit = type.getProperties().get(PERSISTENCE_UNIT);
        if (persistenceUnit == null) {
            throw new BuilderConfigException("Persistence Unit not specified in JPA import in composite",
                parent.getName());
        }
//        Property<?> configProps = type.getProperties().get(PERSISTENCE_UNIT);
        return null;
//        return new JPAAtomicComponent(name, parent, moduleScope, initLevel);
    }
}
