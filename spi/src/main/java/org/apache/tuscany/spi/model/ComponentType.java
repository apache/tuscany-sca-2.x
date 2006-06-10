/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The base representation of a component type
 *
 * @version $Rev$ $Date$
 */
public class ComponentType<S extends ServiceDefinition, R extends ReferenceDefinition, P extends Property<?>>
    extends ModelObject {
    private boolean eagerInit;
    private final Map<String, S> services = new HashMap<String, S>();
    private final Map<String, R> references = new HashMap<String, R>();
    private final Map<String, P> properties = new HashMap<String, P>();

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void setEagerInit(boolean eagerInit) {
        this.eagerInit = eagerInit;
    }

    public Map<String, S> getServices() {
        return services;
    }

    public Map<String, R> getReferences() {
        return references;
    }

    public Map<String, P> getProperties() {
        return properties;
    }

    public void add(S service) {
        services.put(service.getName(), service);
    }

    public void add(R reference) {
        references.put(reference.getName(), reference);
    }

    public void add(P property) {
        properties.put(property.getName(), property);
    }
}
