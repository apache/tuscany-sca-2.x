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
package org.apache.tuscany.core.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;

/**
 * The default implementation of the Tuscany runtime component
 *
 * @version $Rev$ $Date$
 */
public class DefaultRuntime extends CompositeComponentImpl implements RuntimeComponent {
    private CompositeComponent rootComponent;
    private CompositeComponent systemComponent;

    /**
     * Initialize a default runtime with an empty set of Property values.
     */
    public DefaultRuntime() {
        this(new HashMap<String, Document>());
    }

    /**
     * Initialize a runtime with the a set of properties
     *
     * @param runtimeProperties Property values for the runtime itself
     */
    public DefaultRuntime(Map<String, Document> runtimeProperties) {
        super(ComponentNames.TUSCANY_RUNTIME, null, null, runtimeProperties);
    }

    protected void setRootComponent(CompositeComponent rootComponent) {
        this.rootComponent = rootComponent;
    }

    protected void setSystemComponent(CompositeComponent systemComponent) {
        this.systemComponent = systemComponent;
    }

    public CompositeComponent getRootComponent() {
        return rootComponent;
    }

    public CompositeComponent getSystemComponent() {
        return systemComponent;
    }

    public Deployer getDeployer() {
        return systemComponent.resolveExternalInstance(Deployer.class);
    }
}
