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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.provider.SCADefinitionsProvider;
import org.apache.tuscany.sca.provider.SCADefinitionsProviderException;

/**
 * Provider for Policy Intents and PolicySet definitions related to security
 */
public class WSBindingDefinitionsProvider implements SCADefinitionsProvider {
    private String definitionsFile = "org/apache/tuscany/sca/binding/ws/axis2/definitions.xml";
    URLArtifactProcessor urlArtifactProcessor = null;
    
    public WSBindingDefinitionsProvider(ExtensionPointRegistry registry) {
        URLArtifactProcessorExtensionPoint documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        urlArtifactProcessor = (URLArtifactProcessor)documentProcessors.getProcessor(SCADefinitions.class);
    }

    public SCADefinitions getSCADefinition() throws SCADefinitionsProviderException {
        URL defintionsFileUrl = getClass().getClassLoader().getResource(definitionsFile);
        Object scaDefn = null;
        try {
            URI uri = new URI(definitionsFile);
            return (SCADefinitions)urlArtifactProcessor.read(null, 
                                                             uri, 
                                                             defintionsFileUrl);
        } catch ( Exception e ) {
            throw new SCADefinitionsProviderException(e);
        }
    }

}
