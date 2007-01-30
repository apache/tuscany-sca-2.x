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
package org.apache.tuscany.core.deployer.federation;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;
import org.apache.tuscany.spi.services.discovery.RequestListener;

/**
 * Federated deployer that deploys components in response to asynchronous 
 * messages from the federated domain.
 * 
 * TODO Common abstractions between federated and local deployer.
 * 
 * @version $Revision$ $Date$
 *
 */
public class FederatedDeployer extends DeployerImpl implements RequestListener {
    
    /** QName of the message. */
    private static final QName MESSAGE_TYPE = new QName("http://www.osoa.org/xmlns/sca/1.0", "composite");
    
    /**
     * Deploys the SCDL.
     * @param content SCDL content.
     * @return Response to the request message.
     * 
     * TODO Handle response messages.
     */
    public XMLStreamReader onRequest(XMLStreamReader content) {
        
        // TODO get this from somewhere
        final CompositeComponent parent = null;
        
        final ScopeContainer scopeContainer = new CompositeScopeContainer(monitor);
        scopeContainer.start();
        
        final DeploymentContext deploymentContext = new RootDeploymentContext(null, xmlFactory, scopeContainer, null);

        try {
            final Component component = (Component) loader.load(parent, null, content, deploymentContext);
            // TODO Now the component is loaded, build and start it
        } catch (LoaderException ex) {
            return null;
        } catch (XMLStreamException ex) {
            return null;
        }
        
        return null;
    }
    
    /**
     * Injects the discovery service.
     * @param discoveryService Discovery service to be injected.
     */
    @Autowire
    public void setDiscoveryService(DiscoveryService discoveryService) {
        discoveryService.registerRequestListener(MESSAGE_TYPE, this);
    }

}
