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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportRegistration;

/**
 * Watching and exporting OSGi services 
 */
public class OSGiServiceImporter extends AbstractOSGiServiceHandler {
    private final static Logger logger = Logger.getLogger(OSGiServiceImporter.class.getName());
    private Map<EndpointDescription, ImportReferenceImpl> importReferences =
        new ConcurrentHashMap<EndpointDescription, ImportReferenceImpl>();

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    public OSGiServiceImporter(BundleContext context) {
        super(context);
    }

    public void start() {
        // Defer init() to importService()
    }

    public void stop() {
        importReferences.clear();
        super.stop();
    }

    public ImportRegistration importService(Bundle bundle, EndpointDescription endpointDescription) {
        init();
        try {
            Contribution contribution = introspector.introspect(bundle, endpointDescription);
            if (contribution != null) {

                NodeConfiguration configuration = nodeFactory.createNodeConfiguration();
                if (domainRegistry != null) {
                    configuration.setDomainRegistryURI(domainRegistry);
                }
                if (domainURI != null) {
                    configuration.setDomainURI(domainURI);
                }
                configuration.setURI(contribution.getURI());
                // configuration.getExtensions().add(bundle);
                // FIXME: Configure the domain and node URI
                NodeImpl node = new NodeImpl(nodeFactory, configuration, Collections.singletonList(contribution));
                node.start();

                Component component = contribution.getDeployables().get(0).getComponents().get(0);
                ComponentReference componentReference = component.getReferences().get(0);
                ServiceReference serviceReference =
                    context.getServiceReference("(sca.reference=" + component.getURI()
                        + "#reference("
                        + componentReference.getName()
                        + ")");
                synchronized (this) {
                    ImportReferenceImpl importReference = importReferences.get(endpointDescription);
                    if (importReference == null) {
                        importReference = new ImportReferenceImpl(node, serviceReference, endpointDescription);
                        importReferences.put(endpointDescription, importReference);
                    }
                    return importReference.register();
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ImportRegistrationImpl(null, e);
        }
    }

    public void unimportService(ImportRegistration importRegistration) {
        if (importRegistration != null) {
            importRegistration.close();
        }
    }

}
