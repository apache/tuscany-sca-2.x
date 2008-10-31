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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;
import org.apache.tuscany.sca.policy.util.PolicyValidationException;
import org.apache.tuscany.sca.policy.util.PolicyValidationUtils;
import org.w3c.dom.Document;

/**
 * A composite processor. 
 * 
 * @version $Rev$ $Date$
 */
public class CompositeProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<Composite> {
    // FIXME: to be refactored
    private XPathFactory xPathFactory = XPathFactory.newInstance();
    
    protected StAXAttributeProcessor<Object> extensionAttributeProcessor;
    
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
        
    	this.extensionAttributeProcessor = extensionAttributeProcessor;
    }
    
    /**
     * Constructs a new composite processor
     * 
     * @param modelFactories
     * @param extensionProcessor
     * @param monitor
     */
    private CompositeProcessor(ModelFactoryExtensionPoint modelFactories,
             StAXArtifactProcessor extensionProcessor,
             StAXAttributeProcessor extensionAttributeProcessor,
             Monitor monitor) {
        
    	super(modelFactories.getFactory(ContributionFactory.class),
            modelFactories.getFactory(AssemblyFactory.class),
            modelFactories.getFactory(PolicyFactory.class),
            extensionProcessor, 
            monitor);
        
        this.extensionAttributeProcessor = extensionAttributeProcessor;
        
    }
    
    /**
     * Construct a new composite processor
     * 
     * @param contributionFactory
     * @param assemblyFactory
     * @param policyFactory
     * @param extensionProcessor
     */
    public CompositeProcessor(ContributionFactory contributionFactory,
            AssemblyFactory assemblyFactory,
            PolicyFactory policyFactory,
            StAXArtifactProcessor extensionProcessor,
            StAXAttributeProcessor extensionAttributeProcessor,
            Monitor monitor) {
        super(contributionFactory, assemblyFactory, policyFactory, extensionProcessor, monitor);
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
                                        ComponentReference promotedReference =
                                            assemblyFactory.createComponentReference();
                                        promotedReference.setUnresolved(true);
                                        promotedReference.setName(tokens.nextToken());
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
                                            source = source + "/";
                                            index = source.length() - 1;
                                        }
                                        source = source.substring(index + 1);
                                        if ("".equals(source)) {
                                            source = ".";
                                        }
                                    }
                                    XPath xpath = xPathFactory.newXPath();
                                    xpath.setNamespaceContext(reader.getNamespaceContext());
                                    try {
                                        componentProperty.setSourceXPathExpression(xpath.compile(source));
                                    } catch (XPathExpressionException e) {
                                    	ContributionReadException ce = new ContributionReadException(e);
                                    	error("ContributionReadException", xpath, ce);
                                        //throw ce;
                                    }
                                }
                                componentProperty.setFile(getString(reader, FILE));
                                
                                //handle extension attributes
                                this.readExtendedAttributes(reader, name, componentProperty, extensionAttributeProcessor);
    
                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(componentProperty, reader);
                                
                                // Read the property value
                                Document value = readPropertyValue(property.getXSDElement(), property.getXSDType(), reader);
                                property.setValue(value);
                                
                                component.getProperties().add(componentProperty);
                            } else {
    
                                // Read a <composite><property>
                                property = assemblyFactory.createProperty();
                                policyProcessor.readPolicies(property, reader);
                                readAbstractProperty(property, reader);
                                
                                // Read the property value
                                Document value = readPropertyValue(property.getXSDElement(), property.getXSDType(), reader);
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
    
                        } else if (OPERATION_QNAME.equals(name)) {
    
                            // Read an <operation>
                            ConfiguredOperation operation = assemblyFactory.createConfiguredOperation();
                            operation.setName(getString(reader, NAME));
                            operation.setUnresolved(true);
                            if (callback != null) {
                                policyProcessor.readPolicies(operation, reader);
                            } else {
                                policyProcessor.readPolicies(operation, reader);
                            }
                            
                            OperationsConfigurator opConfigurator = null;
                            if ( compositeService != null ) {
                                opConfigurator = compositeService;
                            } else if ( componentService != null ) {
                                opConfigurator = componentService;
                            } else if ( compositeReference != null ) {
                                opConfigurator = compositeReference;
                            } else if ( componentReference != null ) {
                                opConfigurator = componentReference;
                            }
                            
                            opConfigurator.getConfiguredOperations().add(operation);
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
                                        if (name.getNamespaceURI().equals(SCA10_NS)) {
                                        	error("UnexpectedInterfaceElement", extension);
                                            //throw new ContributionReadException("Unexpected <interface> element found. It should appear inside a <service> or <reference> element");
                                        } else {
                                            composite.getExtensions().add(extension);
                                        }
                                    }
                                } else if (extension instanceof Binding) {
                                    if ( extension instanceof PolicySetAttachPoint ) {
                                        IntentAttachPointType bindingType = intentAttachPointTypeFactory.createBindingType();
                                        bindingType.setName(name);
                                        bindingType.setUnresolved(true);
                                        ((PolicySetAttachPoint)extension).setType(bindingType);
                                    }
                                    // <service><binding> and
                                    // <reference><binding>
                                    if (callback != null) {
                                        callback.getBindings().add((Binding)extension);
                                    } else {
                                        if (contract != null) {
                                            contract.getBindings().add((Binding)extension);
                                        } else {
                                            if (name.getNamespaceURI().equals(SCA10_NS)) {
                                            	error("UnexpectedBindingElement", extension);
                                                //throw new ContributionReadException("Unexpected <binding> element found. It should appear inside a <service> or <reference> element");
                                            } else {
                                                composite.getExtensions().add(extension);
                                            }
                                        }
                                    }
    
                                } else if (extension instanceof Implementation) {
                                    if ( extension instanceof PolicySetAttachPoint ) {
                                        IntentAttachPointType implType = intentAttachPointTypeFactory.createImplementationType();
                                        implType.setName(name);
                                        implType.setUnresolved(true);
                                        ((PolicySetAttachPoint)extension).setType(implType);
                                    }
                                    // <component><implementation>
                                    if (component != null) {
                                        component.setImplementation((Implementation)extension);
                                    } else {
                                        if (name.getNamespaceURI().equals(SCA10_NS)) {
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
                writeStart(writer, REFERENCE, new XAttr(NAME, reference.getName()),
                           new XAttr(AUTOWIRE, reference.getAutowire()),
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
            writeStart(writer, WIRE, new XAttr(SOURCE, wire.getSource().getName()), new XAttr(TARGET, wire
                .getTarget().getName()));
            
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
        
        //resolve intents and policy sets
        List<Intent> compositeIntents = null;
        List<PolicySet> compositePolicySets = null;
        List<PolicySet> compositeApplicablePolicySets = null;
        resolveIntents(composite.getRequiredIntents(), resolver);
        resolvePolicySets(composite.getPolicySets(), resolver);
        resolvePolicySets(composite.getApplicablePolicySets(), resolver);
        compositeIntents = composite.getRequiredIntents();
        compositePolicySets = composite.getPolicySets();
        compositeApplicablePolicySets = composite.getApplicablePolicySets();

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
            
            //resolve intents and policy sets
            resolveIntents(component.getRequiredIntents(), resolver);
            resolvePolicySets(component.getPolicySets(), resolver);
            resolvePolicySets(component.getApplicablePolicySets(), resolver);
            
            //inherit composite intents and policysets
            PolicyComputationUtils.addDefaultPolicies(compositeIntents,
                                                      compositePolicySets,
                                                      component.getRequiredIntents(),
                                                      component.getPolicySets());

            addInheritedPolicySets(compositeApplicablePolicySets, component.getApplicablePolicySets());

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
                try {
                    //resolve intents and policysets specified on this implementation
                    //before copying them over to the component.  Before that, from the component
                    //copy over the applicablePolicySets alone as it might have to be
                    //used to validate the policysets specified on the implementation
                    
                    resolveImplIntentsAndPolicySets(implementation, 
                                                    component.getApplicablePolicySets(), 
                                                    resolver);

                    copyPoliciesToComponent(component, implementation, resolver, true);
                    
                    //now resolve the implementation so that even if there is a shared instance
                    //for this that is resolved, the specified intents and policysets are safe in the
                    //component and not lost
                    implementation = resolveImplementation(implementation, resolver);
                    
                    //resolved implementation may contain intents and policysets specified at 
                    //componentType (either in the componentType side file or in annotations if its a 
                    //java implementation).  This has to be consolidated in to the component.
                    copyPoliciesToComponent(component, implementation, resolver, false);
                    
                    component.setImplementation(implementation);
                } catch ( PolicyValidationException e ) {
                	error("PolicyImplValidationException", resolver, component.getName(), e.getMessage());
                    //throw new ContributionResolveException("PolicyValidation exception when processing implementation of component '" 
                                                           //+ component.getName() + "' due to " + e.getMessage(), e);
                }
            
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
        }
    }
    
    private void resolveImplIntentsAndPolicySets(Implementation implementation,
                                                 List<PolicySet> inheritedApplicablePolicySets,
                                                 ModelResolver resolver) throws ContributionResolveException,
                                                                                 PolicyValidationException
                                                        {
        if ( implementation instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policiedImpl = (PolicySetAttachPoint)implementation;
            
            policiedImpl.getApplicablePolicySets().addAll(inheritedApplicablePolicySets);
            
            resolveIntents(policiedImpl.getRequiredIntents(), resolver);
            PolicyValidationUtils.validateIntents(policiedImpl, policiedImpl.getType());
            
            resolvePolicySets(policiedImpl.getPolicySets(), resolver);
            resolvePolicySets(policiedImpl.getApplicablePolicySets(), resolver);
            
            PolicyValidationUtils.validatePolicySets(policiedImpl);
            
            if ( implementation instanceof OperationsConfigurator ) {
                for ( ConfiguredOperation implConfOp : ((OperationsConfigurator)implementation).getConfiguredOperations() ) {
                    resolveIntents(implConfOp.getRequiredIntents(), resolver);
                    PolicyValidationUtils.validateIntents(implConfOp, policiedImpl.getType());
                    
                    resolvePolicySets(implConfOp.getPolicySets(), resolver);
                    resolvePolicySets(implConfOp.getApplicablePolicySets(), resolver);
                    //add the inherited applicablePolicysets
                    addInheritedPolicySets(policiedImpl.getApplicablePolicySets(), implConfOp.getApplicablePolicySets());
                    
                    PolicyValidationUtils.validatePolicySets(implConfOp, policiedImpl.getType());
                    
                    PolicyComputationUtils.addDefaultPolicies(
                                            ((PolicySetAttachPoint)implementation).getRequiredIntents(),
                                            ((PolicySetAttachPoint)implementation).getPolicySets(),
                                            implConfOp.getRequiredIntents(),
                                            implConfOp.getPolicySets());
                }
            }
        }
    }
    
    private void copyPoliciesToComponent(Component component, 
                                         Implementation implementation, 
                                         ModelResolver resolver,
                                         boolean clearImplSettings) throws ContributionResolveException {
        if (implementation instanceof PolicySetAttachPoint) {
            // Add implementation policies into component, since implementation instances are 
            // reused and it's likely that this implementation instance will not hold after its resolution.
            // On the first call to this method (clearImplSettings=true), we are moving policies from the
            // implementation XML element up to the component.  In this case if there are mutually exclusive
            // policies we must clear the component policy so that the implementation policy "wins".
            // On the second call to this method (clearImplSettings=false), we are moving policies from the
            // componentType implementation up to the component.  In this case if there are mutually
            // exclusive policies it is an error.  This error will be detected later in the PolicyComputer.
            if (clearImplSettings) {
                for (Intent intent : ((PolicySetAttachPoint)implementation).getRequiredIntents()) {
                    for (Intent excluded : intent.getExcludedIntents()) {
                        if (component.getRequiredIntents().contains(excluded)) {
                            component.getRequiredIntents().remove(excluded);
                        }
                        for (Iterator i = component.getPolicySets().iterator(); i.hasNext(); ) {
                            PolicySet cmpPolicySet = (PolicySet) i.next();
                            if (cmpPolicySet.getProvidedIntents().contains(excluded)) {
                                i.remove();
                            }
                        }
                    }
                }
                for (PolicySet policySet : ((PolicySetAttachPoint)implementation).getPolicySets()) {
                    for (Intent intent : policySet.getProvidedIntents()) {
                        for (Intent excluded : intent.getExcludedIntents()) {
                            if (component.getRequiredIntents().contains(excluded)) {
                                component.getRequiredIntents().remove(excluded);
                            }
                            for (Iterator i = component.getPolicySets().iterator(); i.hasNext(); ) {
                                PolicySet cmpPolicySet = (PolicySet) i.next();
                                if (cmpPolicySet.getProvidedIntents().contains(excluded)) {
                                    i.remove();
                                }
                            }
                        }
                    }
                }
            }
            component.getRequiredIntents().addAll(((PolicySetAttachPoint)implementation).getRequiredIntents());
            component.getPolicySets().addAll(((PolicySetAttachPoint)implementation).getPolicySets());
            component.getApplicablePolicySets().addAll(((PolicySetAttachPoint)implementation).getApplicablePolicySets());
            
            if ( implementation instanceof OperationsConfigurator ) {
                boolean notFound;
                List<ConfiguredOperation> opsFromImplementation = new ArrayList<ConfiguredOperation>();
                List<ConfiguredOperation> implConfOperations = 
                    new ArrayList<ConfiguredOperation>(((OperationsConfigurator)implementation).getConfiguredOperations());
                for ( ConfiguredOperation implConfOp : implConfOperations ) {
                    notFound = true;
                    for ( ConfiguredOperation compConfOp : ((OperationsConfigurator)component).getConfiguredOperations() ) {
                        if ( implConfOp.getName().equals(compConfOp.getName()) ) {
                            notFound = false;

                            if (clearImplSettings) {
                                for (Intent intent : implConfOp.getRequiredIntents()) {
                                    for (Intent excluded : intent.getExcludedIntents()) {
                                        if (compConfOp.getRequiredIntents().contains(excluded)) {
                                            compConfOp.getRequiredIntents().remove(excluded);
                                        }
                                    }
                                }
                                for (PolicySet policySet : implConfOp.getPolicySets()) {
                                    for (Intent intent : policySet.getProvidedIntents()) {
                                        for (Intent excluded : intent.getExcludedIntents()) {
                                            if (compConfOp.getRequiredIntents().contains(excluded)) {
                                                compConfOp.getRequiredIntents().remove(excluded);
                                            }
                                            for (Iterator i = compConfOp.getPolicySets().iterator(); i.hasNext(); ) {
                                                PolicySet cmpPolicySet = (PolicySet) i.next();
                                                if (cmpPolicySet.getProvidedIntents().contains(excluded)) {
                                                    i.remove();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            addInheritedIntents(implConfOp.getRequiredIntents(), compConfOp.getRequiredIntents());
                            addInheritedPolicySets(implConfOp.getPolicySets(), compConfOp.getPolicySets());
                            addInheritedPolicySets(implConfOp.getApplicablePolicySets(), compConfOp.getApplicablePolicySets());
                        }
                    }
                    
                    if ( notFound ) {
                        opsFromImplementation.add(implConfOp);
                    }
                    
                    if ( clearImplSettings ) {
                        ((OperationsConfigurator)implementation).getConfiguredOperations().remove(implConfOp);
                    }
                }
                ((OperationsConfigurator)component).getConfiguredOperations().addAll(opsFromImplementation);
            }
            
            if ( clearImplSettings ) {
                ((PolicySetAttachPoint)implementation).getRequiredIntents().clear();
                ((PolicySetAttachPoint)implementation).getPolicySets().clear();
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
    private static ModelFactoryExtensionPoint modelFactories(ExtensionPointRegistry extensionPoints) {
        return extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
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
