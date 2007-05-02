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

package org.apache.tuscany.implementation.script;

import java.util.Map;

import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.impl.DefaultMediator;
import org.apache.tuscany.spi.builder.BuilderRegistry;

public class ScriptModuleActivator implements ModuleActivator {

    private ScriptArtifactProcessor scriptArtifactProcessor;
    private ScriptComponentBuilder builder;
    
    public void start(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        BuilderRegistry builderRegistry = registry.getExtensionPoint(BuilderRegistry.class);
        
        scriptArtifactProcessor = new ScriptArtifactProcessor(new DefaultAssemblyFactory());
        processors.addExtension(scriptArtifactProcessor);

        builder = new ScriptComponentBuilder();
        builder.setBuilderRegistry(builderRegistry);
        
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class); 
        DefaultMediator mediator = new DefaultMediator(dataBindings, transformers);
        ScriptPropertyValueObjectFactory factory = new ScriptPropertyValueObjectFactory(mediator);
        builder.setPropertyValueObjectFactory(factory);

        DataBindingExtensionPoint dataBindingRegistry = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        builder.setDataBindingRegistry(dataBindingRegistry);
        
        builder.init();
    }

    public void stop(ExtensionPointRegistry arg0) {
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
