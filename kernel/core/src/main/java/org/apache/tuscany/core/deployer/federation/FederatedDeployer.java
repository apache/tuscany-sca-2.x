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

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshaller;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;
import org.apache.tuscany.spi.services.discovery.RequestListener;
import org.apache.tuscany.spi.util.stax.StaxUtil;

/**
 * Federated deployer that deploys components in response to asynchronous 
 * messages from the federated domain.
 * 
 * @version $Revision$ $Date$
 *
 */
public class FederatedDeployer implements RequestListener {
    
    /** QName of the message. */
    private static final QName MESSAGE_TYPE = new QName("http://www.osoa.org/xmlns/sca/1.0", "composite");
    
    /** Marshaller registry. */
    private ModelMarshallerRegistry marshallerRegistry;
    
    /** Physical component builder registry. */
    private PhysicalComponentBuilderRegistry builderRegistry;
    
    /**
     * Deploys the component.
     * @param content SCDL content.
     * @return Response to the request message.
     * 
     * TODO Handle response messages.
     */
    @SuppressWarnings("unchecked")
    public XMLStreamReader onRequest(XMLStreamReader content) {
        
        try {
            
            final QName qualifiedName = StaxUtil.getDocumentElementQName(content);
            
            final ModelMarshaller<PhysicalComponentDefinition> marshaller = marshallerRegistry.getMarshaller(qualifiedName);
            final PhysicalComponentDefinition definition = marshaller.unmarshall(content);
            
            final PhysicalComponentBuilder builder = builderRegistry.getBuilder(definition.getClass());
            final Component component = builder.build(definition);
            
            component.prepare();
            component.start();
            
        } catch (MarshalException ex) {
            return null;
        } catch (XMLStreamException ex) {
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
     * Injects the model marshaller registry.
     * @param marshallerRegistry Marshaller registry.
     */
    @Autowire
    public void setMarshallerRegistry(ModelMarshallerRegistry marshallerRegistry) {
        this.marshallerRegistry = marshallerRegistry;
    }
    
    /**
     * Injects the builder registry.
     * @param builderRegistry Builder registry.
     */
    @Autowire
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

}
