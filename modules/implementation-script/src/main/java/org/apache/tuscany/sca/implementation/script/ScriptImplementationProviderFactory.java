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
package org.apache.tuscany.sca.implementation.script;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.provider.ImplementationProviderFactory;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementationProviderFactory implements ImplementationProviderFactory<ScriptImplementation> {

    private PropertyValueObjectFactory propertyFactory;

    public ScriptImplementationProviderFactory(PropertyValueObjectFactory propertyFactory) {
        this.propertyFactory = propertyFactory;
    }

    public ImplementationProvider<ScriptImplementation> createImplementationProvider(RuntimeComponent component, ScriptImplementation implementation) {
        return new ScriptImplementationProvider(component, implementation, propertyFactory);
    }
    
    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }
}
