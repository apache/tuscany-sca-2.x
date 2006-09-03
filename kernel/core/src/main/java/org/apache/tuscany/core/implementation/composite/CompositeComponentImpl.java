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

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.builder.Connector;

/**
 * The standard implementation of a composite component. Autowiring is performed by delegating to the parent composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentImpl<T> extends AbstractCompositeComponent<T> {
    private String uri;

    public CompositeComponentImpl(String name,
                                  CompositeComponent parent,
                                  AutowireComponent autowireContext,
                                  Connector connector,
                                  Map<String, Document> propertyValues) {
        this(name, null, parent, autowireContext, connector, propertyValues);
    }

    /**
     * Constructor specifying name and URI.
     *
     * @param name              the name of this Component


     @param uri               the unique identifier for this component


      * @param parent            this component's parent

     @param autowireComponent the component that should be used to resolve autowired references


      * @param connector
     * @param propertyValues    this composite's Property values

     */
    public CompositeComponentImpl(String name,
                                  String uri,
                                  CompositeComponent parent,
                                  AutowireComponent autowireComponent,
                                  Connector connector,
                                  Map<String, Document> propertyValues) {
        super(name, parent, autowireComponent, connector, propertyValues);
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }
}
