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
import static org.apache.tuscany.sca.assembly.xml.Constants.LOCAL;
import static org.apache.tuscany.sca.assembly.xml.Constants.MANY;
import static org.apache.tuscany.sca.assembly.xml.Constants.MUST_SUPPLY;
import static org.apache.tuscany.sca.assembly.xml.Constants.NAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.NONOVERRIDABLE;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROMOTE;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY;
import static org.apache.tuscany.sca.assembly.xml.Constants.PROPERTY_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE;
import static org.apache.tuscany.sca.assembly.xml.Constants.REFERENCE_QNAME;
import static org.apache.tuscany.sca.assembly.xml.Constants.REPLACE;
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
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.ConstrainingType;
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
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;

/**
 * A composite processor.
 *
 * @version $Rev$ $Date$
 */
public class CompositeProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<Composite> {
    private XPathHelper xpathHelper;
    private XPathFactory xPathFactory;
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
			StAXAttributeProcessor extensionAttributeProcessor,
			Monitor monitor) {

    	this(modelFactories(extensionPoints),
    		extensionProcessor,
    		extensionAttributeProcessor,
    		monitor(extensionPoints));

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
             StAXAttributeProcessor extensionAttributeProcessor,
             Monitor monitor) {

    	super(modelFactories, extensionProcessor, monitor);
        this.intentAttachPointTypeFactory = modelFactories.getFactory(PolicyFactory.class);
        this.xPathFactory = modelFactories.getFactory(XPathFactory.class);
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.extensionAttributeProcessor = extensionAttributeProcessor;

    }

    public Composite read(XMLStreamReader reader) throws ContributionReadException {
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

                            composite.setName(new QName(getString(reader, TARGET_NAMESPACE), getString(reader, NAME)));

                            if(!isSet(reader, TARGET_NAMESPACE)){
                                // spec says that a composite must have a namespace
                                warning("NoCompositeNamespace", composite, composite.getName().toString());
                            }

                            if(isSet(reader, AUTOWIRE)) {
                                composite.setAutowire(getBoolean(reader, AUTOWIRE));
                            }

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, composite, extensionAttributeProcessor);

                            composite.setLocal(getBoolean(reader, LOCAL));
                            composite.setConstrainingType(readConstrainingType(reader));
                            policyProcessor.readPolicies(composite, reader);

                        } else if (INCLUDE_QNAME.equals(name)) {

                            // Read an <include>
                            include = assemblyFactory.createComposite();
                            include.setName(getQName(reader, NAME));
                            include.setURI(getString(reader, URI));
                            include.setUnresolved(true);
                            composite.getIncludes().add(include);

                        } else if (SERVICE_QNAME.equals(name)) {
                            if (component != null) {

                                // Read a <component><service>
                                componentService = assemblyFactory.createComponentService();
                                contract = componentService;
                                componentService.setName(getString(reader, NAME));

                                //handle extension attributes
                                this.readExtendedAttributes(reader, name, componentService, extensionAttributeProcessor);

                                component.getServices().add(componentService);
                                policyProcessor.readPolicies(contract, reader);
                            } else {

                                // Read a <composite><service>
                                compositeService = assemblyFactory.createCompositeService();
                                contract = compositeService;
                                compositeService.setName(getString(reader, NAME));

                                String promoted = getString(reader, PROMOTE);
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
                                this.readExtendedAttributes(reader, name, compositeService, extensionAttributeProcessor);

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
                                this.readExtendedAttributes(reader, name, componentReference, extensionAttributeProcessor);

                                component.getReferences().add(componentReference);
                                policyProcessor.readPolicies(contract, reader);
                            } else {
                                // Read a <composite><reference>
                                compositeReference = assemblyFactory.createCompositeReference();
                                contract = compositeReference;
                                compositeReference.setName(getString(reader, NAME));
                                readMultiplicity(compositeReference, reader);
                                readTargets(compositeReference, reader);
                                String promote = reader.getAttributeValue(null, Constants.PROMOTE);
                                if (promote != null) {
                                    for (StringTokenizer tokens = new StringTokenizer(promote); tokens.hasMoreTokens();) {
                                        String refName = tokens.nextToken();
                                        Component promotedComponent = assemblyFactory.createComponent();
                                        int index = refName.indexOf('/');
                                        if (index == -1) {
                                            error("Invalid reference name", compositeReference, refName);
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
                                this.readExtendedAttributes(reader, name, compositeReference, extensionAttributeProcessor);

                                composite.getReferences().add(compositeReference);
                                policyProcessor.readPolicies(contract, reader);
                            }

                        } else if (PROPERTY_QNAME.equals(name)) {
                            if (component != null) {

                                // Read a <component><property>
                                componentProperty = assemblyFactory.createComponentProperty();
                                property = componentProperty;
                                String source = getString(reader, SOURCE);
                                if(source!=null) {
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
                                        error("ContributionReadException", source, ce);
                                        //throw ce;
                                    }
                                }
                                componentProperty.setFile(getString(reader, FILE));

                                //handle extension attributes
                                this.readExtendedAttributes(reader, name, componentProperty, extensionAttributeProcessor);

                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(componentProperty, reader);

                                // Read the property value
                                Document value = readPropertyValue(property.getXSDElement(), property.getXSDType(), property.isMany(), reader);
                                property.setValue(value);

                                component.getProperties().add(componentProperty);
                            } else {

                                // Read a <composite><property>
                                property = assemblyFactory.createProperty();
                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(property, reader);

                                // Read the property value
                                Document value = readPropertyValue(property.getXSDElement(), property.getXSDType(), property.isMany(), reader);
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
                                component.setURI(getString(reader, URI));
                            }

                            //handle extension attributes
                           this.readExtendedAttributes(reader, name, component, extensionAttributeProcessor);

                            component.setConstrainingType(readConstrainingType(reader));
                            composite.getComponents().add(component);
                            policyProcessor.readPolicies(component, reader);

                        } else if (WIRE_QNAME.equals(name)) {

                            // Read a <wire>
                            wire = assemblyFactory.createWire();
                            ComponentReference source = assemblyFactory.createComponentReference();
                            source.setUnresolved(true);
                            source.setName(getString(reader, SOURCE));
                            wire.setSource(source);

                            ComponentService target = assemblyFactory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, TARGET));
                            wire.setTarget(target);

                            // Read @replace
                            String replace = reader.getAttributeValue(null, REPLACE);
                            if (replace != null) {
                                wire.setReplace(Boolean.parseBoolean(replace));
                            }

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, wire, extensionAttributeProcessor);

                            composite.getWires().add(wire);
                            policyProcessor.readPolicies(wire, reader);

                        } else if (CALLBACK_QNAME.equals(name)) {

                            // Read a <callback>
                            callback = assemblyFactory.createCallback();
                            contract.setCallback(callback);

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, callback, extensionAttributeProcessor);

                            policyProcessor.readPolicies(callback, reader);

                        } else if (IMPLEMENTATION_COMPOSITE_QNAME.equals(name)) {

                            // Read an implementation.composite
                            Composite implementation = assemblyFactory.createComposite();
                            implementation.setName(getQName(reader, NAME));
                            implementation.setUnresolved(true);

                            //handle extension attributes
                            this.readExtendedAttributes(reader, name, implementation, extensionAttributeProcessor);

                            component.setImplementation(implementation);
                            policyProcessor.readPolicies(implementation, reader);
                        } else {

                            // Read an extension element
                            Object extension = extensionProcessor.read(reader);
                            if (extension != null) {
                                if (extension instanceof InterfaceContract) {

                                    // <service><interface> and
                                    // <reference><interface>
                                    if (contract != null) {
                                        contract.setInterfaceContract((InterfaceContract)extension);
                                    } else {
                                        if (name.getNamespaceURI().equals(SCA11_NS)) {
                                        	error("UnexpectedInterfaceElement", extension);
                                            //throw new ContributionReadException("Unexpected <interface> element found. It should appear inside a <service> or <reference> element");
                                        } else {
                                            composite.getExtensions().add(extension);
                                        }
                                    }
                                } else if (extension instanceof Binding) {
                                    if ( extension instanceof PolicySubject ) {
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
                                            	error("UnexpectedBindingElement", extension);
                                                //throw new ContributionReadException("Unexpected <binding> element found. It should appear inside a <service> or <reference> element");
                                            } else {
                                                composite.getExtensions().add(extension);
                                            }
                                        }
                                    }

                                } else if (extension instanceof Implementation) {
                                    if ( extension instanceof PolicySubject ) {
                                        ExtensionType implType = intentAttachPointTypeFactory.createImplementationType();
                                        implType.setType(name);
                                        implType.setUnresolved(true);
                                        ((PolicySubject)extension).setExtensionType(implType);
                                    }
                                    // <component><implementation>
                                    if (component != null) {
                                        component.setImplementation((Implementation)extension);
                                    } else {
                                        if (name.getNamespaceURI().equals(SCA11_NS)) {
                                        	error("UnexpectedImplementationElement", extension);
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
        }
        catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error("XMLStreamException", reader, ex);
        }

        return composite;
    }

    public void write(Composite composite, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        // Write <composite> element
        writeStartDocument(writer,
                           COMPOSITE,
                           writeConstrainingType(composite),
                           new XAttr(TARGET_NAMESPACE, composite.getName().getNamespaceURI()),
                           new XAttr(NAME, composite.getName().getLocalPart()),
                           new XAttr(LOCAL, composite.isLocal() ? Boolean.TRUE : null),
                           new XAttr(AUTOWIRE, composite.getAutowire()),
                           policyProcessor.writePolicies(composite));

        //write extended attributes
        this.writeExtendedAttributes(writer, composite, extensionAttributeProcessor);

        // Write <include> elements
        for (Composite include : composite.getIncludes()) {
            String uri = include.isUnresolved()? include.getURI() : null;
            writeStart(writer,
                       INCLUDE,
                       new XAttr(NAME, include.getName()),
                       new XAttr(URI, uri));

            //write extended attributes
            this.writeExtendedAttributes(writer, include, extensionAttributeProcessor);

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
            writeStart(writer, SERVICE, new XAttr(NAME, service.getName()), new XAttr(PROMOTE, promote),
                       policyProcessor.writePolicies(service));

            //write extended attributes
            this.writeExtendedAttributes(writer, service, extensionAttributeProcessor);


            // Write service interface
            extensionProcessor.write(service.getInterfaceContract(), writer);

            // Write bindings
            for (Binding binding : service.getBindings()) {
                extensionProcessor.write(binding, writer);
            }

            // Write <callback> element
            if (service.getCallback() != null) {
                Callback callback = service.getCallback();
                writeStart(writer, CALLBACK,
                           policyProcessor.writePolicies(callback));

                //write extended attributes
                this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor);

                // Write callback bindings
                for (Binding binding : callback.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }

                // Write extensions
                for (Object extension : callback.getExtensions()) {
                    extensionProcessor.write(extension, writer);
                }

                writeEnd(writer);
            }

            // Write extensions
            for (Object extension : service.getExtensions()) {
                extensionProcessor.write(extension, writer);
            }

            writeEnd(writer);
        }

        // Write <component> elements
        for (Component component : composite.getComponents()) {
            writeStart(writer, COMPONENT, new XAttr(NAME, component.getName()),
                       new XAttr(URI, component.getURI()),
                       new XAttr(AUTOWIRE, component.getAutowire()),
                       policyProcessor.writePolicies(component));

            //write extended attributes
            this.writeExtendedAttributes(writer, component, extensionAttributeProcessor);

            // Write the component implementation
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                writeStart(writer, IMPLEMENTATION_COMPOSITE, new XAttr(NAME, ((Composite)implementation).getName()));

                //write extended attributes
                this.writeExtendedAttributes(writer, (Composite)implementation, extensionAttributeProcessor);

                writeEnd(writer);
            } else {
                extensionProcessor.write(component.getImplementation(), writer);
            }

            for (Object extension : component.getExtensions()) {
                extensionProcessor.write(extension, writer);
            }

            // Write <service> elements
            for (ComponentService service : component.getServices()) {
                writeStart(writer, SERVICE, new XAttr(NAME, service.getName()),
                           policyProcessor.writePolicies(service));

                //write extended attributes
                this.writeExtendedAttributes(writer, service, extensionAttributeProcessor);

                // Write service interface
                extensionProcessor.write(service.getInterfaceContract(), writer);

                // Write bindings
                for (Binding binding : service.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }

                // Write <callback> element
                if (service.getCallback() != null) {
                    Callback callback = service.getCallback();
                    writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                    //write extended attributes
                    this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor);

                    // Write bindings
                    for (Binding binding : callback.getBindings()) {
                        extensionProcessor.write(binding, writer);
                    }

                    // Write extensions
                    for (Object extension : callback.getExtensions()) {
                        extensionProcessor.write(extension, writer);
                    }

                    writeEnd(writer);
                }

                // Write extensions
                for (Object extension : service.getExtensions()) {
                    extensionProcessor.write(extension, writer);
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
                this.writeExtendedAttributes(writer, reference, extensionAttributeProcessor);

                // Write reference interface
                extensionProcessor.write(reference.getInterfaceContract(), writer);

                // Write bindings
                for (Binding binding : reference.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }

                // Write callback
                if (reference.getCallback() != null) {
                    Callback callback = reference.getCallback();
                    writeStart(writer, CALLBACK, policyProcessor.writePolicies(callback));

                    //write extended attributes
                    this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor);

                    // Write callback bindings
                    for (Binding binding : callback.getBindings()) {
                        extensionProcessor.write(binding, writer);
                    }

                    // Write extensions
                    for (Object extensions : callback.getExtensions()) {
                        extensionProcessor.write(extensions, writer);
                    }

                    writeEnd(writer);
                }

                // Write extensions
                for (Object extensions : reference.getExtensions()) {
                    extensionProcessor.write(extensions, writer);
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
                this.writeExtendedAttributes(writer, property, extensionAttributeProcessor);

                // Write property value
                writePropertyValue(property.getValue(), property.getXSDElement(), property.getXSDType(), writer);

                // Write extensions
                for (Object extension : property.getExtensions()) {
                    extensionProcessor.write(extension, writer);
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
            for (ComponentReference promoted: compositeReference.getPromotedReferences()) {
                promote.add(promoted.getName());
            }

            // Write <reference> element
            writeStart(writer, REFERENCE, new XAttr(NAME, reference.getName()),
                       new XAttr(PROMOTE, promote),
                       writeMultiplicity(reference),
                       policyProcessor.writePolicies(reference));

            //write extended attributes
            this.writeExtendedAttributes(writer, reference, extensionAttributeProcessor);

            // Write reference interface
            extensionProcessor.write(reference.getInterfaceContract(), writer);

            // Write bindings
            for (Binding binding : reference.getBindings()) {
                extensionProcessor.write(binding, writer);
            }

            // Write <callback> element
            if (reference.getCallback() != null) {
                Callback callback = reference.getCallback();
                writeStart(writer, CALLBACK);

                //write extended attributes
                this.writeExtendedAttributes(writer, callback, extensionAttributeProcessor);

                // Write callback bindings
                for (Binding binding : callback.getBindings()) {
                    extensionProcessor.write(binding, writer);
                }

                // Write extensions
                for (Object extension : callback.getExtensions()) {
                    extensionProcessor.write(extension, writer);
                }

                writeEnd(writer);
            }

            // Write extensions
            for (Object extension : reference.getExtensions()) {
                extensionProcessor.write(extension, writer);
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
            this.writeExtendedAttributes(writer, property, extensionAttributeProcessor);

            // Write property value
            writePropertyValue(property.getValue(), property.getXSDElement(), property.getXSDType(), writer);

            // Write extensions
            for (Object extension : property.getExtensions()) {
                extensionProcessor.write(extension, writer);
            }

            writeEnd(writer);
        }

        // Write <wire> elements
        for (Wire wire : composite.getWires()) {
            writeStart(writer, WIRE, new XAttr(SOURCE, wire.getSource().getName()), new XAttr(TARGET, wire.getTarget()
                .getName()), wire.isReplace() ? new XAttr(Constants.REPLACE, true) : null);

            //write extended attributes
            this.writeExtendedAttributes(writer, wire, extensionAttributeProcessor);

            // Write extensions
            for (Object extension : wire.getExtensions()) {
                extensionProcessor.write(extension, writer);
            }
            writeEnd(writer);
        }

        for (Object extension : composite.getExtensions()) {
        		extensionProcessor.write(extension, writer);
        }

        writeEndDocument(writer);
    }

    public void resolve(Composite composite, ModelResolver resolver) throws ContributionResolveException {

    	try {
    		this.monitor.pushContext("Composite: " + composite.getName());
	    	
	        // Resolve constraining type
	        ConstrainingType constrainingType = composite.getConstrainingType();
	        if (constrainingType != null) {
	            constrainingType = resolver.resolveModel(ConstrainingType.class, constrainingType);
	            composite.setConstrainingType(constrainingType);
	        }
	
	        // Resolve includes in the composite
	        for (int i = 0, n = composite.getIncludes().size(); i < n; i++) {
	            Composite include = composite.getIncludes().get(i);
	            if (include != null) {
	                include = resolver.resolveModel(Composite.class, include);
	                composite.getIncludes().set(i, include);
	            }
	        }
	
	        // Resolve extensions
	        for (Object extension : composite.getExtensions()) {
	            if (extension != null) {
	                extensionProcessor.resolve(extension, resolver);
	            }
	        }
	
	        //Resolve composite services and references
	        resolveContracts(composite, composite.getServices(), resolver);
	        resolveContracts(composite, composite.getReferences(), resolver);
	
	        // Resolve component implementations, services and references
	        for (Component component : composite.getComponents()) {
	            constrainingType = component.getConstrainingType();
	            if (constrainingType != null) {
	                constrainingType = resolver.resolveModel(ConstrainingType.class, constrainingType);
	                component.setConstrainingType(constrainingType);
	            }
	
	            //resolve component services and references
	            resolveContracts(component, component.getServices(), resolver);
	            resolveContracts(component, component.getReferences(), resolver);
	
	            for (ComponentProperty componentProperty : component.getProperties()) {
	                if (componentProperty.getFile() != null) {
	                    Artifact artifact = contributionFactory.createArtifact();
	                    artifact.setURI(componentProperty.getFile());
	                    artifact = resolver.resolveModel(Artifact.class, artifact);
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
	                implementation = resolveImplementation(implementation, resolver);
	                
	                validatePropertyTypes(component, implementation);
	
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
    		this.monitor.popContext();
    	} // end try 
    }

    /**
     * ASM50036: The property type specified for the property element of a component MUST be
     * compatible with the type of the property with the same @name declared in the component
     * type of the implementation used by the component. 
     */
    private void validatePropertyTypes(Component component, Implementation implementation) {
        for (Property cp : component.getProperties()) {
            Property ip = implementation.getProperty(cp.getName());
            if (cp != null && ip != null && cp.getXSDType() != null && ip.getXSDType() != null) {
                if (!cp.getXSDType().equals(ip.getXSDType())) {
                    // FIXME: how to test for incompatible instead of not equal
                    // TODO: TUSCANY-3236, should be error not warning
                    warning("IncompatiblePropertyType", component, component.getName(), cp.getName());
                }
            }
        }
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

    /**
     * Returns the monitor to use.
     *
     * @param extensionPoints
     * @return
     */
    private static Monitor monitor(ExtensionPointRegistry extensionPoints) {
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        if (utilities != null) {
            MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
            if (monitorFactory != null) {
                return monitorFactory.createMonitor();
            }
        }
        return null;
    }

}
