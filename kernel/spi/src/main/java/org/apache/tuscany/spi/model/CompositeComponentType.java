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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
    private final Vector<WireDefinition> wires = new Vector<WireDefinition>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    /**
     * Get all properties including the ones are from included composites
     * @return 
     */
    public Map<String, P> getProperties() {
        Map<String, P> view = new HashMap<String, P>(super.getProperties());
        for (Include i : includes.values()) {
            view.putAll(i.getIncluded().getProperties());
        }
        return Collections.unmodifiableMap(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    /**
     * Get all references including the ones are from included composites
     * @return 
     */
    public Map<String, R> getReferences() {
        Map<String, R> view = new HashMap<String, R>(super.getReferences());
        for (Include i : includes.values()) {
            view.putAll(i.getIncluded().getReferences());
        }
        return Collections.unmodifiableMap(view);
    }

    @SuppressWarnings("unchecked")
    @Override
    /**
     * Get all services including the ones are from included composites
     * @return 
     */
    public Map<String, S> getServices() {
        Map<String, S> view = new HashMap<String, S>(super.getServices());
        for (Include i : includes.values()) {
            view.putAll(i.getIncluded().getServices());
        }
        return Collections.unmodifiableMap(view);
    }

    /**
     * Get all components including the ones are from included composites
     * @return 
     */
    @SuppressWarnings("unchecked")
    public Map<String, ComponentDefinition<? extends Implementation<?>>> getComponents() {
        Map<String, ComponentDefinition<? extends Implementation<?>>> view =
                new HashMap<String, ComponentDefinition<? extends Implementation<?>>>(components);
        for (Include i : includes.values()) {
            view.putAll(i.getIncluded().getComponents());
        }
        return Collections.unmodifiableMap(view);
    }

    
    /**
     * Get all wires including the ones are from included composites
     * @return 
     */
    @SuppressWarnings("unchecked")
    public List<WireDefinition> getWires() {
        List<WireDefinition> view =
                new Vector<WireDefinition>(wires);
        for (Include i : includes.values()) {
            view.addAll(i.getIncluded().getWires());
        }
        return Collections.unmodifiableList(view);
    }
    
    /**
     * Get declared properties in this composite type, included doesn't count
     * @return
     */
    public Map<String, P> getDeclaredProperties() {
        return super.getProperties();
    }

    /**
     * Get declared references in this composite type, included doesn't count
     * @return
     */
    public Map<String, R> getDeclaredReferences() {
        return super.getReferences();
    }

    /**
     * Get declared services in this composite type, included doesn't count
     * @return
     */
    public Map<String, S> getDeclaredServices() {
        return super.getServices();
    }

    /**
     * Get declared components in this composite type, included doesn't count
     * @return
     */
    public Map<String, ComponentDefinition<? extends Implementation<?>>> getDeclaredComponents() {
        return components;
    }
    
    /**
     * Get declared wires in this composite type, included doesn't count
     * @return
     */
    public List<WireDefinition> getDeclaredWires() {
        return wires;
    }
    
    public void add(WireDefinition wireDefn) {
        wires.add(wireDefn);
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
