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

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;
import org.apache.tuscany.spi.services.discovery.RequestListener;

/**
 * Federated deployer that deploys components in response to asynchronous messages from the federated domain.
 *
 * @version $Revision$ $Date$
 */
public abstract class FederatedDeployer<PCD extends PhysicalComponentDefinition, C extends Component> implements
    RequestListener {

    /**
     * Marshaller registry.
     */
    private ModelMarshallerRegistry marshallerRegistry;

    /**
     * Physical component builder registry.
     */
    private PhysicalComponentBuilderRegistry builderRegistry;

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

            final PCD definition = unmarshallDefinition(content);
            final C component = buildComponent(definition);
            component.start();

        } catch (MarshallException ex) {
            return null;
        } catch (BuilderException ex) {
            return null;
        }

        return null;
    }

    /**
     * Injects the discovery service.
     *
     * @param discoveryService Discovery service to be injected.
     */
    @Autowire
    public void setDiscoveryService(DiscoveryService discoveryService) {
        QName messageType = getQualifiedName();
        discoveryService.registerRequestListener(messageType, this);
    }

    /**
     * Injects the model marshaller registry.
     *
     * @param marshallerRegistry Marshaller registry.
     */
    @Autowire
    public void setMarshallerRegistry(ModelMarshallerRegistry marshallerRegistry) {
        this.marshallerRegistry = marshallerRegistry;
    }

    /**
     * Injects the builder registry.
     *
     * @param builderRegistry Builder registry.
     */
    @Autowire
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    /**
     * Gets the builder registry.
     *
     * @return Return the builder registry.
     */
    protected PhysicalComponentBuilderRegistry getBuilderRegistry() {
        return builderRegistry;
    }

    /**
     * Gets the marshaller registry.
     *
     * @return Return the marshaller registry.
     */
    protected ModelMarshallerRegistry getMarshallerRegistry() {
        return marshallerRegistry;
    }

    /**
     * Returns the qualified name of the root element of the marshalled component definition this deployer is interested
     * in.
     *
     * @return The qualified name of the document element.
     */
    protected abstract QName getQualifiedName();

    /**
     * Unmarshalls the XML stream to a component definition.
     *
     * @param content XML content stream.
     * @return Physical component definition.
     * @throws MarshallException If unable to marshall the component definition.
     */
    protected abstract PCD unmarshallDefinition(XMLStreamReader content) throws MarshallException;

    /**
     * Builds the component from the physical component definition.
     *
     * @param componentDefinition Component definition.
     * @return Component instance.
     * @throws BuilderException If unable to build the component.
     */
    protected abstract C buildComponent(PCD componentDefinition) throws BuilderException;

}
