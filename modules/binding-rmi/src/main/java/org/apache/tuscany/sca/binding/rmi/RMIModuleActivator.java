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

package org.apache.tuscany.sca.binding.rmi;

import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.rmi.RMIHost;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.binding.rmi.xml.RMIBindingProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

public class RMIModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {

        StAXArtifactProcessorExtensionPoint processors = 
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        
        RMIHost rmiHost = registry.getExtensionPoint(RMIHost.class);
        RMIBindingFactory rmiFactory = new DefaultRMIBindingFactory();
        processors.addArtifactProcessor(new RMIBindingProcessor(assemblyFactory, policyFactory, rmiFactory));
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        MessageFactory messageFactory = new MessageFactoryImpl();
        providerFactories.addProviderFactory(new RMIBindingProviderFactory(messageFactory, rmiHost));
    }

    public void stop(ExtensionPointRegistry registry) {
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
