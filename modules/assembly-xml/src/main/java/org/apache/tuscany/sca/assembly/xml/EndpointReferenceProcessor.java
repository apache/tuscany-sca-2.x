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

package org.apache.tuscany.sca.assembly.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 *
 */
public class EndpointReferenceProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<EndpointReference> {
    private final static String ENDPOINT_REFERENCE = "endpointReference";
    private final static QName ENDPOINT_REFERENCE_QNAME = new QName(Constants.SCA11_TUSCANY_NS, ENDPOINT_REFERENCE);

    private ExtensionPointRegistry registry;

    public EndpointReferenceProcessor(ExtensionPointRegistry registry,
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
        return ENDPOINT_REFERENCE_QNAME;
    }

    public EndpointReference read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        EndpointReference endpointReference = assemblyFactory.createEndpointReference();
        if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            reader.nextTag();
        }
        Object model = extensionProcessor.read(reader, context);
        if (model instanceof Composite) {
            Composite composite = (Composite)model;
            Component component = composite.getComponents().get(0);
            ComponentReference reference = component.getReferences().get(0);
            Binding binding = reference.getBindings().get(0);
            endpointReference.setComponent(component);
            endpointReference.setReference(reference);
            reference.getEndpointReferences().add(endpointReference);
            endpointReference.setBinding(binding);
            
            // set up the EPR so that resolution will happen
            // at wire creation time if needs be
            Endpoint endpoint = assemblyFactory.createEndpoint();
            endpointReference.setTargetEndpoint(endpoint);
            
            if (reference.getTargets().size() > 0){
                // create a dummy endpoint with the URI set so that
                // the endpoint registry will be consulted
                endpoint.setUnresolved(true);
                endpoint.setURI(reference.getTargets().get(0).getName());
                endpointReference.setStatus(EndpointReference.Status.WIRED_TARGET_NOT_FOUND);
                endpointReference.setUnresolved(true);
            } else {
                endpoint.setUnresolved(false);
                endpoint.setBinding(reference.getBindings().get(0));
                endpointReference.setStatus(EndpointReference.Status.RESOLVED_BINDING);
                endpointReference.setUnresolved(false);
            }            
        }
        return endpointReference;
    }

    public void write(EndpointReference model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        extensionProcessor.write(wrap(model), writer, context);
    }

    private Composite wrap(EndpointReference endpointReference) {
        try {
            Composite composite = assemblyFactory.createComposite();
            composite.setName(ENDPOINT_REFERENCE_QNAME);
            composite.setLocal(false);
            Component component = (Component)endpointReference.getComponent().clone();
            composite.getComponents().add(component);
            component.getReferences().clear();
            component.getServices().clear();
            ComponentReference reference = (ComponentReference)endpointReference.getReference().clone();
            component.getReferences().add(reference);
            reference.getBindings().clear();
            Binding binding = (Binding)endpointReference.getBinding().clone();
            reference.getBindings().add(binding);
            //reference.setInterfaceContract(endpointReference.getInterfaceContract());
            if (endpointReference.getStatus() != EndpointReference.Status.RESOLVED_BINDING){
                ComponentService service = assemblyFactory.createComponentService();
                service.setName(endpointReference.getTargetEndpoint().getURI());
                reference.getTargets().clear();
                reference.getTargets().add(service);
            }
            return composite;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Class<EndpointReference> getModelType() {
        return EndpointReference.class;
    }

    public void resolve(EndpointReference model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    }
}
