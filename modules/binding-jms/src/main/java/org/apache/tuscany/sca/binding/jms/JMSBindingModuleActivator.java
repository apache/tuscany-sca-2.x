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

package org.apache.tuscany.sca.binding.jms;

/*
import org.apache.tuscany.sca.binding.jsonrpc.DefaultJSONRPCBindingFactory;
import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBindingFactory;
import org.apache.tuscany.sca.binding.jsonrpc.impl.JSONRPCBindingProcessor;
import org.apache.tuscany.sca.binding.jsonrpc.provider.JSONRPCBindingProviderFactory;
*/
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * A module activator for the JMS binding extension.
 *
 * @version $Rev$ $Date$
 */
public class JMSBindingModuleActivator implements ModuleActivator {
    
    public Object[] getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        // Create the JMS model factory
        JMSBindingFactory bindingFactory = new JMSBindingFactoryImpl();

        // Add the JMSBindingProcessor extension
        AssemblyFactory assemblyFactory = registry.getExtensionPoint(AssemblyFactory.class); 
        PolicyFactory policyFactory = registry.getExtensionPoint(PolicyFactory.class);
        JMSBindingProcessor bindingProcessor = new JMSBindingProcessor(assemblyFactory,
                                                                          policyFactory,
                                                                          bindingFactory);
        
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(bindingProcessor);       
 
        // Add the JMSBindingProviderFactory extension
        JMSBindingProviderFactory providerFactory = new JMSBindingProviderFactory();
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(providerFactory);
    }

    public void stop(ExtensionPointRegistry registry) {        
    }

}
