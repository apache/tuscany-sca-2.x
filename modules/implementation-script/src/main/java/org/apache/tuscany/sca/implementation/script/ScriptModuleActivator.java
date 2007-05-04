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

import java.util.Map;

import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.impl.DefaultMediator;
import org.apache.tuscany.implementation.spi.PropertyValueObjectFactory;

public class ScriptModuleActivator implements ModuleActivator {

    protected ScriptArtifactProcessor scriptArtifactProcessor;
    
    public void start(ExtensionPointRegistry registry) {

        // TODO: could the runtime have a default PropertyValueObjectFactory in the registry
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class); 
        DefaultMediator mediator = new DefaultMediator(dataBindings, transformers);
        PropertyValueObjectFactory propertyFactory = new PropertyValueObjectFactory(mediator);

        scriptArtifactProcessor = new ScriptArtifactProcessor(new DefaultAssemblyFactory(), propertyFactory);

        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(scriptArtifactProcessor);
    }

    public void stop(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.removeArtifactProcessor(scriptArtifactProcessor);
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
