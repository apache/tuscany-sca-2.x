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
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.deployer.DeployerImpl;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshaller;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;
import org.apache.tuscany.spi.services.discovery.RequestListener;

/**
 * Federated deployer that deploys components in response to asynchronous 
 * messages from the federated domain.
 * 
 * @version $Revision$ $Date$
 *
 */
public class FederatedDeployer extends DeployerImpl implements RequestListener {
    
    /** QName of the message. */
    private static final QName MESSAGE_TYPE = new QName("http://www.osoa.org/xmlns/sca/1.0", "composite");
    
    /** Marshaller. */
    private ModelMarshaller<ComponentDefinition<?>> marshaller;
    
    /**
     * Deploys the component.
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
            
            final ComponentDefinition<?> definition = marshaller.unmarshall(content, false);
            final Component component =  (Component) builder.build(parent, definition, deploymentContext);
            
            component.prepare();
            component.start();
            
        } catch (MarshalException ex) {
            return null;
        } catch (BuilderException ex) {
            return null;
        } catch (PrepareException ex) {
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
    
    /**
     * Injects the model marshaller.
     * @param marshaller Marshaller.
     */
    @Autowire
    public void setMarshaller(ModelMarshaller<ComponentDefinition<?>> marshaller) {
        this.marshaller = marshaller;
    }

}
