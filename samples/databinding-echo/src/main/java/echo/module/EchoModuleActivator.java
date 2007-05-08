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

package echo.module;

import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.provider.ProviderFactoryExtensionPoint;

import echo.DefaultEchoBindingFactory;
import echo.EchoBindingFactory;
import echo.impl.EchoBindingProcessor;
import echo.provider.EchoBindingProviderFactory;
import echo.server.EchoServer;

/**
 * A module activator for the sample Echo binding extension.
 *
 * @version $Rev$ $Date$
 */
public class EchoModuleActivator implements ModuleActivator {
    
    public Map<Class, Object> getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        
        // Create the Echo model factory
        EchoBindingFactory echoFactory = new DefaultEchoBindingFactory();

        // Add the EchoProcessor extension
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        EchoBindingProcessor echoBindingProcessor = new EchoBindingProcessor(echoFactory);
        processors.addArtifactProcessor(echoBindingProcessor);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(new EchoBindingProviderFactory());
       
        // Start the Echo server
        EchoServer.start();
    }

    public void stop(ExtensionPointRegistry registry) {
        EchoServer.stop();
    }

}
