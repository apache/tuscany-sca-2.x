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
package org.apache.tuscany.sca.builder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.ContractBuilder;
import org.apache.tuscany.sca.assembly.builder.ImplementationBuilder;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.assembly.xsd.Constants;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @version $Rev$ $Date$
 */
public class ComponentBuilderImpl {
    protected static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    protected static final String BINDING_SCA = "binding.sca";
    protected static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    private CompositeComponentTypeBuilderImpl componentTypeBuilder;
    protected ComponentPolicyBuilderImpl policyBuilder;
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    protected TransformerFactory transformerFactory;
    private InterfaceContractMapper interfaceContractMapper;
    private BuilderExtensionPoint builders;
    private Mediator mediator;
    private ContractBuilder contractBuilder;

    public ComponentBuilderImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        documentBuilderFactory = modelFactories.getFactory(DocumentBuilderFactory.class);
        transformerFactory = modelFactories.getFactory(TransformerFactory.class);

        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
        policyBuilder = new ComponentPolicyBuilderImpl(registry);
        builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        mediator = new MediatorImpl(registry);
        contractBuilder = builders.getContractBuilder();
    }

    public void setComponentTypeBuilder(CompositeComponentTypeBuilderImpl componentTypeBuilder) {
        this.componentTypeBuilder = componentTypeBuilder;
    }

    /**
     * Configure the component based on its component type using OASIS rules
     * 
     * @Param outerCompoment the component that uses the parentComposite as its implementation
     * @Param parentComposite the composite that contains the component being configured. Required for property processing
     * @param component the component to be configured
     */
    public void configureComponentFromComponentType(Component outerComponent, Composite parentComposite, Component component, BuilderContext context) {

        Monitor monitor = context.getMonitor();
        monitor.pushContext("Component: " + component.getName().toString());
        
        try {
            // do any work we need to do before we calculate the component type
            // for this component. Anything that needs to be pushed down the promotion
            // hierarchy must be done before we calculate the component type
            
            // check that the implementation is present
            if (!isComponentImplementationPresent(component, monitor)){
                return;
            }
    
            // carry out any implementation specific builder processing
            Implementation impl = component.getImplementation();
            if (impl != null) {
                ImplementationBuilder builder = builders.getImplementationBuilder(impl.getType());
                if (builder != null) {
                    builder.build(component, impl, context);
                }
            }
    
            // Properties on the composite component type are not affected by the components 
            // that the composite contains. Instead the child components might source  
            // composite level property values. Hence we have to calculate whether the component 
            // type property value should be overridden by this component's property value 
            // before we go ahead and calculate the component type
            configureProperties(outerComponent, parentComposite, component, monitor);
    
            // create the component type for this component 
            // taking any nested composites into account
            createComponentType(component, context);
    
            // configure services based on the calculated component type
            configureServices(component, context);
    
            // configure services based on the calculated component type
            configureReferences(component, context);
            
            // NOTE: configureServices/configureReferences may add callback references and services
            policyBuilder.configure(component, context);
            
        } finally {
            monitor.popContext();
        }         
    }
    
    /**
     * Checks that a component implementation is present and resolved 
     * before doing anything else
     * 
     * @param component
     * @return true if the implementation is present and resolved
     */
    private boolean isComponentImplementationPresent(Component component, Monitor monitor){
        Implementation implementation = component.getImplementation();
        if (implementation == null) {
            // A component must have an implementation
            Monitor.error(monitor, 
                          this, 
                          Messages.ASSEMBLY_VALIDATION, 
                          "NoComponentImplementation", 
                          component.getName());
            return false;
        } else if (implementation.isUnresolved()) {
            // The implementation must be fully resolved
            Monitor.error(monitor, 
                          this, 
                          Messages.ASSEMBLY_VALIDATION,
                          "UnresolvedComponentImplementation", 
                          component, 
                          component.getName(), 
                          implementation.getURI());
            return false;
        }        
        
        return true;
    }

    /**
     * Use the component type builder to build the component type for 
     * this component. 
     * 
     * @param component
     */
    private void createComponentType(Component component, BuilderContext context) {
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            componentTypeBuilder.createComponentType(component, (Composite)implementation, context);
        }
    }

    /**
     * Configure this component's services based on the services in its 
     * component type and the configuration from the composite file
     * 
     * @param component
     */
    private void configureServices(Component component, BuilderContext context) {
        Monitor monitor = context.getMonitor();

        // If the component type has services that are not described in this
        // component then create services for this component
        addServicesFromComponentType(component, monitor);

        // Connect this component's services to the 
        // services from its component type
        connectServicesToComponentType(component, monitor);

        // look at each component service in turn and calculate its 
        // configuration based on OASIS rules
        for (ComponentService componentService : component.getServices()) {
                      
            Service componentTypeService = componentService.getService();

            if (componentTypeService == null) {
                // raise error?
                // can be null in some of the assembly-xml unit tests
                continue;
            }

            // interface contracts
            calculateServiceInterfaceContract(component, componentService, componentTypeService, monitor);

            // bindings
            calculateBindings(component, componentService, componentTypeService, context);

            // add callback reference model objects
            createCallbackReference(component, componentService, monitor);
        }
    }

    /**
     * Configure this component's references based on the references in its 
     * component type and the configuration from the composite file
     * 
     * @param component
     */
    private void configureReferences(Component component, BuilderContext context) {
        Monitor monitor = context.getMonitor();
        
        // If the component type has references that are not described in this
        // component then create references for this component
        addReferencesFromComponentType(component, monitor);

        // Connect this component's references to the 
        // references from its component type
        connectReferencesToComponentType(component, monitor);

        // look at each component reference in turn and calculate its 
        // configuration based on OASIS rules
        for (ComponentReference componentReference : component.getReferences()) {
            Reference componentTypeReference = componentReference.getReference();

            if (componentTypeReference == null) {
                // raise error?
                // can be null in some of the assembly-xml unit tests
                continue;
            }

            // reference multiplicity
            reconcileReferenceMultiplicity(component, componentReference, componentTypeReference, monitor);

            // interface contracts
            calculateReferenceInterfaceContract(component, componentReference, componentTypeReference, monitor);

            // bindings
            calculateBindings(componentReference, componentTypeReference);

            // add callback service model objects
            createCallbackService(component, componentReference, monitor);

            // Propagate autowire setting from the component down the structural 
            // hierarchy
            if (componentReference.getAutowire() == null) {
                componentReference.setAutowire(component.getAutowire());
            }
        }
    }

    /**
     * Configure this component's properties based on the properties in its 
     * component type and the configuration from the composite file
     * 
     * @param component
     */
    private void configureProperties(Component outerComponent, Composite parentComposite, Component component, Monitor monitor) {
        // If the component type has properties that are not described in this
        // component then create properties for this component
        addPropertiesFromComponentType(component, monitor);

        // Connect this component's properties to the 
        // properties from its component type
        connectPropertiesToComponentType(component, monitor);

        // Reconcile component properties and their component type properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            reconcileComponentPropertyWithComponentType(component, componentProperty, monitor);

            // configure the property value based on the @source attribute
            // At the moment this is done in the parent composite component
            // type calculation 
            processPropertySourceAttribute(outerComponent, parentComposite, component, componentProperty, monitor);

            // configure the property value based on the @file attribute
            processPropertyFileAttribute(component, componentProperty, monitor);
            
            // Check that a value is supplied
            if (componentProperty.isMustSupply() && !isPropertyValueSet(componentProperty)) {
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertyMustSupplyNull", 
                              component.getName(), 
                              componentProperty.getName());
            }
            
            // check that not too many values are supplied
            if (!componentProperty.isMany() && isPropertyManyValued(componentProperty)){
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertyHasManyValues", 
                              component.getName(), 
                              componentProperty.getName());                
            }
            
            // check the property type
            checkComponentPropertyType(component, componentProperty, monitor);
            
        }
    }

    private void addServicesFromComponentType(Component component, Monitor monitor) {

        // Create a component service for each service
        if (component.getImplementation() != null) {
            for (Service service : component.getImplementation().getServices()) {
                // check for duplicate service names in implementation
                if (service != component.getImplementation().getService(service.getName())){
                    Monitor.error(monitor, 
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "DuplicateImplementationServiceName", 
                                  component.getName(), 
                                  service.getName());
                }
                
                ComponentService componentService = (ComponentService)component.getService(service.getName());

                // if the component doesn't have a service with the same name as the 
                // component type service then create one
                if (componentService == null) {
                    componentService = assemblyFactory.createComponentService();
                    componentService.setForCallback(service.isForCallback());
                    String name = service.getName();
                    componentService.setName(name);
                    component.getServices().add(componentService);
                }
            }
        }
    }

    private void addReferencesFromComponentType(Component component, Monitor monitor) {

        // Create a component reference for each reference
        if (component.getImplementation() != null) {
            for (Reference reference : component.getImplementation().getReferences()) {
                // check for duplicate reference names in implementation
                if (reference != component.getImplementation().getReference(reference.getName())){
                    Monitor.error(monitor, 
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "DuplicateImplementationReferenceName", 
                                  component.getName(), 
                                  reference.getName());
                } 
                
                ComponentReference componentReference = (ComponentReference)component.getReference(reference.getName());

                // if the component doesn't have a reference with the same name as the 
                // component type reference then create one
                if (componentReference == null) {
                    componentReference = assemblyFactory.createComponentReference();
                    componentReference.setForCallback(reference.isForCallback());
                    componentReference.setName(reference.getName());
                    componentReference.setReference(reference);
                    component.getReferences().add(componentReference);
                }
            }
        }
    }

    private void addPropertiesFromComponentType(Component component, Monitor monitor) {

        // Create component property for each property
        if (component.getImplementation() != null) {
            for (Property property : component.getImplementation().getProperties()) {
                // check for duplicate property names in implementation
                if (property != component.getImplementation().getProperty(property.getName())){
                    Monitor.error(monitor, 
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "DuplicateImplementationPropertyName", 
                                  component.getName(), 
                                  property.getName());
                }                
                ComponentProperty componentProperty = (ComponentProperty)component.getProperty(property.getName());

                // if the component doesn't have a property with the same name as
                // the component type property then create one
                if (componentProperty == null) {
                    componentProperty = assemblyFactory.createComponentProperty();
                    componentProperty.setName(property.getName());
                    componentProperty.setValue(property.getValue());
                    componentProperty.setMany(property.isMany());
                    componentProperty.setMustSupply(property.isMustSupply());
                    componentProperty.setXSDElement(property.getXSDElement());
                    componentProperty.setXSDType(property.getXSDType());
                    componentProperty.setProperty(property);
                    component.getProperties().add(componentProperty);
                }
            }
        }
    }

    private void connectServicesToComponentType(Component component, Monitor monitor) {

        // Connect each component service to the corresponding component type service
        for (ComponentService componentService : component.getServices()) {
            // check for duplicate service names in component
            if (componentService != component.getService(componentService.getName())){
                Monitor.error(monitor, 
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "DuplicateComponentServiceName", 
                              component.getName(), 
                              componentService.getName());
            }
            
            if (componentService.getService() != null || componentService.isForCallback()) {
                continue;
            }

            if (component.getImplementation() == null) {
                // is null in some of our basic unit tests
                continue;
            }

            Service service = component.getImplementation().getService(componentService.getName());

            if (service != null) {
                componentService.setService(service);
            } else {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ServiceNotFoundForComponentService",
                              component.getName(),
                              componentService.getName());
            }
        }
    }

    private void connectReferencesToComponentType(Component component, Monitor monitor) {

        // Connect each component reference to the corresponding component type reference
        for (ComponentReference componentReference : component.getReferences()) {
            // check for duplicate reference names in component
            if (componentReference != component.getReference(componentReference.getName())){
                Monitor.error(monitor, 
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "DuplicateComponentReferenceName", 
                              component.getName(), 
                              componentReference.getName());
            }
            
            if (componentReference.getReference() != null || componentReference.isForCallback()) {
                continue;
            }

            if (component.getImplementation() == null) {
                // is null in some of our basic unit tests
                continue;
            }

            Reference reference = component.getImplementation().getReference(componentReference.getName());

            if (reference != null) {
                componentReference.setReference(reference);
            } else {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceNotFoundForComponentReference",
                              component.getName(),
                              componentReference.getName());
            }
        }
    }

    private void connectPropertiesToComponentType(Component component, Monitor monitor) {
        // Connect each component property to the corresponding component type property
        for (ComponentProperty componentProperty : component.getProperties()) {
            // check for duplicate property names in component
            if (componentProperty != component.getProperty(componentProperty.getName())){
                Monitor.error(monitor, 
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "DuplicateComponentPropertyName", 
                              component.getName(), 
                              componentProperty.getName());
            }
            
            Property property = component.getImplementation().getProperty(componentProperty.getName());
            
            if (property != null) {
                componentProperty.setProperty(property);
            } else {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "PropertyNotFound",
                              component.getName(),
                              componentProperty.getName());
            }
        }
    }

    private void reconcileReferenceMultiplicity(Component component,
                                                Reference componentReference,
                                                Reference componentTypeReference,
                                                Monitor monitor) {
        if (componentReference.getMultiplicity() != null) {
            if (!isValidMultiplicityOverride(componentTypeReference.getMultiplicity(), componentReference
                .getMultiplicity())) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceIncompatibleMultiplicity",
                              component.getName(),
                              componentReference.getName());
            }
        } else {
            componentReference.setMultiplicity(componentTypeReference.getMultiplicity());
        }
    }

    private void reconcileComponentPropertyWithComponentType(Component component, ComponentProperty componentProperty, Monitor monitor) {
        Property componentTypeProperty = componentProperty.getProperty();
        if (componentTypeProperty != null) {

            // Check that a component property does not override the
            // mustSupply attribute
            if (!componentTypeProperty.isMustSupply() && componentProperty.isMustSupply()) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "PropertyMustSupplyIncompatible",
                              component.getName(),
                              componentProperty.getName());
            }

            // Default to the mustSupply attribute specified on the property
            if (!componentProperty.isMustSupply())
                componentProperty.setMustSupply(componentTypeProperty.isMustSupply());

            // Default to the value specified on the component type property
            if (!isPropertyValueSet(componentProperty)) {
                componentProperty.setValue(componentTypeProperty.getValue());
            }

            // Override the property value for the composite
            if (component.getImplementation() instanceof Composite) {
                componentTypeProperty.setValue(componentProperty.getValue());
            }

            // Check that a component property does not override the
            // many attribute
            if (!componentTypeProperty.isMany() && componentProperty.isMany()) {
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertyOverrideManyAttribute", 
                              component.getName(), 
                              componentProperty.getName());
            }

            // Default to the many attribute defined on the property
            componentProperty.setMany(componentTypeProperty.isMany());

            // Default to the type and element defined on the property
            if (componentProperty.getXSDType() == null) {
                componentProperty.setXSDType(componentTypeProperty.getXSDType());
            }
            if (componentProperty.getXSDElement() == null) {
                componentProperty.setXSDElement(componentTypeProperty.getXSDElement());
            }

            // Check that a type or element are specified
            if (componentProperty.getXSDElement() == null && componentProperty.getXSDType() == null) {
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "NoTypeForComponentProperty", 
                              component.getName(), 
                              componentProperty.getName());
            }
            
            // check that the types specified in the component type and component property match
            if ( componentProperty.getXSDElement() != null &&
                 !componentProperty.getXSDElement().equals(componentTypeProperty.getXSDElement())){
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertXSDElementsDontMatch", 
                              component.getName(), 
                              componentProperty.getName(),
                              componentProperty.getXSDElement(),
                              componentTypeProperty.getXSDElement());                
            }
            
            if ( componentProperty.getXSDType() != null &&
                    !componentProperty.getXSDType().equals(componentTypeProperty.getXSDType())){
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertXSDTypesDontMatch", 
                              component.getName(), 
                              componentProperty.getName(),
                              componentProperty.getXSDType(),
                              componentTypeProperty.getXSDType());                
            }            
        }
    }
    
    /**
     * checks that the component property value is correctly typed when compared with 
     * the type specified in the composite file property 
     * 
     * TODO - Don't yet handle multiplicity
     *        Need to check composite properties also
     * 
     * @param component
     * @param componentProperty
     * @param monitor
     */
    private void checkComponentPropertyType(Component component, ComponentProperty componentProperty, Monitor monitor) {
        
        QName propertyXSDType = componentProperty.getXSDType();
        QName propertyElementType = componentProperty.getXSDElement();
        
        if (propertyXSDType != null){
            if (propertyXSDType.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
                // The property has a simple schema type so we can use the 
                // data binding framework to see if the XML value can be transformed 
                // into a simple Java value
                Document doc = (Document)componentProperty.getValue();
                Node source = (doc == null) ? null : doc.getDocumentElement().getFirstChild();
                DataType<XMLType> sourceDataType = new DataTypeImpl<XMLType>(DOMDataBinding.NAME, 
                                                                             Node.class,
                                                                             new XMLType(null, componentProperty.getXSDType()));
                DataType<XMLType> targetDataType = new DataTypeImpl<XMLType>(JAXBDataBinding.NAME,
                                                                             Object.class,
                                                                             new XMLType(null, componentProperty.getXSDType()));                                                       
                try {
                    mediator.mediate(source, sourceDataType, targetDataType, null);
                } catch (Exception ex){
                    Monitor.error(monitor, 
                                  this, 
                                  Messages.ASSEMBLY_VALIDATION, 
                                  "PropertyValueDoesNotMatchSimpleType", 
                                  componentProperty.getName(),
                                  component.getName(), 
                                  componentProperty.getXSDType().toString()); 
                }
            } else {
                // The property has a complex schema type so we fluff up a schema 
                // and use that to validate the property value
                XSDefinition xsdDefinition = (XSDefinition)componentProperty.getXSDDefinition();
                
                if (xsdDefinition != null) {
                    try {
                        // create schema factory for XML schema
                        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                      
                        Document schemaDom = xsdDefinition.getSchema().getSchemaDocument();
                        
                        String valueSchema = null; 
                        Schema schema = null;
                        
                        if (componentProperty.getXSDType().getNamespaceURI().equals(Constants.SCA11_NS)){
                            // include the referenced schema as it's already in the OASIS namespace
                            valueSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
                                          "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" "+
                                                  "xmlns:sca=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" "+
                                                  "xmlns:__tmp=\"" + componentProperty.getXSDType().getNamespaceURI() + "\" "+
                                                  "targetNamespace=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" " +
                                                  "elementFormDefault=\"qualified\">" +
                                              "<include schemaLocation=\"" + xsdDefinition.getLocation() + "\"/>" +
//                                              "<element name=\"value\" type=\"" + "__tmp:" + componentProperty.getXSDType().getLocalPart() + "\"/>" +
                                          "</schema>";
//                            Source sources[] = {new StreamSource(new StringReader(valueSchema))};
                            Source sources[] = {new DOMSource(schemaDom)};
                            schema = factory.newSchema(sources);
                            
                            // The SCA schema already contains a "value" element so I can't create this schema
                            // the SCA value element is an any so return assuming that it validates. 
                            return; 
                        } else {
                            // import the referenced schema
                            valueSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
                        		          "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" "+
                                                  "xmlns:sca=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" "+
                                                  "xmlns:__tmp=\"" + componentProperty.getXSDType().getNamespaceURI() + "\" "+
                                                  "targetNamespace=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" " +
                                                  "elementFormDefault=\"qualified\">" +
                                              "<import namespace=\"" + componentProperty.getXSDType().getNamespaceURI() + "\"/>" +
                                              "<element name=\"value\" type=\"" + "__tmp:" + componentProperty.getXSDType().getLocalPart() + "\"/>" +
                                          "</schema>";
                            Source sources[] = {new DOMSource(schemaDom), new StreamSource(new StringReader(valueSchema))};
                            schema = factory.newSchema(sources);
                        }
                                                
                        // get the value child of the property element
                        Document property = (Document)componentProperty.getValue();
                        Element value = (Element)property.getDocumentElement().getFirstChild();
                
                        // validate the element property/value from the DOM
                        Validator validator = schema.newValidator();
                        validator.validate(new DOMSource(value));
                        
                    } catch (Exception e) {
                        Monitor.error(monitor, 
                                this, 
                                Messages.ASSEMBLY_VALIDATION, 
                                "PropertyValueDoesNotMatchComplexType", 
                                componentProperty.getName(),
                                component.getName(), 
                                componentProperty.getXSDType().toString(), 
                                e.getMessage());
                    }
                }
            }
        } else if (propertyElementType != null) {
            // TODO - TUSCANY-3530 - still need to add validation for element type
            
        }
    }    

    /**
     * If the property has a source attribute use this to retrieve the value from a 
     * property in the parent composite
     * 
     * @param parentCompoent the composite that contains the component
     * @param component
     * @param componentProperty
     */
    private void processPropertySourceAttribute(Component outerComponent,
                                                Composite parentComposite,
                                                Component component,
                                                ComponentProperty componentProperty,
                                                Monitor monitor) {
        String source = componentProperty.getSource();

        if (source == null) return;
        
        try {
            String sourceName = extractSourcePropertyName( source );

            Property sourceProp = null;
            if (outerComponent != null) {
                sourceProp = outerComponent.getProperty(sourceName);
            } else {
                sourceProp = parentComposite.getProperty(sourceName);
            }
            if (sourceProp == null) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "PropertySourceNotFound",
                              source,
                              componentProperty.getName(),
                              component.getName());
            } else {

	            Document sourcePropValue = (Document)sourceProp.getValue();
	
	            try {
	                // FIXME: How to deal with namespaces?
	                Document node =
	                    evaluateXPath2(sourcePropValue,
	                                  componentProperty.getSourceXPathExpression(),
	                                  documentBuilderFactory);
	
	                if (node != null) {
	                    componentProperty.setValue(node);
	                } else {
	                    Monitor.warning(monitor,
	                                    this,
	                                    Messages.ASSEMBLY_VALIDATION,
	                                    "PropertyXpathExpressionReturnedNull",
	                                    component.getName(),
	                                    componentProperty.getName(),
	                                    componentProperty.getSource());
	                } // end if
	           
	            } catch (Exception ex) {
	                Monitor.error(monitor,
	                              this,
	                              Messages.ASSEMBLY_VALIDATION,
	                              "PropertySourceXpathInvalid",
	                              source,
	                              componentProperty.getName(),
	                              component.getName(),
	                              ex);
	            } // end try
            } // end if
        } catch (IllegalArgumentException e ) {
            Monitor.error(monitor,
                          this,
                          Messages.ASSEMBLY_VALIDATION,
                          "PropertySourceValueInvalid",
                          source,
                          componentProperty.getName(),
                          component.getName());
        } // end try 
    } // end method
    
    /**
     * Extracts the name of the source property from the value of an @source attribute string
     * @param source - the value of the @source attribute
     * @return - the source property name as a String
     */
    private String extractSourcePropertyName( String source ) throws IllegalArgumentException {
    	String propertyName = null;
    	
        // Possible values for the source string:
    	// a) $name
    	// b) $name/expression
    	// c) $name[xx]
    	// d) $name[xx]/expression
    	// ...and note that the expression MAY contain '/' and '[' characters
    	if( source.charAt(0) != '$' ) throw new IllegalArgumentException("Source value does not start with '$'");	
    	
        int index = source.indexOf('/');
        int bracket = source.indexOf('[');
        
        if( index == -1 && bracket == -1 ) {
        	// Format a) - simply remove the '$'
        	propertyName = source.substring(1);
        } else if ( bracket == -1 ) {
        	// Format b) - remove the '$' and the '/' and everything following it
        	propertyName = source.substring(1, index);
        } else if ( index == -1 ) {
        	// Format c) - remove the '$' and the '[' and everything following it
        	propertyName = source.substring(1, bracket);
        } else {
        	// Format d) - but need to ensure that the '[' is BEFORE the '/'
        	if( bracket < index ) {
        		// Format d) - remove the '$' and the '[' and everything following it
        		propertyName = source.substring(1, bracket);
        	} else {
        		// Format b) variant where there is a '[' in the expression...
        		propertyName = source.substring(1, index);
        	} // end if
        } // end if
        
    	return propertyName;
    } // end method extractSourcePropertyName( source, monitor )

    /**
     * If the property has a file attribute use this to retrieve the property value from a local file
     * Format of the property value file is defined in the SCA Assembly specification in ASM50046
     * 
     * @param component the component holding the property
     * @param componentProperty - the property 
     * @param monitor - a Monitor object for reporting problems
     */
    /**
     * Property file format:
     * MUST contain a <sca:values/> element
     * - either contains one or more <sca:value/> subelements (mandatory for property with a simple XML type)
     * - or contains one or more global elements of the type of the property
     * 
     * eg.
     * 	<?xml version="1.0" encoding="UTF-8"?>
     *  <values>
     *     <value>MyValue</value>
     *  </values>
     *  
     *  <?xml version="1.0" encoding="UTF-8"?>
     *  <values>
     *     <foo:fooElement>
     *        <foo:a>AValue</foo:a>
     *        <foo:b>InterestingURI</foo:b>
     *     </foo:fooElement>
     *  </values/>
     */
    private void processPropertyFileAttribute(Component component, ComponentProperty componentProperty, Monitor monitor) {
        String file = componentProperty.getFile();
        if (file == null) return;
        try {
/*        	
            URI uri = URI.create(file);
            // URI resolution for relative URIs is done when the composite is resolved.
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            InputStream is = null;           
            try {

                is = connection.getInputStream();

                Source streamSource = new SAXSource(new InputSource(is));
                DOMResult result = new DOMResult();
                javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(streamSource, result);

                Document document = (Document)result.getNode();
*/                
                Document document = readPropertyFileData( file );
                
                Element docElement = document.getDocumentElement();
                if( docElement == null ) throw new Exception("Property File has no XML document element");
                
                if( !"values".equals( docElement.getLocalName() ) ) {
                	throw new Exception("Property File does not start with <values/> element");
                } // end if
                
                // The property value is the subelement(s) of the <values/> element
                NodeList values = docElement.getChildNodes();
                
                Document newdoc = documentBuilderFactory.newDocumentBuilder().newDocument();
                Element newProperty = newdoc.createElementNS(SCA11_NS, "property");
                newdoc.appendChild( newProperty );
                
                int count = 0;
                
                // Copy the property values under the new <property/> element
                for( int i = 0 ; i < values.getLength() ; i++ ) {
                	Node valueNode = values.item(i);
                	// Only <value/> elements or global elements are valid values...
                	if( valueNode.getNodeType() == Node.ELEMENT_NODE ) {
                		newProperty.appendChild(newdoc.importNode(values.item(i), true));
                		count++;
                	} // end if
                } // end for
                
                if( count == 0 ) {
                	throw new Exception("Property File has no property values");
                } // end if 
                
                componentProperty.setValue(newdoc);
                
/*
                // TUSCANY-2377, Add a fake value element so it's consistent with
                // the DOM tree loaded from inside SCDL
                if (!document.getDocumentElement().getLocalName().equals("value")){
                    Element root = document.createElementNS(null, "value");
                    root.appendChild(document.getDocumentElement());
                    
                    // remove all the child nodes as they will be replaced by the "value" node
                    NodeList children = document.getChildNodes();
                    for (int i=0; i < children.getLength(); i++){
                        document.removeChild(children.item(i));
                    }
                    
                    // add the value node back in
                    document.appendChild(root);
                }               
                componentProperty.setValue(document);               
            } finally {
                if (is != null) {
                    is.close();
                }  
            } // end try
*/                
        } catch (Exception ex) {
            Monitor.error(monitor,
                          this,
                          Messages.ASSEMBLY_VALIDATION,
                          "PropertyFileValueInvalid",
                          file,
                          componentProperty.getName(),
                          component.getName(),
                          ex);
        } // end try
    } // end method processPropertyFileAttribute
    
    private Document readPropertyFileData( String file ) throws Exception {
    	Document doc = null;
    	
    	URI uri = URI.create(file);
        // URI resolution for relative URIs is done when the composite is resolved.
        URL url = uri.toURL();
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        InputStream is = null;
        try {
	        is = connection.getInputStream();
	
	        Source streamSource = new SAXSource(new InputSource(is));
	        DOMResult result = new DOMResult();
	        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(streamSource, result);
	        
	        doc = (Document)result.getNode();
        } finally {
            if (is != null) {
                is.close();
            }
        } // end try

        return doc;
    } // end method readPropertyFileData

    /**
     * Evaluate an XPath expression against a Property value, returning the result as a Property value
     * @param node - the document root element of a Property value
     * @param expression - the XPath expression
     * @param documentBuilderFactory - a DOM document builder factory
     * @return - a DOM Document representing the result of the evaluation as a Property value
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     */
    private Document evaluateXPath(Document node,
                                   XPathExpression expression,
                                   DocumentBuilderFactory documentBuilderFactory) throws XPathExpressionException,
        ParserConfigurationException {

        // The document element is a <sca:property/> element
        Node property = node.getDocumentElement();
        // The first child of the <property/> element is a <value/> element
        Node value = property.getFirstChild();

        Node result = (Node)expression.evaluate(value, XPathConstants.NODE);
        if (result == null) {
            return null;
        }

        if (result instanceof Document) {
            return (Document)result;
        } else {
            Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
            Element newProperty = document.createElementNS(SCA11_NS, "property");

            if (result.getNodeType() == Node.ELEMENT_NODE) {
                // If the result is a <value/> element, use it directly in the result
                newProperty.appendChild(document.importNode(result, true));
            } else {
                // If the result is not a <value/> element, create a <value/> element to contain the result
                Element newValue = document.createElementNS(SCA11_NS, "value");
                newValue.appendChild(document.importNode(result, true));
                newProperty.appendChild(newValue);
            } // end if
            document.appendChild(newProperty);

            return document;
        }
    }
    
    /**
     * Evaluate an XPath expression against a Property value, returning the result as a Property value
     * - deals with multi-valued input property values and with multi-valued output property values
     * @param node - the document root element of a Property value
     * @param expression - the XPath expression
     * @param documentBuilderFactory - a DOM document builder factory
     * @return - a DOM Document representing the result of the evaluation as a Property value
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     */
    private Document evaluateXPath2(Document node,
                                   XPathExpression expression,
                                   DocumentBuilderFactory documentBuilderFactory) throws XPathExpressionException,
        ParserConfigurationException {

        // The document element is a <sca:property/> element
        Element property = node.getDocumentElement();

        NodeList result = (NodeList)expression.evaluate(property, XPathConstants.NODESET);
        if (result == null || result.getLength() == 0) return null;

        if (result instanceof Document) {
            return (Document)result;
        } else {
            Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
            Element newProperty = document.createElementNS(SCA11_NS, "property");
            
            for( int i = 0 ; i < result.getLength() ; i++ ) {
		        if (result.item(i).getNodeType() == Node.ELEMENT_NODE) {
		            // If the result is an element, use it directly in the result
		            newProperty.appendChild(document.importNode(result.item(i), true));
		        } else {
		            // If the result is not an element, create a <value/> element to contain the result
		            Element newValue = document.createElementNS(SCA11_NS, "value");
		            newValue.appendChild(document.importNode(result.item(i), true));
		            newProperty.appendChild(newValue);
		        } // end if
            } // end for
            
            document.appendChild(newProperty);

            return document;
        } // end if
    } // end method

    /**
     * Create a callback reference for a component service
     * 
     * @param component
     * @param service
     */
    private void createCallbackReference(Component component, ComponentService service, Monitor monitor) {

        // if the service has a callback interface create a reference
        // to represent the callback 
        if (service.getInterfaceContract() != null && // can be null in unit tests
            service.getInterfaceContract().getCallbackInterface() != null) {

            ComponentReference callbackReference = assemblyFactory.createComponentReference();
            callbackReference.setForCallback(true);
            callbackReference.setName(service.getName());
            // MJE: multiplicity = 0..n for these callback references
            callbackReference.setMultiplicity(Multiplicity.ZERO_N);
            try {
                InterfaceContract contract = (InterfaceContract)service.getInterfaceContract().clone();
                contract.setInterface(contract.getCallbackInterface());
                contract.setCallbackInterface(null);
                callbackReference.setInterfaceContract(contract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            Service implService = service.getService();
            if (implService != null) {

                // If the implementation service is a CompositeService, ensure that the Reference that is 
                // created is a CompositeReference, otherwise create a Reference
                Reference implReference;
                if (implService instanceof CompositeService) {
                    CompositeReference implCompReference = assemblyFactory.createCompositeReference();
                    // Set the promoted component from the promoted component of the composite service
                    implCompReference.getPromotedComponents().add(((CompositeService)implService)
                        .getPromotedComponent());
                    
                    // Get the promoted component reference corresponding to the service with the callback
                    // fist checking that the promoted service is resolved lest we get a NPE trying to
                    // retrieve the promoted component. It could be unresolved if the user gets the 
                    // promotes string wrong
                    // TODO - is there any danger that the callback reference name will clash with other
                    //        reference names. Old code used to qualify it with promoted component name
                    if (((CompositeService)implService).getPromotedService().isUnresolved() == false){
                        String referenceName = ((CompositeService)implService).getPromotedService().getName();
                        ComponentReference promotedReference = ((CompositeService)implService).getPromotedComponent().getReference(referenceName);
                        
                        if (promotedReference != null){
                            implCompReference.getPromotedReferences().add(promotedReference);
                        } else {
                            Monitor.error(monitor,
                                          this,
                                          Messages.ASSEMBLY_VALIDATION,
                                          "PromotedCallbackReferenceNotFound",
                                          component.getName(),
                                          service.getName(),
                                          ((CompositeService)implService).getPromotedComponent().getName(),
                                          referenceName);
                        }
                    }                 
                    implReference = implCompReference;
                    
                    // Add the composite reference to the composite implementation artifact
                    Implementation implementation = component.getImplementation();
                    if (implementation != null && implementation instanceof Composite) {
                        ((Composite)implementation).getReferences().add(implCompReference);
                    }
                } else {
                    implReference = assemblyFactory.createReference();
                }

                implReference.setName(implService.getName());
                // MJE: Fixup multiplicity as 0..n for callback references in the component type
                implReference.setMultiplicity(Multiplicity.ZERO_N);
                try {
                    InterfaceContract implContract = (InterfaceContract)implService.getInterfaceContract().clone();
                    implContract.setInterface(implContract.getCallbackInterface());
                    implContract.setCallbackInterface(null);
                    implReference.setInterfaceContract(implContract);
                } catch (CloneNotSupportedException e) {
                    // will not happen
                }
                callbackReference.setReference(implReference);
            }
            component.getReferences().add(callbackReference);

            // Set the bindings of the callback reference 
            if (callbackReference.getBindings().isEmpty()) {
                // If there are specific callback bindings set in the SCDL service
                // callback element then use them
                // at runtime a callback binding will be selected based on the forward call
                if (service.getCallback() != null && service.getCallback().getBindings().size() > 0) {
                    callbackReference.getBindings().addAll(service.getCallback().getBindings());
                } else {
                    // otherwise take a copy of all the bindings on the forward service
                    // at runtime a callback binding will be selected based on the forward call
                    List<Binding> serviceBindings = service.getBindings();
                    for ( Binding serviceBinding: serviceBindings ) {
                    	try {
							Binding referenceBinding = (Binding)serviceBinding.clone();
							referenceBinding.setURI(null);
							callbackReference.getBindings().add(referenceBinding);
						} catch (CloneNotSupportedException e) {
							// will not happen
						} // end try
                    } // end for
                    
                    // if there are still no bindings for the callback create a default binding which 
                    // will cause the EPR for this reference to be marked as EndpointReference.NOT_CONFIGURED
                    if( serviceBindings.size() == 0 ) {
                    	createSCABinding(callbackReference, null);
                    } // end if
                }
            }
            service.setCallbackReference(callbackReference);
        }
    }

    /**
     * Create a callback service for a component reference
     * 
     * @param component
     * @param service
     */
    private void createCallbackService(Component component, ComponentReference reference, Monitor monitor) {
        if (reference.getInterfaceContract() != null && // can be null in unit tests
            reference.getInterfaceContract().getCallbackInterface() != null) {
            ComponentService callbackService = assemblyFactory.createComponentService();
            callbackService.setForCallback(true);
            callbackService.setName(reference.getName());
            try {
                InterfaceContract contract = (InterfaceContract)reference.getInterfaceContract().clone();
                contract.setInterface(contract.getCallbackInterface());
                contract.setCallbackInterface(null);
                callbackService.setInterfaceContract(contract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            Reference implReference = reference.getReference();
            if (implReference != null) {
                // If the implementation reference is a CompositeReference, ensure that the Service that is 
                // created is a CompositeService, otherwise create a Service
                Service implService;
                if (implReference instanceof CompositeReference) {
                    CompositeService implCompService = assemblyFactory.createCompositeService();
                    // TODO The reality here is that the composite reference which has the callback COULD promote more than
                    // one component reference - and there must be a separate composite callback service for each of these component
                    // references
                    
                    // Set the promoted component from the promoted component of the composite reference
                    implCompService.setPromotedComponent(((CompositeReference)implReference).getPromotedComponents().get(0));
                    implCompService.setForCallback(true);
                    
                    // Get the promoted component service corresponding to the reference with the callback
                    // fist checking that the promoted reference is resolved lest we get a NPE trying to
                    // retrieve the promoted component. It could be unresolved if the user gets the 
                    // promotes string wrong
                    if (((CompositeReference)implReference).getPromotedReferences().get(0).isUnresolved() == false){
                        String serviceName = ((CompositeReference)implReference).getPromotedReferences().get(0).getName();
                        ComponentService promotedService = ((CompositeReference)implReference).getPromotedComponents().get(0).getService(serviceName);
                        
                        if (promotedService != null){
                            implCompService.setPromotedService(promotedService);
                        } else {
                            Monitor.error(monitor,
                                          this,
                                          Messages.ASSEMBLY_VALIDATION,
                                          "PromotedCallbackServiceNotFound",
                                          component.getName(),
                                          reference.getName(),
                                          ((CompositeReference)implReference).getPromotedComponents().get(0).getName(),
                                          serviceName);
                        }
                    }
                    
                    implService = implCompService;
                    // Add the composite service to the composite implementation artifact
                    Implementation implementation = component.getImplementation();
                    if (implementation != null && implementation instanceof Composite) {
                        ((Composite)implementation).getServices().add(implCompService);
                    } // end if
                    //
                } else {
                    implService = assemblyFactory.createService();
                } // end if
                //
                implService.setName(implReference.getName());
                try {
                    InterfaceContract implContract = (InterfaceContract)implReference.getInterfaceContract().clone();
                    implContract.setInterface(implContract.getCallbackInterface());
                    implContract.setCallbackInterface(null);
                    implService.setInterfaceContract(implContract);
                } catch (CloneNotSupportedException e) {
                    // will not happen
                }
                callbackService.setService(implService);
            }
            component.getServices().add(callbackService);

            // configure bindings for the callback service
            if (callbackService.getBindings().isEmpty()) {
                if (reference.getCallback() != null && reference.getCallback().getBindings().size() > 0) {
                    // set bindings of the callback service based on the information provided in 
                    // SCDL reference callback element
                    callbackService.getBindings().addAll(reference.getCallback().getBindings());
                } else if (reference.getBindings().size() > 0) {
                    // use any bindings explicitly declared on the forward reference
                    for (Binding binding : reference.getBindings()) {
                        try {
                            Binding clonedBinding = (Binding)binding.clone();
                            // binding uri will be calculated during runtime build
                            clonedBinding.setURI(null);
                            callbackService.getBindings().add(clonedBinding);
                        } catch (CloneNotSupportedException ex) {

                        }
                    }
                } else {
                    // create a default binding which will have the correct policy 
                    // and URI added. We check later to see if a new binding is required
                    // based on the forward binding but can then copy policy and URI
                    // details from here. 
                    // TODO - there is a hole here. If the user explicitly specified an
                    //        SCA callback binding that is different from the forward 
                    //        binding type then we're in trouble
                    createSCABinding(callbackService, null);
                }
            }

            reference.setCallbackService(callbackService);
        }
    }

    /**
     * Create a default SCA binding in the case that no binding
     * is specified by the user
     * 
     * @param contract
     * @param definitions
     */
    protected void createSCABinding(Contract contract, Definitions definitions) {

        SCABinding scaBinding = scaBindingFactory.createSCABinding();
        scaBinding.setName(contract.getName());

        if (definitions != null) {
            for (ExtensionType attachPointType : definitions.getBindingTypes()) {
                if (attachPointType.getType().equals(BINDING_SCA_QNAME)) {
                    ((PolicySubject)scaBinding).setExtensionType(attachPointType);
                }
            }
        }

        contract.getBindings().add(scaBinding);
        contract.setOverridingBindings(false);
    }

    /**
     * Look to see if any value elements have been set into the property
     * A bit involved as the value is stored as a DOM Document
     * 
     * @param property the property to be tested
     * @return true is values are present
     */
    private boolean isPropertyValueSet(Property property) {
        Document value = (Document)property.getValue();

        if (value == null) {
            return false;
        }

        if (value.getDocumentElement() == null) {
            return false;
        }

        if (value.getDocumentElement().getChildNodes().getLength() == 0) {
            return false;
        }

        return true;
    }
    
    /**
     * Look to see is a property has more than one value
     * 
     * @param property
     * @return true is the property has more than one value
     */
    private boolean isPropertyManyValued(Property property) {
        
        if (isPropertyValueSet(property)){
            Document value = (Document)property.getValue();
            if (value.getDocumentElement().getChildNodes().getLength() > 1){
                return true;
            }
        }
        return false;
    }

    private boolean isValidMultiplicityOverride(Multiplicity definedMul, Multiplicity overridenMul) {
        if (definedMul != overridenMul) {
            switch (definedMul) {
                case ZERO_N:
                    return overridenMul == Multiplicity.ZERO_ONE || overridenMul == Multiplicity.ONE_ONE
                        || overridenMul == Multiplicity.ONE_N;
                case ONE_N:
                    return overridenMul == Multiplicity.ONE_ONE;
                case ZERO_ONE:
                    return overridenMul == Multiplicity.ONE_ONE;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }


    /**
     * Interface contract from higher in the implementation hierarchy takes precedence
     * When it comes to checking compatibility the top level service interface is a 
     * subset of the promoted service interface so treat the top level interface as
     * the source
     * 
     * @param topContract the top contract 
     * @param bottomContract the bottom contract
     */
    private void calculateServiceInterfaceContract(Component component, Service topContract, Service bottomContract, Monitor monitor) {

        // Use the interface contract from the bottom level contract if
        // none is specified on the top level contract
        InterfaceContract topInterfaceContract = topContract.getInterfaceContract();
        InterfaceContract bottomInterfaceContract = bottomContract.getInterfaceContract();

        if (topInterfaceContract == null) {
            topContract.setInterfaceContract(bottomInterfaceContract);
        } else if (bottomInterfaceContract != null) {
            // Check that the top and bottom interface contracts are compatible
            boolean isCompatible = true;
            String incompatibilityReason = "";
            try{
                isCompatible = checkSubsetCompatibility(topInterfaceContract, 
                                                        bottomInterfaceContract);
            } catch (IncompatibleInterfaceContractException ex){
                isCompatible = false;
                incompatibilityReason = ex.getMessage();
            }            
            if (!isCompatible) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ServiceIncompatibleComponentInterface",
                              component.getName(),
                              topContract.getName(),
                              incompatibilityReason);
            }
            
            // TODO - there is an issue with the following code if the 
            //        contracts are of different types. Need to use the 
            //        normalized form
            
            // fix up the forward interface based on the promoted component
            // Someone might have manually specified a callback interface but
            // left out the forward interface
            if (topInterfaceContract.getInterface() == null){
                topInterfaceContract.setInterface(bottomInterfaceContract.getInterface());
            }              
            
            // fix up the callback interface based on the promoted component
            // Someone might have manually specified a forward interface but
            // left out the callback interface
            if (topInterfaceContract.getCallbackInterface() == null){
                topInterfaceContract.setCallbackInterface(bottomInterfaceContract.getCallbackInterface());
            }            
        }
    }
    
    /**
     * Interface contract from higher in the implementation hierarchy takes precedence
     * When it comes to checking compatibility the top level reference interface is a 
     * superset of the promoted reference interface so treat the promoted
     * (bottom) interface as the source    
     * 
     * @param topContract the top contract 
     * @param bottomContract the bottom contract
     */
    private void calculateReferenceInterfaceContract(Component component, Reference topContract, Reference bottomContract, Monitor monitor) {

        // Use the interface contract from the bottom level contract if
        // none is specified on the top level contract
        InterfaceContract topInterfaceContract = topContract.getInterfaceContract();
        InterfaceContract bottomInterfaceContract = bottomContract.getInterfaceContract();

        if (topInterfaceContract == null) {
            topContract.setInterfaceContract(bottomInterfaceContract);
        } else if (bottomInterfaceContract != null) {
            // Check that the top and bottom interface contracts are compatible
            boolean isCompatible = true;
            String incompatibilityReason = "";
            try{
                isCompatible = checkSubsetCompatibility(bottomInterfaceContract, 
                                                        topInterfaceContract);
            } catch (IncompatibleInterfaceContractException ex){
                isCompatible = false;
                incompatibilityReason = ex.getMessage();
            }            
            if (!isCompatible) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceIncompatibleComponentInterface",
                              component.getName(),
                              topContract.getName(),
                              incompatibilityReason);
            }
            
            // TODO - there is an issue with the following code if the 
            //        contracts of of different types. Need to use the 
            //        normalized form
            
            // fix up the forward interface based on the promoted component
            // Someone might have manually specified a callback interface but
            // left out the forward interface
            if (topInterfaceContract.getInterface() == null){
                topInterfaceContract.setInterface(bottomInterfaceContract.getInterface());
            }            
            
            // fix up the callback interface based on the promoted component
            // Someone might have manually specified a forward interface but
            // left out the callback interface
            if (topInterfaceContract.getCallbackInterface() == null){
                topInterfaceContract.setCallbackInterface(bottomInterfaceContract.getCallbackInterface());
            }            
        }
    }    

    /**
     * Bindings from higher in the hierarchy take precedence
     * 
     * @param componentService the top service 
     * @param componentTypeService the bottom service
     */
    private void calculateBindings(Component component, Service componentService, Service componentTypeService, BuilderContext context) {
    	Monitor monitor = context.getMonitor();
    	
        // forward bindings
        if (componentService.getBindings().isEmpty()) {
            componentService.getBindings().addAll(componentTypeService.getBindings());
        }

        if (componentService.getBindings().isEmpty()) {
            createSCABinding(componentService, context.getDefinitions());
        }

        // callback bindings
        if (componentService.getCallback() == null) {
            componentService.setCallback(componentTypeService.getCallback());
            if (componentService.getCallback() == null) {
                // Create an empty callback to avoid null check
                componentService.setCallback(assemblyFactory.createCallback());
            }
        } else if (componentService.getCallback().getBindings().isEmpty() && componentTypeService.getCallback() != null) {
            componentService.getCallback().getBindings().addAll(componentTypeService.getCallback().getBindings());
        }
        
        // [ASM90005] validate that binding.sca has no uri set
        for (Binding binding : componentService.getBindings()){
            if (binding instanceof SCABinding){
                if ((binding.getURI() != null) &&
                    (binding.getURI().length() > 0)){
                    Monitor.error(monitor,
                            this,
                            Messages.ASSEMBLY_VALIDATION,
                            "URIFoundOnServiceSCABinding",
                            binding.getName(),
                            component.getName(),
                            componentService.getName(),
                            binding.getURI());
                } 
            }
        }

    }
    
    /**
     * Bindings from higher in the hierarchy take precedence
     * 
     * @param componentReference the top service 
     * @param componentTypeReference the bottom service
     */
    private void calculateBindings(Reference componentReference, Reference componentTypeReference) {
        // forward bindings
        if (componentReference.getBindings().isEmpty()) {
            componentReference.getBindings().addAll(componentTypeReference.getBindings());
        }

        // callback bindings
        if (componentReference.getCallback() == null) {
            componentReference.setCallback(componentTypeReference.getCallback());
        } else if (componentReference.getCallback().getBindings().isEmpty() && componentTypeReference.getCallback() != null) {
            componentReference.getCallback().getBindings().addAll(componentTypeReference.getCallback().getBindings());
        }
    }  
    
    /**
     * A local wrapper for the interface contract mapper as we need to normalize the 
     * interface contracts if appropriate and the mapper doesn't have the right
     * dependencies to be able to do it. 
     * 
     * Sometimes the two interfaces can be presented using different IDLs, for example
     * Java and WSDL. In this case interfaces are converted so that they are both WSDL1.1 interfaces
     * and they are then compared. The generated WSDL is cached on the interface object for 
     * any subsequent matching
     * 
     * @param contractA
     * @param contractB
     * @return true if the interface contracts match
     */
    private boolean checkSubsetCompatibility(InterfaceContract contractA, InterfaceContract contractB)
        throws IncompatibleInterfaceContractException {
        
        if (contractA.getClass() != contractB.getClass()) {
                      
            if (contractA instanceof JavaInterfaceContract){
                contractBuilder.build(contractA, null);
                contractA = ((JavaInterfaceContract)contractA).getNormalizedWSDLContract();
            } 
            
            if (contractB instanceof JavaInterfaceContract){
                contractBuilder.build(contractB, null);
                contractB = ((JavaInterfaceContract)contractB).getNormalizedWSDLContract();
            }            
        }   
        
        return interfaceContractMapper.checkCompatibility(contractA, 
                                                          contractB, 
                                                          Compatibility.SUBSET, 
                                                          false, 
                                                          false);
    }

}
