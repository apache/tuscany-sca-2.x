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
package org.apache.tuscany.container.spring.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Component type information for a Spring composite component implementation type. A component type is associated with
 * a Spring application context
 *
 * @version $Rev$ $Date$
 */
public class SpringComponentType<P extends Property<?>>
    extends CompositeComponentType<ServiceDefinition, ReferenceDefinition, P> {
    private Map<String, ServiceDeclaration> serviceDeclarations = new HashMap<String, ServiceDeclaration>();
    private Map<String, ReferenceDeclaration> referenceDeclarations = new HashMap<String, ReferenceDeclaration>();
    private boolean exposeAllBeans;

    public SpringComponentType() {
    }

    /**
     * Returns true if all beans in the Spring application context may be service targets or false if service types are
     * defined
     */
    public boolean isExposeAllBeans() {
        return exposeAllBeans;
    }

    /**
     * Sets if all beans in the Spring application context may be service targets or false if service types are defined
     */
    public void setExposeAllBeans(boolean exposeAllBeans) {
        this.exposeAllBeans = exposeAllBeans;
    }

    /**
     * Returns the service declarations for the composite
     *
     * @return Returns the service declarations for the composite
     */
    public Map<String, ServiceDeclaration> getServiceDeclarations() {
        return serviceDeclarations;
    }

    /**
     * Adds a service declaration for the composite
     */
    public void addServiceDeclaration(ServiceDeclaration declaration) {
        serviceDeclarations.put(declaration.getName(), declaration);
    }

    /**
     * Returns the reference declarations for the composite
     *
     * @return Returns the reference declarations for the composite
     */
    public Map<String, ReferenceDeclaration> getReferenceDeclarations() {
        return referenceDeclarations;
    }

    /**
     * Adds a service declarations for the composite
     */
    public void addReferenceDeclaration(ReferenceDeclaration declaration) {
        referenceDeclarations.put(declaration.getName(), declaration);
    }

}
