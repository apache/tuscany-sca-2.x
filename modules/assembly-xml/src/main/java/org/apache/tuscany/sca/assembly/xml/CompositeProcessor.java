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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.tuscany.sca.assembly.xml.Constants.AUTOWIRE;
import static org.apache.tuscany.sca.assembly.xml.Constants.CALLBACK;
import static org.apache.tuscany.sca.assembly.xml.Constants.CALLBACK_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPONENT;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPONENT_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPOSITE;
import static org.apache.tuscany.sca.assembly.xml.Constants.COMPOSITE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.ELEMENT;
import static org.apache.tuscany.sca.assembly.xml.Constants.FILE;
import static org.apache.tuscany.sca.assembly.xml.Constants.IMPLEMENTATION_COMPOSITE;
import static org.apache.tuscany.sca.assembly.xml.Constants.IMPLEMENTATION_COMPOSITE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.INCLUDE;
import static org.apache.tuscany.sca.assembly.xml.Constants.INCLUDE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.INTENTS;
import static org.apache.tuscany.sca.assembly.xml.Constants.LOCAL;
import static org.apache.tuscany.sca.assembly.xml.Constants.MANY;
import static org.apache.tuscany.sca.assembly.xml.Constants.MUST_SUPPLY;
import static org.apache.tuscany.sca.assembly.xml.Constants.NAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.NONOVERRIDABLE;
import static org.apache.tuscany.sca.assembly.xml.Constants.POLICY_SET_ATTACHMENT_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROMOTE;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.REPLACE;
import static org.apache.tuscany.sca.assembly.xml.Constants.REQUIRES_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.SCA11_NS;
import static org.apache.tuscany.sca.assembly.xml.Constants.SERVICE;
import static org.apache.tuscany.sca.assembly.xml.Constants.SERVICE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.SOURCE;
import static org.apache.tuscany.sca.assembly.xml.Constants.TARGET;
import static org.apache.tuscany.sca.assembly.xml.Constants.TARGET_NAMESPACE;
import static org.apache.tuscany.sca.assembly.xml.Constants.TYPE;
import static org.apache.tuscany.sca.assembly.xml.Constants.URI;
import static org.apache.tuscany.sca.assembly.xml.Constants.WIRE;
import static org.apache.tuscany.sca.assembly.xml.Constants.WIRED_BY_IMPL;
import static org.apache.tuscany.sca.assembly.xml.Constants.WIRE_QNAME;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.common.xml.xpath.XPathHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;

/**
 * A composite processor.
 *
 * @version $Rev$ $Date$
 */
public class CompositeProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<Composite> {
    private XPathHelper xpathHelper;
    private PolicyFactory intentAttachPointTypeFactory;
    private StAXAttributeProcessor<Object> extensionAttributeProcessor;
    private ContributionFactory contributionFactory;

    /**
     * Construct a new composite processor
     *
     * @param extensionPoints
     * @param extensionProcessor
     */
    public CompositeProcessor(ExtensionPointRegistry extensionPoints,
                              StAXArtifactProcessor extensionProcessor,
                              StAXAttributeProcessor extensionAttributeProcessor) {

        this(modelFactories(extensionPoints), extensionProcessor, extensionAttributeProcessor);

        this.xpathHelper = XPathHelper.getInstance(extensionPoints);
        this.extensionAttributeProcessor = extensionAttributeProcessor;
    }

    /**
     * Constructs a new composite processor
     *
     * @param modelFactories
     * @param extensionProcessor
     * @param monitor
     */
    private CompositeProcessor(FactoryExtensionPoint modelFactories,
                               StAXArtifactProcessor extensionProcessor,
                               StAXAttributeProcessor extensionAttributeProcessor) {

        super(modelFactories, extensionProcessor);
        this.intentAttachPointTypeFactory = modelFactories.getFactory(PolicyFactory.class);
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.extensionAttributeProcessor = extensionAttributeProcessor;

    }

    public Composite read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException {
        Composite composite = null;
        Composite include = null;
        Component component = null;
        Property property = null;
        ComponentService componentService = null;
        ComponentReference componentReference = null;
        ComponentProperty componentProperty = null;
        CompositeService compositeService = null;
        CompositeReference compositeReference = null;
        Contract contract = null;
        Wire wire = null;
        Callback callback = null;
        QName name = null;
        Monitor monitor = context.getMonitor();
        try {
            // Read the composite document
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        name = reader.getName();

                        if (COMPOSITE_QNAME.equals(name)) {

                            // Read a <composite>
                            composite = assemblyFactory.createComposite();
                            composite.setSpecVersion(Constants.SCA11_NS);

                            composite.setName(new QName(getURIString(reader, TARGET_NAMESPACE), getString(reader, NAME)));

                            if (!isSet(reader, TARGET_NAMESPACE)) {
                                // spec says that a composite must have a namespace
                                warning(monitor, "NoCompositeNamespace", composite, composite.getName().toString());
                            }

                            if (isSet(reader, AUTOWIRE)) {
                                composite.setAutowire(getBoolean(reader, AUTOWIRE));
                            }

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, composite, extensionAttributeProcessor, context);

                            composite.setLocal(getBoolean(reader, LOCAL));
                            policyProcessor.readPolicies(composite, reader);

                        } else if (INCLUDE_QNAME.equals(name)) {

                            // Read an <include>
                            include = assemblyFactory.createComposite();
                            include.setName(getQName(reader, NAME));
                            include.setURI(getURIString(reader, URI));
                            include.setUnresolved(true);
                            composite.getIncludes().add(include);

                        } else if (SERVICE_QNAME.equals(name)) {
                            if (component != null) {

                                // Read a <component><service>
                                componentService = assemblyFactory.createComponentService();
                                contract = componentService;
                                componentService.setName(getString(reader, NAME));

                                //handle extension attributes
                                this
                                    .readExtendedAttributes(reader, name, componentService, extensionAttributeProcessor, context);

                                component.getServices().add(componentService);
                                policyProcessor.readPolicies(contract, reader);
                            } else {

                                // Read a <composite><service>
                                compositeService = assemblyFactory.createCompositeService();
                                contract = compositeService;
                                compositeService.setName(getString(reader, NAME));

                                String promoted = getURIString(reader, PROMOTE);
                                if (promoted != null) {
                                    String promotedComponentName;
                                    String promotedServiceName;
                                    int s = promoted.indexOf('/');
                                    if (s == -1) {
                                        promotedComponentName = promoted;
                                        promotedServiceName = null;
                                    } else {
                                        promotedComponentName = promoted.substring(0, s);
                                        promotedServiceName = promoted.substring(s + 1);
                                    }

                                    Component promotedComponent = assemblyFactory.createComponent();
                                    promotedComponent.setUnresolved(true);
                                    promotedComponent.setName(promotedComponentName);
                                    compositeService.setPromotedComponent(promotedComponent);

                                    ComponentService promotedService = assemblyFactory.createComponentService();
                                    promotedService.setUnresolved(true);
                                    promotedService.setName(promotedServiceName);
                                    compositeService.setPromotedService(promotedService);
                                }

                                //handle extension attributes
                                this
                                    .readExtendedAttributes(reader, name, compositeService, extensionAttributeProcessor, context);

                                composite.getServices().add(compositeService);
                                policyProcessor.readPolicies(contract, reader);
                            }

                        } else if (REFERENCE_QNAME.equals(name)) {
                            if (component != null) {
                                // Read a <component><reference>
                                componentReference = assemblyFactory.createComponentReference();
                                contract = componentReference;
                                componentReference.setName(getString(reader, NAME));
                                readMultiplicity(componentReference, reader);
                                if (isSet(reader, AUTOWIRE)) {
                                    componentReference.setAutowire(getBoolean(reader, AUTOWIRE));
                                }
                                // Read @nonOverridable
                                String nonOverridable = reader.getAttributeValue(null, NONOVERRIDABLE);
                                if (nonOverridable != null) {
                                    componentReference.setNonOverridable(Boolean.parseBoolean(nonOverridable));
                                }
                                readTargets(componentReference, reader);
                                componentReference.setWiredByImpl(getBoolean(reader, WIRED_BY_IMPL));

                                //handle extension attributes
                                this.readExtendedAttributes(reader,
                                                            name,
                                                            componentReference,
                                                            extensionAttributeProcessor, context);

                                component.getReferences().add(componentReference);
                                policyProcessor.readPolicies(contract, reader);
                            } else {
                                // Read a <composite><reference>
                                compositeReference = assemblyFactory.createCompositeReference();
                                contract = compositeReference;
                                compositeReference.setName(getString(reader, NAME));
                                readMultiplicity(compositeReference, reader);
                                readTargets(compositeReference, reader);
                                String promote = getURIString(reader, Constants.PROMOTE);
                                if (promote != null) {
                                    for (StringTokenizer tokens = new StringTokenizer(promote); tokens.hasMoreTokens();) {
                                        String refName = tokens.nextToken();
                                        Component promotedComponent = assemblyFactory.createComponent();
                                        int index = refName.indexOf('/');
                                        if (index == -1) {
                                            error(monitor, "Invalid reference name", compositeReference, refName);
                                        }
                                        String promotedComponentName = refName.substring(0, index);
                                        promotedComponent.setName(promotedComponentName);
                                        promotedComponent.setUnresolved(true);
                                        compositeReference.getPromotedComponents().add(promotedComponent);
                                        ComponentReference promotedReference =
                                            assemblyFactory.createComponentReference();
                                        promotedReference.setUnresolved(true);
                                        promotedReference.setName(refName);
                                        compositeReference.getPromotedReferences().add(promotedReference);
                                    }
                                }
                                compositeReference.setWiredByImpl(getBoolean(reader, WIRED_BY_IMPL));

                                //handle extension attributes
                                this.readExtendedAttributes(reader,
                                                            name,
                                                            compositeReference,
                                                            extensionAttributeProcessor, context);

                                composite.getReferences().add(compositeReference);
                                policyProcessor.readPolicies(contract, reader);
                            }

                        } else if (PROPERTY_QNAME.equals(name)) {
                            if (component != null) {

                                // Read a <component><property>
                                componentProperty = assemblyFactory.createComponentProperty();
                                property = componentProperty;
                                String source = getURIString(reader, SOURCE);
                                if (source != null) {
                                    source = source.trim();
                                }
                                componentProperty.setSource(source);
                                if (source != null) {
                                    // $<name>/...
                                    if (source.charAt(0) == '$') {
                                        int index = source.indexOf('/');
                                        if (index == -1) {
                                            // Tolerating $prop
                                            source = "";
                                        } else {
                                            source = source.substring(index + 1);
                                        }
                                        if ("".equals(source)) {
                                            source = ".";
                                        }
                                    }

                                    try {
                                        componentProperty.setSourceXPathExpression(xpathHelper.compile(reader
                                            .getNamespaceContext(), source));
                                    } catch (XPathExpressionException e) {
                                        ContributionReadException ce = new ContributionReadException(e);
                                        error(monitor, "ContributionReadException", source, ce);
                                        //throw ce;
                                    }
                                }
                                componentProperty.setFile(getURIString(reader, FILE));

                                //handle extension attributes
                                this.readExtendedAttributes(reader,
                                                            name,
                                                            componentProperty,
                                                            extensionAttributeProcessor, context);

                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(componentProperty, reader, context);

                                // Read the property value
                                Document value =
                                    readPropertyValue(property.getXSDElement(), property.getXSDType(), property
                                        .isMany(), reader, context);
                                property.setValue(value);

                                component.getProperties().add(componentProperty);
                            } else {

                                // Read a <composite><property>
                                property = assemblyFactory.createProperty();
                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(property, reader, context);

                                // Read the property value
                                Document value =
                                    readPropertyValue(property.getXSDElement(), property.getXSDType(), property
                                        .isMany(), reader, context);
                                property.setValue(value);

                                composite.getProperties().add(property);
                            }

                            // TUSCANY-1949
                            // If the property doesn't have a value, the END_ELEMENT event is read by the readPropertyValue
                            if (reader.getEventType() == END_ELEMENT && PROPERTY_QNAME.equals(reader.getName())) {
                                property = null;
                                componentProperty = null;
                            }

                        } else if (COMPONENT_QNAME.equals(name)) {

                            // Read a <component>
                            component = assemblyFactory.createComponent();
                            component.setName(getString(reader, NAME));
                            if (isSet(reader, AUTOWIRE)) {
                                component.setAutowire(getBoolean(reader, AUTOWIRE));
                            }
                            if (isSet(reader, URI)) {
                                component.setURI(getURIString(reader, URI));
                            }

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, component, extensionAttributeProcessor, context);

                            composite.getComponents().add(component);
                            policyProcessor.readPolicies(component, reader);

                        } else if (WIRE_QNAME.equals(name)) {

                            // Read a <wire>
                            wire = assemblyFactory.createWire();
                            ComponentReference source = assemblyFactory.createComponentReference();
                            source.setUnresolved(true);
                            source.setName(getURIString(reader, SOURCE));
                            wire.setSource(source);

                            ComponentService target = assemblyFactory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getURIString(reader, TARGET));
                            wire.setTarget(target);

                            // Read @replace
                            String replace = reader.getAttributeValue(null, REPLACE);
                            if (replace != null) {
                                wire.setReplace(Boolean.parseBoolean(replace));
                            }

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, wire, extensionAttributeProcessor, context);

                            composite.getWires().add(wire);
                            policyProcessor.readPolicies(wire, reader);

                        } else if (CALLBACK_QNAME.equals(name)) {

                            // Read a <callback>
                            callback = assemblyFactory.createCallback();
                            contract.setCallback(callback);

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, callback, extensionAttributeProcessor, context);

                            policyProcessor.readPolicies(callback, reader);

                        } else if (IMPLEMENTATION_COMPOSITE_QNAME.equals(name)) {

                            // Read an implementation.composite
                            Composite implementation = assemblyFactory.createComposite();
                            implementation.setName(getQName(reader, NAME));
                            implementation.setUnresolved(true);

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, implementation, extensionAttributeProcessor, context);

                            component.setImplementation(implementation);
                            policyProcessor.readPolicies(implementation, reader);
                        } else if (REQUIRES_QNAME.equals(name)) {
                            List<QName> intents = getQNames(reader, INTENTS);
                            for (QName i : intents) {
                                Intent intent = policyFactory.createIntent();
                                intent.setName(i);
                                if (composite != null) {
                                    composite.getRequiredIntents().add(intent);
                                } else if (component != null) {
                                    component.getRequiredIntents().add(intent);
                                } else if (contract != null) {
                                    contract.getRequiredIntents().add(intent);
                                } else if (callback != null) {
                                    callback.getRequiredIntents().add(intent);
                                }
                            }
                        } else if (POLICY_SET_ATTACHMENT_QNAME.equals(name)) {
                            QName ps = getQName(reader, NAME);
                            if (ps != null) {
                                PolicySet policySet = policyFactory.createPolicySet();
                                policySet.setName(ps);
                                if (composite != null) {
                                    composite.getPolicySets().add(policySet);
                                } else if (component != null) {
                                    component.getPolicySets().add(policySet);
                                } else if (contract != null) {
                                    contract.getPolicySets().add(policySet);
                                } else if (callback != null) {
                                    callback.getPolicySets().add(policySet);
                                }
                            }

                        } else {

                            // Read an extension element
                            Object extension = extensionProcessor.read(reader, context);
                            if (extension != null) {
                                if (extension instanceof InterfaceContract) {

                                    // <service><interface> and
                                    // <reference><interface>
                                    if (contract != null) {
                                        contract.setInterfaceContract((InterfaceContract)extension);
                                    } else {
                                        if (name.getNamespaceURI().equals(SCA11_NS)) {
                                            error(monitor, "UnexpectedInterfaceElement", extension);
                                            //throw new ContributionReadException("Unexpected <interface> element found. It should appear inside a <service> or <reference> element");
                                        } else {
                                            composite.getExtensions().add(extension);
                                        }
                                    }
                                } else if (extension instanceof Binding) {
                                    if (extension instanceof PolicySubject) {
                                        ExtensionType bindingType = intentAttachPointTypeFactory.createBindingType();
                                        bindingType.setType(name);
                                        bindingType.setUnresolved(true);
                                        ((PolicySubject)extension).setExtensionType(bindingType);
                                    }
                                    // <service><binding> and
                                    // <reference><binding>
                                    if (callback != null) {
                                        callback.getBindings().add((Binding)extension);
                                    } else {
                                        if (contract != null) {
                                            contract.getBindings().add((Binding)extension);
                                        } else {
                                            if (name.getNamespaceURI().equals(SCA11_NS)) {
                                                error(monitor, "UnexpectedBindingElement", extension);
                                                //throw new ContributionReadException("Unexpected <binding> element found. It should appear inside a <service> or <reference> element");
                                            } else {
                                                composite.getExtensions().add(extension);
                                            }
                                        }
                                    }

                                } else if (extension instanceof Implementation) {
                                    if (extension instanceof PolicySubject) {
                                        ExtensionType implType =
                                            intentAttachPointTypeFactory.createImplementationType();
                                        implType.setType(name);
                                        implType.setUnresolved(true);
                                        ((PolicySubject)extension).setExtensionType(implType);
                                    }
                                    // <component><implementation>
                                    if (component != null) {
                                        component.setImplementation((Implementation)extension);
                                    } else {
                                        if (name.getNamespaceURI().equals(SCA11_NS)) {
                                            error(monitor, "UnexpectedImplementationElement", extension);
                                            //throw new ContributionReadException("Unexpected <implementation> element found. It should appear inside a <component> element");
                                        } else {
                                            composite.getExtensions().add(extension);
                                        }
                                    }
                                } else {

                                    // Add the extension element to the current
                                    // element
                                    if (callback != null) {
                                        callback.getExtensions().add(extension);
                                    } else if (contract != null) {
                                        contract.getExtensions().add(extension);
                                    } else if (property != null) {
                                        property.getExtensions().add(extension);
                                    } else if (component != null) {
                                        component.getExtensions().add(extension);
                                    } else {
                                        composite.getExtensions().add(extension);
                                    }
                                }
                            }
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        break;

                    case END_ELEMENT:
                        name = reader.getName();

                        // Clear current state when reading reaching end element
                        if (SERVICE_QNAME.equals(name)) {
                            componentService = null;
                            compositeService = null;
                            contract = null;
                        } else if (INCLUDE_QNAME.equals(name)) {
                            include = null;
                        } else if (REFERENCE_QNAME.equals(name)) {
                            componentReference = null;
                            compositeReference = null;
                            contract = null;
                        } else if (PROPERTY_QNAME.equals(name)) {
                            componentProperty = null;
                            property = null;
                        } else if (COMPONENT_QNAME.equals(name)) {
                            component = null;
                        } else if (WIRE_QNAME.equals(name)) {
                            wire = null;
                        } else if (CALLBACK_QNAME.equals(name)) {
                            callback = null;
                        }
                        break;
                }

                // Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
        } catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error(monitor, "XMLStreamException", reader, ex);
        }

        return composite;
    }

    public void write(Composite composite, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {

        // Write <composite> element
        writeStartDocument(writer,
                           COMPOSITE,
                           new XAttr(TARGET_NAMESPACE, composite.getName().getNamespaceURI()),
                           new XAttr(NAME, composite.getName().getLocalPart()),
                           new XAttr(LOCAL, composite.isLocal() ? Boolean.TRUE : null),
                           new XAttr(AUTOWIRE, composite.getAutowire()),
                           policyProcessor.writePolicies(composite));

        //write extended attributes
        this.writeExtendedAttributes(writer, composite, extensionAttributeProcessor, context);

        // Write <include> elements
        for (Composite include : composite.getIncludes()) {
            String uri = include.isUnresolved() ? include.getURI() : null;
            writeStart(writer, INCLUDE, new XAttr(NAME, include.getName()), new XAttr(URI, uri));

            //write extended attributes
            this.writeExtendedAttributes(writer, include, extensionAttributeProcessor, context);

            writeEnd(writer);
        }

        // Write <service> elements
        for (Service service : composite.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            Component promotedComponent = compositeService.getPromotedComponent();
            ComponentService promotedService = compositeService.getPromotedService();
            String promote;
            if (promotedService != null) {
                if (promotedService.getName() != null) {
                    promote = promotedComponent.getName() + '/' + promotedService.getName();
                } else {
                    promote = promotedComponent.getName();
                }
            } else {
                promote = null;
            }
            writeStart(writer,
                       SERVICE,
                       new XAttr(NAME, service.getName()),
                       new XAttr(PROMOTE, promote),
                       policyProcessor.writePolicies(service));

            //write extended attributes
            this.writeExtendedAttributes(writer, service, extensionAttributeProcessor, context);

            // Write service interface
            extensionProcessor.write(service.getInterfaceContract(), writer, context);

            // Write bindings
            for (Binding binding : service.getBindings()) {
                extensionProcessor.write(binding, writer, context);
            }

            // Write <callback> element
            if (service.getCallback() != null) {
                Callback callback = service.getCallback();
                writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                //write extended attributes
                this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor, context);

                // Write callback bindings
                for (Binding binding : callback.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }

                // Write extensions
                for (Object extension : callback.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }

                writeEnd(writer);
            }

            // Write extensions
            for (Object extension : service.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }

            writeEnd(writer);
        }

        // Write <component> elements
        for (Component component : composite.getComponents()) {
            writeStart(writer,
                       COMPONENT,
                       new XAttr(NAME, component.getName()),
                       new XAttr(URI, component.getURI()),
                       new XAttr(AUTOWIRE, component.getAutowire()),
                       policyProcessor.writePolicies(component));

            //write extended attributes
            this.writeExtendedAttributes(writer, component, extensionAttributeProcessor, context);

            // Write the component implementation
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                writeStart(writer, IMPLEMENTATION_COMPOSITE, new XAttr(NAME, ((Composite)implementation).getName()));

                //write extended attributes
                this.writeExtendedAttributes(writer, (Composite)implementation, extensionAttributeProcessor, context);

                writeEnd(writer);
            } else {
                extensionProcessor.write(component.getImplementation(), writer, context);
            }

            for (Object extension : component.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }

            // Write <service> elements
            for (ComponentService service : component.getServices()) {
                writeStart(writer, SERVICE, new XAttr(NAME, service.getName()), policyProcessor.writePolicies(service));

                //write extended attributes
                this.writeExtendedAttributes(writer, service, extensionAttributeProcessor, context);

                // Write service interface
                extensionProcessor.write(service.getInterfaceContract(), writer, context);

                // Write bindings
                for (Binding binding : service.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }

                // Write <callback> element
                if (service.getCallback() != null) {
                    Callback callback = service.getCallback();
                    writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                    //write extended attributes
                    this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor, context);

                    // Write bindings
                    for (Binding binding : callback.getBindings()) {
                        extensionProcessor.write(binding, writer, context);
                    }

                    // Write extensions
                    for (Object extension : callback.getExtensions()) {
                        extensionProcessor.write(extension, writer, context);
                    }

                    writeEnd(writer);
                }

                // Write extensions
                for (Object extension : service.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }

                writeEnd(writer);
            }

            // Write <reference> elements
            for (ComponentReference reference : component.getReferences()) {
                writeStart(writer,
                           REFERENCE,
                           new XAttr(NAME, reference.getName()),
                           new XAttr(AUTOWIRE, reference.getAutowire()),
                           (reference.isNonOverridable() ? new XAttr(NONOVERRIDABLE, true) : null),
                           writeMultiplicity(reference),
                           writeTargets(reference),
                           policyProcessor.writePolicies(reference));

                //write extended attributes
                this.writeExtendedAttributes(writer, reference, extensionAttributeProcessor, context);

                // Write reference interface
                extensionProcessor.write(reference.getInterfaceContract(), writer, context);

                // Write bindings
                for (Binding binding : reference.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }

                // Write callback
                if (reference.getCallback() != null) {
                    Callback callback = reference.getCallback();
                    writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                    //write extended attributes
                    this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor, context);

                    // Write callback bindings
                    for (Binding binding : callback.getBindings()) {
                        extensionProcessor.write(binding, writer, context);
                    }

                    // Write extensions
                    for (Object extensions : callback.getExtensions()) {
                        extensionProcessor.write(extensions, writer, context);
                    }

                    writeEnd(writer);
                }

                // Write extensions
                for (Object extensions : reference.getExtensions()) {
                    extensionProcessor.write(extensions, writer, context);
                }

                writeEnd(writer);
            }

            // Write <property> elements
            for (ComponentProperty property : component.getProperties()) {
                writeStart(writer,
                           PROPERTY,
                           new XAttr(NAME, property.getName()),
                           new XAttr(MUST_SUPPLY, property.isMustSupply()),
                           new XAttr(MANY, property.isMany()),
                           new XAttr(TYPE, property.getXSDType()),
                           new XAttr(ELEMENT, property.getXSDElement()),
                           new XAttr(SOURCE, property.getSource()),
                           new XAttr(FILE, property.getFile()),
                           policyProcessor.writePolicies(property));

                //write extended attributes
                this.writeExtendedAttributes(writer, property, extensionAttributeProcessor, context);

                // Write property value
                writePropertyValue(property.getValue(), property.getXSDElement(), property.getXSDType(), writer);

                // Write extensions
                for (Object extension : property.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }

                writeEnd(writer);
            }

            writeEnd(writer);
        }

        // Write <reference> elements
        for (Reference reference : composite.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;

            // Write list of promoted references
            List<String> promote = new ArrayList<String>();
            for (ComponentReference promoted : compositeReference.getPromotedReferences()) {
                promote.add(promoted.getName());
            }

            // Write <reference> element
            writeStart(writer,
                       REFERENCE,
                       new XAttr(NAME, reference.getName()),
                       new XAttr(PROMOTE, promote),
                       writeMultiplicity(reference),
                       policyProcessor.writePolicies(reference));

            //write extended attributes
            this.writeExtendedAttributes(writer, reference, extensionAttributeProcessor, context);

            // Write reference interface
            extensionProcessor.write(reference.getInterfaceContract(), writer, context);

            // Write bindings
            for (Binding binding : reference.getBindings()) {
                extensionProcessor.write(binding, writer, context);
            }

            // Write <callback> element
            if (reference.getCallback() != null) {
                Callback callback = reference.getCallback();
                writeStart(writer, CALLBACK);

                //write extended attributes
                this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor, context);

                // Write callback bindings
                for (Binding binding : callback.getBindings()) {
                    extensionProcessor.write(binding, writer, context);
                }

                // Write extensions
                for (Object extension : callback.getExtensions()) {
                    extensionProcessor.write(extension, writer, context);
                }

                writeEnd(writer);
            }

            // Write extensions
            for (Object extension : reference.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }

            writeEnd(writer);
        }

        // Write <property> elements
        for (Property property : composite.getProperties()) {
            writeStart(writer,
                       PROPERTY,
                       new XAttr(NAME, property.getName()),
                       new XAttr(MUST_SUPPLY, property.isMustSupply()),
                       new XAttr(MANY, property.isMany()),
                       new XAttr(TYPE, property.getXSDType()),
                       new XAttr(ELEMENT, property.getXSDElement()),
                       policyProcessor.writePolicies(property));

            //write extended attributes
            this.writeExtendedAttributes(writer, property, extensionAttributeProcessor, context);

            // Write property value
            writePropertyValue(property.getValue(), property.getXSDElement(), property.getXSDType(), writer);

            // Write extensions
            for (Object extension : property.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }

            writeEnd(writer);
        }

        // Write <wire> elements
        for (Wire wire : composite.getWires()) {
            writeStart(writer, WIRE, new XAttr(SOURCE, wire.getSource().getName()), new XAttr(TARGET, wire.getTarget()
                .getName()), wire.isReplace() ? new XAttr(Constants.REPLACE, true) : null);

            //write extended attributes
            this.writeExtendedAttributes(writer, wire, extensionAttributeProcessor, context);

            // Write extensions
            for (Object extension : wire.getExtensions()) {
                extensionProcessor.write(extension, writer, context);
            }
            writeEnd(writer);
        }

        for (Object extension : composite.getExtensions()) {
            extensionProcessor.write(extension, writer, context);
        }

        writeEndDocument(writer);
    }

    public void resolve(Composite composite, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        Monitor monitor = context.getMonitor();
        try {
            monitor.pushContext("Composite: " + composite.getName());

            // Resolve includes in the composite
            for (int i = 0, n = composite.getIncludes().size(); i < n; i++) {
                Composite include = composite.getIncludes().get(i);
                if (include != null) {
                    Composite resolved = resolver.resolveModel(Composite.class, include, context);
                    if (!resolved.isUnresolved()) {
                        if ((composite.isLocal() && resolved.isLocal()) || (!composite.isLocal() && !resolved.isLocal())) {
                            composite.getIncludes().set(i, resolved);
                        } else {
                            ContributionResolveException ce =
                                new ContributionResolveException("[ASM60041] Error: Composite " + composite.getName()
                                    + " can only include another composite with the identical @local attribute value");
                            error(monitor, "ContributionResolveException", include, ce);
                        }
                    } else {
                        ContributionResolveException ce =
                            new ContributionResolveException("[ASM60042] Error: Composite " + include.getName()
                                + " is not a valid composite within the domain");
                        error(monitor, "ContributionResolveException", include, ce);
                    }
                }
            }

            // Resolve extensions
            for (Object extension : composite.getExtensions()) {
                if (extension != null) {
                    extensionProcessor.resolve(extension, resolver, context);
                }
            }

            //Resolve composite services and references
            resolveContracts(composite, composite.getServices(), resolver, context);
            resolveContracts(composite, composite.getReferences(), resolver, context);

            // Resolve component implementations, services and references
            for (Component component : composite.getComponents()) {

                //resolve component services and references
                resolveContracts(component, component.getServices(), resolver, context);
                resolveContracts(component, component.getReferences(), resolver, context);

                for (ComponentProperty componentProperty : component.getProperties()) {
                    if (componentProperty.getFile() != null) {
                        Artifact artifact = contributionFactory.createArtifact();
                        artifact.setURI(componentProperty.getFile());
                        artifact = resolver.resolveModel(Artifact.class, artifact, context);
                        if (artifact.getLocation() != null) {
                            componentProperty.setFile(artifact.getLocation());
                        }
                    }
                }

                //resolve component implementation
                Implementation implementation = component.getImplementation();
                if (implementation != null) {
                    //now resolve the implementation so that even if there is a shared instance
                    //for this that is resolved, the specified intents and policysets are safe in the
                    //component and not lost
                    implementation = resolveImplementation(implementation, resolver, context);

                    component.setImplementation(implementation);
                }

                //add model resolver to component
                if (component instanceof ResolverExtension) {
                    ((ResolverExtension)component).setModelResolver(resolver);
                }
            }

            // Add model resolver to promoted components
            for (Service service : composite.getServices()) {
                CompositeService compositeService = (CompositeService)service;
                Component promotedComponent = compositeService.getPromotedComponent();
                if (promotedComponent instanceof ResolverExtension) {
                    ((ResolverExtension)promotedComponent).setModelResolver(resolver);
                }
            } // end for

        } finally {
            // Pop context
            monitor.popContext();
        } // end try 
    }

    public QName getArtifactType() {
        return COMPOSITE_QNAME;
    }

    public Class<Composite> getModelType() {
        return Composite.class;
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

}
