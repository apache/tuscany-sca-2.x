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

package org.apache.tuscany.sca.policy.transaction;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.provider.DefinitionsProvider;
import org.apache.tuscany.sca.provider.DefinitionsProviderException;

/**
 * Provider for Policy Intents and PolicySet definitions related to transaction
 *
 * @version $Rev$ $Date$
 */
public class TransactionPolicyDefinitionsProvider implements DefinitionsProvider {
    private static final String scaDefinitionsFile = "definitions.xml";
    private static final String tuscanyDefinitionsFile = "tuscany_definitions.xml";
    private URLArtifactProcessor urlArtifactProcessor;

    public TransactionPolicyDefinitionsProvider(ExtensionPointRegistry registry) {
        URLArtifactProcessorExtensionPoint documentProcessors =
            registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        urlArtifactProcessor = (URLArtifactProcessor)documentProcessors.getProcessor(Definitions.class);
    }

    public Definitions getDefinitions() throws DefinitionsProviderException {

        try {
            Definitions scaTransactionPolicyDefns = null;
            Definitions tuscanyTransactionPolicyDefns = null;

            URI uri = new URI(scaDefinitionsFile);
            URL defintionsFileUrl = getClass().getResource(scaDefinitionsFile);
            scaTransactionPolicyDefns = (Definitions)urlArtifactProcessor.read(null, uri, defintionsFileUrl);

            uri = new URI(tuscanyDefinitionsFile);
            defintionsFileUrl = getClass().getResource(tuscanyDefinitionsFile);
            tuscanyTransactionPolicyDefns = (Definitions)urlArtifactProcessor.read(null, uri, defintionsFileUrl);

            DefinitionsUtil.aggregate(tuscanyTransactionPolicyDefns, scaTransactionPolicyDefns);

            return scaTransactionPolicyDefns;
        } catch (Exception e) {
            throw new DefinitionsProviderException(e);
        }
    }

}
