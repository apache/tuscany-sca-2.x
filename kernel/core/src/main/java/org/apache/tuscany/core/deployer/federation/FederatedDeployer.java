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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;
import org.apache.tuscany.spi.services.discovery.RequestListener;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * Federated deployer that deploys components in response to asynchronous messages from the federated domain.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class FederatedDeployer implements RequestListener {

    /**
     * Marshaller registry.
     */
    private ModelMarshallerRegistry marshallerRegistry;

    /**
     * Physical component builder registry.
     */
    private PhysicalComponentBuilderRegistry builderRegistry;
    
    /**
     * Component manager.
     */
    private ComponentManager componentManager;
    
    /**
     * Connector.
     */
    private Connector connector;

    /**
     * Deploys the component.
     *
     * @param content SCDL content.
     * @return Response to the request message.
     *         <p/>
     *         TODO Handle response messages.
     */
    public XMLStreamReader onRequest(XMLStreamReader content) {

        try {

            // Get to the document element
            while(content.next() != START_ELEMENT) {            
            }
            
            final PhysicalChangeSet changeSet = (PhysicalChangeSet) marshallerRegistry.unmarshall(content);
            applyChangeSet(changeSet);
        } catch (MarshalException ex) {
            return null;
        } catch (BuilderException ex) {
            return null;
        } catch (RegistrationException ex) {
            return null;
        } catch (XMLStreamException ex) {
            return null;
        } catch (RuntimeException ex) {
            // FIXME log this
            ex.printStackTrace();
            return null;
        }

        return null;
    }

    public void applyChangeSet(PhysicalChangeSet changeSet) throws BuilderException, RegistrationException {
        Set<PhysicalComponentDefinition> componentDefinitions = changeSet.getComponentDefinitions();
        List<Component> components = new ArrayList<Component>(componentDefinitions.size());
        for (PhysicalComponentDefinition pcd : componentDefinitions) {
            final Component component = builderRegistry.build(pcd);
            components.add(component);
        }
        for (Component component : components) {
            componentManager.register(component);
        }
        for (PhysicalWireDefinition pwd : changeSet.getWireDefinitions()) {
            connector.connect(pwd);
        }
        for (Component component : components) {
            component.start();
        }
    }

    /**
     * Injects the discovery service.
     * @param discoveryService Discovery service to be injected.
     */
    @Reference
    public void setDiscoveryService(DiscoveryService discoveryService) {
        discoveryService.registerRequestListener(PhysicalChangeSetMarshaller.QNAME, this);
    }

    /**
     * Injects the model marshaller registry.
     * @param marshallerRegistry Marshaller registry.
     */
    @Reference
    public void setMarshallerRegistry(ModelMarshallerRegistry marshallerRegistry) {
        this.marshallerRegistry = marshallerRegistry;
    }

    /**
     * Injects the builder registry.
     * @param builderRegistry Builder registry.
     */
    @Reference
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    /**
     * Injects the component manager.
     * @param componentManager Component manager.
     */
    @Reference
    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    /**
     * Injects the connector.
     * @param connector Connector.
     */
    @Reference
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

}
