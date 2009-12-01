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

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
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
import org.apache.tuscany.sca.assembly.builder.ImplementationBuilder;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @version $Rev$ $Date$
 */
public class ComponentBuilderImpl {
    protected static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";
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
            
            // Inherit the intents and policySets from the componentType
            // NOTE: configureServices/configureReferences may add callback references and services
            for(ComponentReference componentReference: component.getReferences()) {
                policyBuilder.configure(componentReference, context);
            }
            for(ComponentService componentService: component.getServices()) {
                policyBuilder.configure(componentService, context);
            }
            
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
            calculateBindings(componentService, componentTypeService);

            // add callback reference model objects
            createCallbackReference(component, componentService);
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
            createCallbackService(component, componentReference);

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
                              componentProperty.getXSDElement().toString(),
                              componentTypeProperty.getXSDElement().toString());                
            }
            
            if ( componentProperty.getXSDType() != null &&
                    !componentProperty.getXSDType().equals(componentTypeProperty.getXSDType())){
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION, 
                              "PropertXSDTypesDontMatch", 
                              component.getName(), 
                              componentProperty.getName(),
                              componentProperty.getXSDType().toString(),
                              componentTypeProperty.getXSDType().toString());                
            }            
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

        if (source != null) {
            // $<name>/...
            int index = source.indexOf('/');
            if (index == -1) {
                // Tolerating $prop
                source = source + "/";
                index = source.length() - 1;
            }
            if (source.charAt(0) == '$') {
                String name = source.substring(1, index);
                Property sourceProp = null;
                if (outerComponent != null) {
                    sourceProp = outerComponent.getProperty(name);
                } else {
                    sourceProp = parentComposite.getProperty(name);
                }
                if (sourceProp == null) {
                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "PropertySourceNotFound",
                                  source,
                                  componentProperty.getName(),
                                  component.getName());
                }

                Document sourcePropValue = (Document)sourceProp.getValue();

                try {
                    // FIXME: How to deal with namespaces?
                    Document node =
                        evaluateXPath(sourcePropValue,
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
                                        componentProperty.getName());
                    }
                } catch (Exception ex) {
                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "PropertySourceXpathInvalid",
                                  source,
                                  componentProperty.getName(),
                                  component.getName(),
                                  ex);
                }
            } else {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "PropertySourceValueInvalid",
                              source,
                              componentProperty.getName(),
                              component.getName());
            }
        }
    }

    /**
     * If the property has a file attribute use this to retrieve the value from a 
     * local file

     * 
     * @param parentCompoent the composite that contains the component
     * @param component
     */
    private void processPropertyFileAttribute(Component component, ComponentProperty componentProperty, Monitor monitor) {
        String file = componentProperty.getFile();
        if (file != null) {
            try {
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

                    // TUSCANY-2377, Add a fake value element so it's consistent with
                    // the DOM tree loaded from inside SCDL
                    Element root = document.createElementNS(null, "value");
                    root.appendChild(document.getDocumentElement());
                    document.appendChild(root);
                    componentProperty.setValue(document);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            } catch (Exception ex) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "PropertyFileValueInvalid",
                              file,
                              componentProperty.getName(),
                              component.getName(),
                              ex);
            }
        }

    }

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
     * Create a callback reference for a component service
     * 
     * @param component
     * @param service
     */
    private void createCallbackReference(Component component, ComponentService service) {

        // if the service has a callback interface create a reference
        // to represent the callback 
        if (service.getInterfaceContract() != null && // can be null in unit tests
        service.getInterfaceContract().getCallbackInterface() != null) {

            ComponentReference callbackReference = assemblyFactory.createComponentReference();
            callbackReference.setForCallback(true);
            callbackReference.setName(service.getName());
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
                    // Set the promoted service
                    ComponentReference promotedReference = assemblyFactory.createComponentReference();
                    String promotedRefName =
                        ((CompositeService)implService).getPromotedComponent().getName() + "/"
                            + ((CompositeService)implService).getPromotedService().getName();
                    promotedReference.setName(promotedRefName);
                    promotedReference.setUnresolved(true);
                    implCompReference.getPromotedReferences().add(promotedReference);
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
                if (service.getCallback() != null && service.getCallback().getBindings().size() > 0) {
                    callbackReference.getBindings().addAll(service.getCallback().getBindings());
                } else {
                    // otherwise create a default binding which 
                    // will cause the EPR for this reference to be 
                    // marked as EndpointReference.NOT_CONFIGURED
                    createSCABinding(callbackReference, null);

                    // TODO - should really use the forward binding here but 
                    //        awaiting OASIS decision on what's going to 
                    //        happen with callbacks
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
    private void createCallbackService(Component component, ComponentReference reference) {
        if (reference.getInterfaceContract() != null && // can be null in unit tests
        reference.getInterfaceContract().getCallbackInterface() != null) {
            ComponentService componentService = assemblyFactory.createComponentService();
            componentService.setForCallback(true);
            componentService.setName(reference.getName());
            try {
                InterfaceContract contract = (InterfaceContract)reference.getInterfaceContract().clone();
                contract.setInterface(contract.getCallbackInterface());
                contract.setCallbackInterface(null);
                componentService.setInterfaceContract(contract);
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
                    implCompService.setPromotedComponent(((CompositeReference)implReference).getPromotedComponents()
                        .get(0));
                    implCompService.setForCallback(true);
                    // Set the promoted service
                    ComponentService promotedService = assemblyFactory.createComponentService();
                    promotedService.setName(((CompositeReference)implReference).getPromotedReferences().get(0)
                        .getName());
                    promotedService.setUnresolved(true);
                    promotedService.setForCallback(true);
                    implCompService.setPromotedService(promotedService);
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
                componentService.setService(implService);
            }
            component.getServices().add(componentService);

            // configure bindings for the callback service
            if (componentService.getBindings().isEmpty()) {
                if (reference.getCallback() != null && reference.getCallback().getBindings().size() > 0) {
                    // set bindings of the callback service based on the information provided in 
                    // SCDL reference callback element
                    componentService.getBindings().addAll(reference.getCallback().getBindings());
                } else if (reference.getBindings().size() > 0) {
                    // use any bindings explicitly declared on the forward reference
                    for (Binding binding : reference.getBindings()) {
                        try {
                            Binding clonedBinding = (Binding)binding.clone();
                            // binding uri will be calculated during runtime build
                            clonedBinding.setURI(null);
                            componentService.getBindings().add(clonedBinding);
                        } catch (CloneNotSupportedException ex) {

                        }
                    }
                } else {
                    // TODO - should use the binding resolved from the service but
                    //        waiting for OASIS to decide what to do about callbacks
                    // create a default binding
                    createSCABinding(componentService, null);
                }
            }

            reference.setCallbackService(componentService);
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

        if (value.getFirstChild() == null) {
            return false;
        }

        if (value.getFirstChild().getChildNodes().getLength() == 0) {
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
            if (value.getFirstChild().getChildNodes().getLength() > 1){
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
                isCompatible = interfaceContractMapper.checkCompatibility(topInterfaceContract, bottomInterfaceContract, false, false);
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
        }
    }
    
    /**
     * Interface contract from higher in the implementation hierarchy takes precedence
     * When it comes to checking compatibility the top level reference interface is a 
     * superset of the promoted reference interface so treat the treat the promoted
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
                isCompatible = interfaceContractMapper.checkCompatibility(bottomInterfaceContract, topInterfaceContract, false, false);
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
        }
    }    

    /**
     * Bindings from higher in the hierarchy take precedence
     * 
     * @param componentService the top service 
     * @param componentTypeService the bottom service
     */
    private void calculateBindings(Service componentService, Service componentTypeService) {
        // forward bindings
        if (componentService.getBindings().isEmpty()) {
            componentService.getBindings().addAll(componentTypeService.getBindings());
        }

        if (componentService.getBindings().isEmpty()) {
            createSCABinding(componentService, null);
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

}
