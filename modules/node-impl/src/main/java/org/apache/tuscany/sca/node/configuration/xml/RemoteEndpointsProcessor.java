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

package org.apache.tuscany.sca.node.configuration.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.xml.BaseAssemblyProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * A StaX processor for remote endpoints
 */
public class RemoteEndpointsProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<RemoteEndpoints> {
    private final static String REMOTE_ENDPOINTS = "remoteEndpoints";
    private final static QName REMOTE_ENDPOINTS_QNAME = new QName(Constants.SCA11_TUSCANY_NS, REMOTE_ENDPOINTS);

    private ExtensionPointRegistry registry;

    public RemoteEndpointsProcessor(ExtensionPointRegistry registry,
                                    StAXArtifactProcessor extensionProcessor,
                                    StAXAttributeProcessor extensionAttributeProcessor) {

        super(modelFactories(registry), extensionProcessor);
        this.registry = registry;
    }

    /**
     * Returns the model factory extension point to use.
     *
     * @param extensionPoints
     * @return
     */
    private static FactoryExtensionPoint modelFactories(ExtensionPointRegistry extensionPoints) {
        return extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
    }

    public QName getArtifactType() {
        return REMOTE_ENDPOINTS_QNAME;
    }

    public RemoteEndpoints read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException,
        XMLStreamException {
        RemoteEndpoints endpoints = new RemoteEndpoints();
        if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            reader.nextTag();
        }
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && REMOTE_ENDPOINTS_QNAME.equals(reader.getName())) {
            // Skip the "endpoint" element wrapper
            reader.nextTag();
        }
        Object model = extensionProcessor.read(reader, context);
        if (model instanceof Composite) {
            Composite composite = (Composite)model;
            endpoints.setComposite(composite);
            for (Component component : composite.getComponents()) {
                if (component.getURI() == null) {
                    // Default to the component name as the uri
                    component.setURI(component.getName());
                }
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        Endpoint endpoint = assemblyFactory.createEndpoint();
                        endpoint.setComponent(component);
                        endpoint.setService(service);
                        endpoint.setBinding(binding);

                        // retrieve the stash of intents and policy sets from the component
                        endpoint.getRequiredIntents().addAll(component.getRequiredIntents());
                        endpoint.getPolicySets().addAll(component.getPolicySets());

                        endpoints.add(endpoint);
                    }
                }
            }
        }
        return endpoints;
    }

    public void write(RemoteEndpoints model, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {
        writeStart(writer, REMOTE_ENDPOINTS_QNAME.getNamespaceURI(), REMOTE_ENDPOINTS_QNAME.getLocalPart());
        extensionProcessor.write(model.getComposite(), writer, context);
        writeEnd(writer);
    }

    public Class<RemoteEndpoints> getModelType() {
        return RemoteEndpoints.class;
    }

    public void resolve(RemoteEndpoints model, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        // the only thing we'll resolve here is the policy model as the endpoint 
        // matching algorithm needs to look inside the policy model

        for (Endpoint endpoint : model) {
            for (PolicySet policySet : endpoint.getPolicySets()) {
                extensionProcessor.resolve(policySet, resolver, context);
            }
        }
    }
}
