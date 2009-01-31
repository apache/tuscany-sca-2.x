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

package org.apache.tuscany.sca.implementation.script.provider;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.script.ScriptImplementation;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * An ImplementationProviderFactory for Script implementations.
 *
 * @version $Rev: $ $Date: $
 */
public class ScriptImplementationProviderFactory implements ImplementationProviderFactory<ScriptImplementation> {
    
    private ScriptPropertyFactory propertyFactory;
    
    public ScriptImplementationProviderFactory(ExtensionPointRegistry extensionPoints) {
        Mediator mediator = new MediatorImpl(extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class),
                                         extensionPoints.getExtensionPoint(TransformerExtensionPoint.class));
        propertyFactory = new ScriptPropertyFactory(mediator);
    }

    public ImplementationProvider createImplementationProvider(RuntimeComponent component, ScriptImplementation Implementation) {
        return new ScriptImplementationProvider(component, Implementation, propertyFactory);
    }
    
    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }
    
}
