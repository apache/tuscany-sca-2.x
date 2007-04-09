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
package org.apache.tuscany.runtime.webapp.implementation.webapp;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class WebappBuilder extends ComponentBuilderExtension<WebappImplementation> {
    protected Class<WebappImplementation> getImplementationType() {
        return WebappImplementation.class;
    }

    public Component build(ComponentDefinition<WebappImplementation> definition, DeploymentContext context)
        throws BuilderException {

        URI uri = definition.getUri();
        WebappComponentType componentType = definition.getImplementation().getComponentType();

        Map<String, ObjectFactory<?>> attributes = new HashMap<String, ObjectFactory<?>>();
        for (PropertyValue<?> property : definition.getPropertyValues().values()) {
            ObjectFactory<?> factory = property.getValueFactory();
            if (factory != null) {
                attributes.put(property.getName(), factory);
            }
        }

        Map<String, Class<?>> referenceTypes = new HashMap<String, Class<?>>();
        for (ReferenceDefinition referenceDefinition : componentType.getReferences().values()) {
            String name = referenceDefinition.getUri().getFragment();
            Class<?> type = referenceDefinition.getServiceContract().getInterfaceClass();
            referenceTypes.put(name, type);
        }

        return new WebappComponent(uri, proxyService, workContext, context.getGroupId(), attributes, referenceTypes);
    }
}
