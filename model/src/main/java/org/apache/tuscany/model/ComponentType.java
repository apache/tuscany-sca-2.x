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
package org.apache.tuscany.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
public class ComponentType extends ModelObject {
    private final Map<String, Service> services = new HashMap<String, Service>();
    private final Map<String, Reference> references = new HashMap<String, Reference>();
    private final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

    public Map<String, Service> getServices() {
        return services;
    }

    public Map<String, Reference> getReferences() {
        return references;
    }

    public Map<String, Property<?>> getProperties() {
        return properties;
    }

    public void add(Service service) {
        services.put(service.getName(), service);
    }

    public void add(Reference reference) {
        references.put(reference.getName(), reference);
    }

    public void add(Property<?> property) {
        properties.put(property.getName(), property);
    }
}
