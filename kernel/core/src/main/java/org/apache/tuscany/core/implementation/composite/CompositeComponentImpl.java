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
package org.apache.tuscany.core.implementation.composite;

import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;

/**
 * The standard implementation of a composite component. Autowiring is performed by delegating to the parent composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentImpl extends AbstractCompositeComponent {
    private String uri;
    private boolean systemComposite;

    /**
     * Constructor specifying property values
     *
     * @param name           the name of this Component
     * @param parent         this component's parent
     * @param connector      the connector to use for wires
     * @param propertyValues this composite's Property values
     */
    public CompositeComponentImpl(String name,
                                  CompositeComponent parent,
                                  Connector connector,
                                  Map<String, Document> propertyValues) {
        this(name, null, parent, connector, propertyValues);
    }

    /**
     * Constructor specifying if the composite is a system composite
     *
     * @param name            the name of this Component
     * @param parent          this component's parent
     * @param connector       the connector to use for wires
     * @param systemComposite true if the composite is a system composite
     */
    public CompositeComponentImpl(String name,
                                  CompositeComponent parent,
                                  Connector connector,
                                  boolean systemComposite) {
        this(name, null, parent, connector, null, systemComposite);
    }

    /**
     * Constructor specifying name and URI.
     *
     * @param name           the name of this Component
     * @param uri            the unique identifier for this component
     * @param parent         this component's parent
     * @param connector      the connector to use for wires
     * @param propertyValues this composite's Property values
     */
    public CompositeComponentImpl(String name,
                                  String uri,
                                  CompositeComponent parent,
                                  Connector connector,
                                  Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
        this.uri = uri;
    }

    /**
     * Constructor specifying name and URI.
     *
     * @param name            the name of this Component
     * @param uri             the unique identifier for this component
     * @param parent          this component's parent
     * @param connector       the connector to use for wires
     * @param propertyValues  this composite's Property values
     * @param systemComposite true if the composite is a system composite
     */
    public CompositeComponentImpl(String name,
                                  String uri,
                                  CompositeComponent parent,
                                  Connector connector,
                                  Map<String, Document> propertyValues,
                                  boolean systemComposite) {
        super(name, parent, connector, propertyValues);
        this.uri = uri;
        this.systemComposite = systemComposite;
    }

    public String getURI() {
        return uri;
    }

    public boolean isSystem() {
        return systemComposite;
    }

}
