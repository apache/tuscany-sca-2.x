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

package echo;

import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;

/**
 * A module activator for the sample Echo binding extension.
 *
 * @version $Rev$ $Date$
 */
public class EchoModuleActivator implements ModuleActivator {
    private final EchoBindingProcessor echoBindingProcessor = new EchoBindingProcessor();

    public Map<Class, Object> getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {

        // Add the EchoProcessor extension
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addExtension(echoBindingProcessor);
       
        // Start the Echo server
        EchoServer.start();
    }

    public void stop(ExtensionPointRegistry registry) {
        
        // Remove the EchoProcessor from the registry
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.removeExtension(echoBindingProcessor);

        EchoServer.stop();
    }

}
