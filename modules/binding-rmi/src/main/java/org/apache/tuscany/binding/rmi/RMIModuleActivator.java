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

package org.apache.tuscany.binding.rmi;

import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.binding.rmi.impl.DefaultRMIBindingFactory;
import org.apache.tuscany.binding.rmi.xml.RMIBindingProcessor;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.spi.builder.BuilderRegistry;

public class RMIModuleActivator implements ModuleActivator {

    private RMIBindingBuilder builder;

    public void start(ExtensionPointRegistry registry) {

        StAXArtifactProcessorExtensionPoint processors = 
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        RMIBindingFactory rmiFactory = new DefaultRMIBindingFactory();
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        processors.addArtifactProcessor(new RMIBindingProcessor(assemblyFactory, policyFactory, rmiFactory));

        RMIHostExtensionPoint rmiHost = registry.getExtensionPoint(RMIHostExtensionPoint.class);
        
        BuilderRegistry builderRegistry = registry.getExtensionPoint(BuilderRegistry.class);
        builder = new RMIBindingBuilder();
        builder.setBuilderRegistry(builderRegistry);
        builder.setRmiHost(rmiHost);
        builder.init();
    }

    public void stop(ExtensionPointRegistry registry) {
        //FIXME
        // release resources held by bindings
        // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
        // TODO: revisit this as part of the lifecycle work
        builder.destroy(); // release connections
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
