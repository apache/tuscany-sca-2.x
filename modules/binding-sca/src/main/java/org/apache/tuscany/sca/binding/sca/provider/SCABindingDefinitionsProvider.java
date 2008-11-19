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

package org.apache.tuscany.sca.binding.sca.provider;

import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.provider.DefinitionsProvider;
import org.apache.tuscany.sca.provider.DefinitionsProviderException;

/**
 * Provider for Policy Intents and PolicySet definitions related to security
 *
 * @version $Rev$ $Date$
 */
public class SCABindingDefinitionsProvider implements DefinitionsProvider {
    private String definitionsFile = "org/apache/tuscany/sca/binding/sca/definitions.xml";
    URLArtifactProcessor urlArtifactProcessor = null;
    
    public SCABindingDefinitionsProvider(ExtensionPointRegistry registry) {
        URLArtifactProcessorExtensionPoint documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        urlArtifactProcessor = (URLArtifactProcessor)documentProcessors.getProcessor(Definitions.class);
    }

    public Definitions getDefinitions() throws DefinitionsProviderException {
        // Allow privileged access to load resource. Requires RuntimePermssion in security policy.
        final URL definitionsFileUrl = AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return getClass().getClassLoader().getResource(definitionsFile);
            }
        });

        Definitions scaDefn = null;
        try {
            final URI uri = new URI(definitionsFile);
            // Allow bindings to read properties. Requires PropertyPermission read in security policy. 
            scaDefn = AccessController.doPrivileged(new PrivilegedExceptionAction<Definitions>() {
                public Definitions run() throws ContributionReadException {
                    return (Definitions)urlArtifactProcessor.read(null, uri, definitionsFileUrl);
                }
            });
        } catch (Exception e) {
            throw new DefinitionsProviderException(e);
        }
        return scaDefn;
    }

}
