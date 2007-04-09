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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a component. <p>A component is a configured instance of an implementation. The services provided and
 * consumed and the available configuration properties are defined by the implementation (represented by its
 * componentType).</p> <p>Every component has a name which uniquely identifies it within the scope of the composite that
 * contains it; the name must be different from the names of all other components, services and references immediately
 * contained in the composite (directly or through an &lt;include&gt; element).</p> <p>A component may define a {@link
 * PropertyValue} that overrides the default value of a {@link Property} defined in the componentType.</p> <p>It may
 * also define a {@link ReferenceTarget} for a {@link ReferenceDefinition} defined in the componentType. The
 * ReferenceTarget must resolve to another component or a reference in the enclosing composite.</p> <p>Components may
 * specify an initialization level that will determine the order in which it will be eagerly initialized relative to
 * other components from the enclosing composite that are in the same scope. This can be used to define a startup
 * sequence for components that are otherwise independent. Any initialization required to resolve references between
 * components will override this initialization order.</p>
 *
 * @version $Rev$ $Date$
 */
public class ComponentDefinition<I extends Implementation<?>> extends ModelObject {
    private URI uri;
    private URI runtimeId;
    private boolean autowire;
    private Integer initLevel;
    private final I implementation;
    private final Map<String, ReferenceTarget> referenceTargets = new HashMap<String, ReferenceTarget>();
    private final Map<String, PropertyValue<?>> propertyValues = new HashMap<String, PropertyValue<?>>();

    /**
     * Constructor specifying the component's name and implementation.
     *
     * @param uri            the name of this component
     * @param implementation the implementation of this component
     */
    public ComponentDefinition(URI uri, I implementation) {
        this.uri = uri;
        this.implementation = implementation;
    }

    /**
     * Constructor specifying the implementation of this component.
     *
     * @param implementation the implementation of this component
     */
    public ComponentDefinition(I implementation) {
        this.implementation = implementation;
    }

    /**
     * Returns the {@link Implementation} of this component.
     *
     * @return the implementation of this component
     */
    public I getImplementation() {
        return implementation;
    }

    /**
     * Returns the name of this component.
     *
     * @return the name of this component
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the name of this component.
     *
     * @param uri the name of this component
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the id of the node the component is to be provisioned to.
     *
     * @return the id of the node the component is to be provisioned to
     */
    public URI getRuntimeId() {
        return runtimeId;
    }

    /**
     * Sets the id of the node the component is to be provisioned to.
     *
     * @param id the id of the node the component is to be provisioned to
     */
    public void setRuntimeId(URI id) {
        this.runtimeId = id;
    }

    /**
     * Returns true if autowire is enabled for the component.
     *
     * @return true if autowire is enabled for the component.
     */
    public boolean getAutowire() {
        return autowire;
    }

    /**
     * Sets autowire enablement for the component.
     *
     * @param autowire true if autowire is enabled.
     */
    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    /**
     * Returns the initialization level of this component.
     *
     * @return the initialization level of this component
     */
    public Integer getInitLevel() {
        return initLevel;
    }

    /**
     * Sets the initialization level of this component. If set to null then the level from the componentType is used. If
     * set to zero or a negative value then the component will not be eagerly initialized.
     *
     * @param initLevel the initialization level of this component
     */
    public void setInitLevel(Integer initLevel) {
        this.initLevel = initLevel;
    }

    /**
     * Returns a live Map of the {@link ReferenceTarget targets} configured by this component definition.
     *
     * @return the reference targets configured by this component
     */
    public Map<String, ReferenceTarget> getReferenceTargets() {
        return referenceTargets;
    }

    /**
     * Add a reference target configuration to this component. Any existing configuration for the reference named in the
     * target is replaced.
     *
     * @param target the target to add
     */
    public void add(ReferenceTarget target) {
        referenceTargets.put(target.getReferenceName().getFragment(), target);
    }

    /**
     * Returns a live Map of {@link PropertyValue property values} configured by this component definition.
     *
     * @return the property values configured by this component
     */
    public Map<String, PropertyValue<?>> getPropertyValues() {
        return propertyValues;
    }

    /**
     * Add a property value configuration to this component. Any existing configuration for the property names in the
     * property value is replaced.
     *
     * @param value the property value to add
     */
    public void add(PropertyValue<?> value) {
        propertyValues.put(value.getName(), value);
    }
}
