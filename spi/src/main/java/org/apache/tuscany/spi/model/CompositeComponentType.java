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
package org.apache.tuscany.spi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A specialization of component type for composite components.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentType<S extends ServiceDefinition,
    R extends ReferenceDefinition,
    P extends Property<?>> extends ComponentType<S, R, P> {

    private String name;
    private final Map<String, ComponentDefinition<? extends Implementation<?>>> components =
        new HashMap<String, ComponentDefinition<? extends Implementation<?>>>();
    private final Map<String, Include> includes = new HashMap<String, Include>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ComponentDefinition<? extends Implementation<?>>> getComponents() {
        return components;
    }

    public void add(ComponentDefinition<? extends Implementation<?>> componentDefinition) {
        components.put(componentDefinition.getName(), componentDefinition);
    }

    public Map<String, Include> getIncludes() {
        return includes;
    }

    public void add(Include include) {
        includes.put(include.getName(), include);
    }
}
