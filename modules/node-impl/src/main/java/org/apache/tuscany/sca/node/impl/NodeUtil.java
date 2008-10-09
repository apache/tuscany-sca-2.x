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

package org.apache.tuscany.sca.node.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * NodeUtil
 *
 * @version $Rev: $ $Date: $
 */
public class NodeUtil {
    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());

    static Contribution contribution(ContributionFactory contributionFactory, org.apache.tuscany.sca.node.Contribution c) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(c.getURI());
        contribution.setLocation(c.getLocation());
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    static URI createURI(String uri) {
        if (uri.indexOf(' ') != -1) {
            uri = uri.replace(" ", "%20");
        }
        return URI.create(uri);
    }

//    private void loadSCADefinitions() throws ActivationException {
//        try {
//            URLArtifactProcessorExtensionPoint documentProcessors =
//                registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
//            URLArtifactProcessor<SCADefinitions> definitionsProcessor =
//                documentProcessors.getProcessor(SCADefinitions.class);
//            SCADefinitionsProviderExtensionPoint scaDefnProviders =
//                registry.getExtensionPoint(SCADefinitionsProviderExtensionPoint.class);
//
//            SCADefinitions systemSCADefinitions = new SCADefinitionsImpl();
//            SCADefinitions aSCADefn = null;
//            for (SCADefinitionsProvider aProvider : scaDefnProviders.getSCADefinitionsProviders()) {
//                aSCADefn = aProvider.getSCADefinition();
//                SCADefinitionsUtil.aggregateSCADefinitions(aSCADefn, systemSCADefinitions);
//            }
//
//            policyDefinitions.add(systemSCADefinitions);
//
//            //we cannot expect that providers will add the intents and policysets into the resolver
//            //so we do this here explicitly
//            for (Intent intent : systemSCADefinitions.getPolicyIntents()) {
//                policyDefinitionsResolver.addModel(intent);
//            }
//
//            for (PolicySet policySet : systemSCADefinitions.getPolicySets()) {
//                policyDefinitionsResolver.addModel(policySet);
//            }
//
//            for (IntentAttachPointType attachPoinType : systemSCADefinitions.getBindingTypes()) {
//                policyDefinitionsResolver.addModel(attachPoinType);
//            }
//
//            for (IntentAttachPointType attachPoinType : systemSCADefinitions.getImplementationTypes()) {
//                policyDefinitionsResolver.addModel(attachPoinType);
//            }
//
//            //now that all system sca definitions have been read, lets resolve them right away
//            definitionsProcessor.resolve(systemSCADefinitions, policyDefinitionsResolver);
//            
//        } catch (Exception e) {
//            throw new ActivationException(e);
//        }
//    }

}
