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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.util.UriHelper;

/**
 * A specialization of component type for composite components.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentType<S extends ServiceDefinition,
    R extends ReferenceDefinition,
    P extends Property<?>> extends ComponentType<S, R, P> {

    private QName name;
    private boolean autowire;
    private final Map<String, ComponentDefinition<? extends Implementation<?>>> components =
        new HashMap<String, ComponentDefinition<? extends Implementation<?>>>();
    private final Map<String, Include> includes = new HashMap<String, Include>();
    private final List<WireDefinition> wires = new ArrayList<WireDefinition>();

    public CompositeComponentType() {
        implementationScope = Scope.SYSTEM;
    }

    /**
     * Constructor defining the composite name.
     *
     * @param name the qualified name of this composite
     */
    public CompositeComponentType(QName name) {
        this();
        this.name = name;
    }

    /**
     * Returns the qualified name of this composite.
     * The namespace portion of this name is the targetNamespace for other qualified names used in the composite.
     *
     * @return the qualified name of this composite
     */
    public QName getName() {
        return name;
    }

    /**
     * Set the qualified name of this composite.
     *
     * @param name the qualified name of this composite
     */
    public void setName(QName name) {
        this.name = name;
    }

    /**
     * Returns if autowire is set of for composite
     *
     * @return true if autowire is set for the composite
     */
    public boolean isAutowire() {
        return autowire;
    }

    /**
     * Sets autowire for the composite
     *
     * @param autowire true if autowire is enabled for the composite
     */
    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
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
     */
    @SuppressWarnings("unchecked")
    public List<WireDefinition> getWires() {
        List<WireDefinition> view =
            new ArrayList<WireDefinition>(wires);
        for (Include i : includes.values()) {
            view.addAll(i.getIncluded().getWires());
        }
        return Collections.unmodifiableList(view);
    }

    /**
     * Get declared properties in this composite type, included doesn't count
     */
    public Map<String, P> getDeclaredProperties() {
        return super.getProperties();
    }

    /**
     * Get declared references in this composite type, included doesn't count
     */
    public Map<String, R> getDeclaredReferences() {
        return super.getReferences();
    }

    /**
     * Get declared services in this composite type, included doesn't count
     */
    public Map<String, S> getDeclaredServices() {
        return super.getServices();
    }

    /**
     * Get declared components in this composite type, included doesn't count
     */
    public Map<String, ComponentDefinition<? extends Implementation<?>>> getDeclaredComponents() {
        return components;
    }

    /**
     * Get declared wires in this composite type, included doesn't count
     */
    public List<WireDefinition> getDeclaredWires() {
        return wires;
    }

    public void add(WireDefinition wireDefn) {
        wires.add(wireDefn);
    }


    public void add(ComponentDefinition<? extends Implementation<?>> componentDefinition) {
        components.put(UriHelper.getBaseName(componentDefinition.getUri()), componentDefinition);
    }

    public Map<String, Include> getIncludes() {
        return includes;
    }

    public void add(Include include) {
        includes.put(include.getName(), include);
    }


    public int hashCode() {
        return name.hashCode();
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeComponentType that = (CompositeComponentType) o;
        return name.equals(that.name);
    }
}
