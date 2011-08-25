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
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
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
public class EndpointProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<Endpoint> {
    private final static String ENDPOINT = "endpoint";
    private final static QName ENDPOINT_QNAME = new QName(Constants.SCA11_TUSCANY_NS, ENDPOINT);

    private ExtensionPointRegistry registry;

    public EndpointProcessor(ExtensionPointRegistry registry,
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
        return ENDPOINT_QNAME;
    }

    public Endpoint read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        Endpoint endpoint = assemblyFactory.createEndpoint();
        if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            reader.nextTag();
        }
        Object model = extensionProcessor.read(reader, context);
        if (model instanceof Composite) {
            Composite composite = (Composite)model;
            Component component = composite.getComponents().get(0);
            ComponentService service = component.getServices().get(0);
            Binding binding = service.getBindings().get(0);
            endpoint.setComponent(component);
            endpoint.setService(service);
            endpoint.setBinding(binding);
        }
        return endpoint;
    }

    public void write(Endpoint model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        // writeStart(writer, ENDPOINT_QNAME);
        extensionProcessor.write(wrap(model), writer, context);
        // writeEnd(writer);
    }

    private Composite wrap(Endpoint endpoint) {
        try {
            Composite composite = assemblyFactory.createComposite();
            composite.setName(ENDPOINT_QNAME);
            composite.setLocal(false);
            if (endpoint.getComponent() != null) {
                Component component = (Component)endpoint.getComponent().clone();
                component.setImplementation(null);
                composite.getComponents().add(component);
                component.getReferences().clear();
                component.getServices().clear();
                if (endpoint.getService() != null) {
                    ComponentService service = (ComponentService)endpoint.getService().clone();
                    component.getServices().add(service);
                    service.getBindings().clear();
                    service.setInterfaceContract(endpoint.getComponentServiceInterfaceContract());
                    if (endpoint.getBinding() != null) {
                        Binding binding = (Binding)endpoint.getBinding().clone();
                        service.getBindings().add(binding);
                    } 
                    // put both manually configured AND automatically generated callback bindings
                    // into the wrapping model so that we can pass callback configuarion via
                    // the registry
                    if (service.getCallbackReference() != null) {
                        Callback callback = service.getCallback();
                        if(callback == null){
                            callback = assemblyFactory.createCallback();
                        }
                        for (EndpointReference epr : service.getCallbackReference().getEndpointReferences()){
                            callback.getBindings().add(epr.getBinding());
                        }
                        service.setCallback(callback);
                    }
                }
            }
            return composite;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Class<Endpoint> getModelType() {
        return Endpoint.class;
    }

    public void resolve(Endpoint model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    }
}
