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
package org.apache.tuscany.sca.implementation.spring.introspect;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.PolicySubjectProcessor;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringConstructorArgElement;
import org.apache.tuscany.sca.implementation.spring.SpringImplementation;
import org.apache.tuscany.sca.implementation.spring.SpringImplementationConstants;
import org.apache.tuscany.sca.implementation.spring.SpringPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Introspects a Spring XML application-context configuration file to create <implementation-spring../>
 * component type information.
 *
 * @version $Rev$ $Date$
 */
public class SpringXMLComponentTypeLoader {

    private XMLInputFactory xmlInputFactory;
    private ContributionFactory contributionFactory;
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private PolicyFactory policyFactory;
    private PolicySubjectProcessor policyProcessor;
    private Monitor monitor;
    private SpringBeanIntrospector beanIntrospector;

    public SpringXMLComponentTypeLoader(FactoryExtensionPoint factories,
                                        AssemblyFactory assemblyFactory,
                                        JavaInterfaceFactory javaFactory,
                                        PolicyFactory policyFactory,
                                        Monitor monitor) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.javaFactory = javaFactory;
        this.policyFactory = policyFactory;
        this.policyProcessor = new PolicySubjectProcessor(policyFactory);
        this.contributionFactory = factories.getFactory(ContributionFactory.class);
        this.xmlInputFactory = factories.getFactory(XMLInputFactory.class);
        this.monitor = monitor;
    }
    
    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
    	 if (monitor != null) {
	        Problem problem = monitor.createProblem(this.getClass().getName(), "impl-spring-validation-messages", Severity.ERROR, model, message, ex);
	        monitor.problem(problem);
    	 }
    }

    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
	        Problem problem = monitor.createProblem(this.getClass().getName(), "impl-spring-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	 }
    }

    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    /**
     * Base method which loads the component type from the application-context attached to the
     * Spring implementation
     *
     */
    public void load(SpringImplementation implementation, ModelResolver resolver, ProcessorContext context) throws ContributionReadException {
        //System.out.println("Spring TypeLoader - load method start");
    	ComponentType componentType = implementation.getComponentType();
        /* Check that there is a component type object already set	*/
        if (componentType == null) {
            throw new ContributionReadException("SpringXMLLoader load: implementation has no ComponentType object");
        }
        if (componentType.isUnresolved()) {
            /* Fetch the location of the application-context file from the implementation */
            loadFromXML(implementation, resolver, context);
            if (!componentType.isUnresolved())
                implementation.setUnresolved(false);
        } // end if
        //System.out.println("Spring TypeLoader - load method complete");
    } // end method load

    private Class<?> resolveClass(ModelResolver resolver, String className, ProcessorContext context) throws ClassNotFoundException {
        ClassReference classReference = new ClassReference(className);
        classReference = resolver.resolveModel(ClassReference.class, classReference, context);
        if (classReference.isUnresolved()) {
            throw new ClassNotFoundException(className);
        }
        Class<?> javaClass = classReference.getJavaClass();
        return javaClass;
    }

    /**
     * Method which fills out the component type for a Spring implementation by reading the
     * Spring application-context.xml file.
     *
     * @param implementation SpringImplementation into which to load the component type information
     * @throws ContributionReadException Failed to read the contribution
     */
    private void loadFromXML(SpringImplementation implementation, ModelResolver resolver, ProcessorContext context) throws ContributionReadException {
        XMLStreamReader reader;
        List<SpringBeanElement> beans = new ArrayList<SpringBeanElement>();
        List<SpringSCAServiceElement> services = new ArrayList<SpringSCAServiceElement>();
        List<SpringSCAReferenceElement> references = new ArrayList<SpringSCAReferenceElement>();
        List<SpringSCAPropertyElement> scaproperties = new ArrayList<SpringSCAPropertyElement>();

        URL resource;
        List<URL> contextResources = new ArrayList<URL>();
        String contextPath = implementation.getLocation();

        try {        	
            resource = resolveLocation(resolver, contextPath, context);
            contextResources = getApplicationContextResource(resource);
            
            implementation.setClassLoader(new ContextClassLoader(resolver, context));
            implementation.setResource(contextResources);
            // The URI is used to uniquely identify the Implementation
            implementation.setURI(resource.toString());
            
            for (URL contextResource : contextResources) {            	
            	List<SpringBeanElement> appCxtBeans = new ArrayList<SpringBeanElement>();
                List<SpringSCAServiceElement> appCxtServices = new ArrayList<SpringSCAServiceElement>();
                List<SpringSCAReferenceElement> appCxtReferences = new ArrayList<SpringSCAReferenceElement>();
                List<SpringSCAPropertyElement> appCxtProperties = new ArrayList<SpringSCAPropertyElement>();
            	reader = xmlInputFactory.createXMLStreamReader(contextResource.openStream());
            	// Read the beans, services, references and properties for individual application context
            	readContextDefinition(resolver, reader, contextPath, appCxtBeans, appCxtServices, appCxtReferences, appCxtProperties, context);
            	// Validate the beans from individual application context for uniqueness
            	validateBeans(appCxtBeans, appCxtServices, appCxtReferences, appCxtProperties);
            	// Add all the validated beans to the generic list
            	beans.addAll(appCxtBeans);
            	services.addAll(appCxtServices);
            	references.addAll(appCxtReferences);
            	scaproperties.addAll(appCxtProperties);
            	reader.close();
            }
        } catch (IOException e) {
            throw new ContributionReadException(e);
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }

        /* At this point, the complete application-context.xml file has been read and its contents  */
        /* stored in the lists of beans, services, references.  These are now used to generate      */
        /* the implied componentType for the application context								    */
        generateComponentType(implementation, resolver, beans, services, references, scaproperties, context);

        return;
    } // end method loadFromXML

    private URL resolveLocation(ModelResolver resolver, String contextPath, ProcessorContext context) throws MalformedURLException,
        ContributionReadException {
        URL resource = null;
        URI uri = URI.create(contextPath);
        if (!uri.isAbsolute()) {
            Artifact artifact = contributionFactory.createArtifact();
            artifact.setUnresolved(true);
            artifact.setURI(contextPath);
            artifact = resolver.resolveModel(Artifact.class, artifact, context);
            if (!artifact.isUnresolved()) {
                resource = new URL(artifact.getLocation());
            } else {
                throw new ContributionReadException("Location cannot be resloved: " + contextPath);
            }
        } else {
            resource = new URL(contextPath);
        }
        return resource;
    }

    /**
     * Method which returns the XMLStreamReader for the Spring application-context.xml file
     * specified in the location attribute
     */
    private XMLStreamReader getApplicationContextReader(ModelResolver resolver, String location, ProcessorContext context) throws ContributionReadException {

        try {
            URL resource = getApplicationContextResource(resolveLocation(resolver, location, context)).get(0);
            XMLStreamReader reader =
            	xmlInputFactory.createXMLStreamReader(resource.openStream());
            return reader;
        } catch (IOException e) {
            throw new ContributionReadException(e);
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    /**
     * Method which reads the spring context definitions from Spring application-context.xml
     * file and identifies the defined beans, properties, services and references
     * @param context 
     */
    private void readContextDefinition(ModelResolver resolver,
                                       XMLStreamReader reader,
                                       String contextPath,
                                       List<SpringBeanElement> beans,
                                       List<SpringSCAServiceElement> services,
                                       List<SpringSCAReferenceElement> references,
                                       List<SpringSCAPropertyElement> scaproperties, ProcessorContext context) throws ContributionReadException {

        SpringBeanElement bean = null;

        try {
            boolean completed = false;
            while (!completed) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        //System.out.println("Spring TypeLoader - found element with name: " + qname.toString());
                        if (SpringImplementationConstants.IMPORT_ELEMENT.equals(qname)) {
                        	//FIXME - put the sequence of code below which gets the ireader into a subsidiary method
                            String location = reader.getAttributeValue(null, "resource");
                            if (location != null) {
                            	// FIXME - need to find a right way of generating this path
                                String resourcePath = contextPath.substring(0, contextPath.lastIndexOf("/")+1) + location;
                                XMLStreamReader ireader = getApplicationContextReader(resolver, resourcePath, context);
                                // Read the context definition for the identified imported resource
                                readContextDefinition(resolver, ireader, contextPath, beans, services, references, scaproperties, context);
                            }
                        } else if (SpringImplementationConstants.SCA_SERVICE_ELEMENT.equals(qname)) {
                        	// The value of the @name attribute of an <sca:service/> subelement of a <beans/> 
                        	// element MUST be unique amongst the <sca:service/> subelements of the <beans/> element.
                        	if (!services.isEmpty() && (services.contains(reader.getAttributeValue(null, "name"))))
                            		error("ScaServiceNameNotUnique", resolver);
                        	
                        	SpringSCAServiceElement service =
                                new SpringSCAServiceElement(reader.getAttributeValue(null, "name"), 
                                							reader.getAttributeValue(null, "target"));
                            if (reader.getAttributeValue(null, "type") != null)
                            	service.setType(reader.getAttributeValue(null, "type"));
                            policyProcessor.readPolicies(service, reader);
                            services.add(service);
                        } else if (SpringImplementationConstants.SCA_REFERENCE_ELEMENT.equals(qname)) {
                        	// The value of the @name attribute of an <sca:reference/> subelement of a <beans/> 
                        	// element MUST be unique amongst the @name attributes of the <sca:reference/> subelements, 
                        	// of the <beans/> element.
                        	if (!references.isEmpty() && (references.contains(reader.getAttributeValue(null, "name"))))
                            		error("ScaReferenceNameNotUnique", resolver);
                        	
                        	SpringSCAReferenceElement reference =
                                new SpringSCAReferenceElement(reader.getAttributeValue(null, "name"), 
                                							  reader.getAttributeValue(null, "type"));
                            if (reader.getAttributeValue(null, "default") != null)
                            	reference.setDefaultBean(reader.getAttributeValue(null, "default"));
                            policyProcessor.readPolicies(reference, reader);
                            references.add(reference);                            
                        } else if (SpringImplementationConstants.SCA_PROPERTY_ELEMENT.equals(qname)) {
                        	// The value of the @name attribute of an <sca:property/> subelement of a <beans/> 
                        	// element MUST be unique amongst the @name attributes of the <sca:property/> subelements, 
                        	// of the <beans/> element.
                        	if (!scaproperties.isEmpty() && (scaproperties.contains(reader.getAttributeValue(null, "name"))))
                            		error("ScaPropertyNameNotUnique", resolver);
                        	
                        	SpringSCAPropertyElement scaproperty =
                                new SpringSCAPropertyElement(reader.getAttributeValue(null, "name"), reader
                                    .getAttributeValue(null, "type"));
                            scaproperties.add(scaproperty);
                        } else if (SpringImplementationConstants.BEAN_ELEMENT.equals(qname)) {
                            bean = new SpringBeanElement(reader.getAttributeValue(null, "id"), 
                            							 reader.getAttributeValue(null, "class"));
                            if (reader.getAttributeValue(null, "abstract") != null)
                            	if (reader.getAttributeValue(null, "abstract").equals("true"))
                            		bean.setAbstractBean(true);
                            if (reader.getAttributeValue(null, "parent") != null)
                            	if (!reader.getAttributeValue(null, "parent").equals(""))
                            		bean.setParentAttribute(true);                           	
                            if (reader.getAttributeValue(null, "factory-bean") != null)
                            	if (!reader.getAttributeValue(null, "factory-bean").equals(""))
                            		bean.setFactoryBeanAttribute(true);                            	
                            if (reader.getAttributeValue(null, "factory-method") != null)
                            	if (!reader.getAttributeValue(null, "factory-method").equals(""))
                            		bean.setFactoryMethodAttribute(true);                           	
                            // Set the first name as bean name, when the @id attribute is absent.
                            if (reader.getAttributeValue(null, "id") == null) {
                            	if (reader.getAttributeValue(null, "name") != null) {
                            		String[] names = (reader.getAttributeValue(null, "name")).split(",");
                            		bean.setId(names[0]);
                            	}
                            }
                            beans.add(bean);
                            // Read the <bean> element and its child elements
                            readBeanDefinition(reader, bean, beans);
                        } // end if
                        break;
                    case END_ELEMENT:
                        if (SpringImplementationConstants.BEANS_ELEMENT.equals(reader.getName())) {
                            //System.out.println("Spring TypeLoader - finished read of context file");
                            completed = true;
                            break;
                        } // end if
                } // end switch
            } // end while
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }


    /**
     * Method which reads the bean definitions from Spring application-context.xml file and identifies
     * the defined beans, properties, services and references
     */
    private void readBeanDefinition(XMLStreamReader reader,
                                    SpringBeanElement bean,
                                    List<SpringBeanElement> beans) throws ContributionReadException {

    	SpringBeanElement innerbean = null;
        SpringPropertyElement property = null;
        SpringConstructorArgElement constructorArg = null;
        
        try {
            boolean completed = false;            
            while (!completed) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (SpringImplementationConstants.BEAN_ELEMENT.equals(qname)) {
                        	innerbean = new SpringBeanElement(reader.getAttributeValue(null, "id"), reader
                                    .getAttributeValue(null, "class"));
                        	// Set the first name as bean name, when the @id attribute is absent.
                            if (reader.getAttributeValue(null, "id") == null) {
                            	if (reader.getAttributeValue(null, "name") != null) {
                            		String[] names = (reader.getAttributeValue(null, "name")).split(",");
                            		innerbean.setId(names[0]);
                            	}
                            }
                            innerbean.setInnerBean(true);
                            beans.add(innerbean);
                            readBeanDefinition(reader, innerbean, beans);
                        } else if (SpringImplementationConstants.PROPERTY_ELEMENT.equals(qname)) {
                            property = new SpringPropertyElement(reader.getAttributeValue(null, "name"));
                            if (reader.getAttributeValue(null, "ref") != null)
                            	property.addRef(reader.getAttributeValue(null, "ref"));
                            bean.addProperty(property);
                        } else if (SpringImplementationConstants.CONSTRUCTORARG_ELEMENT.equals(qname)) {
                            constructorArg = new SpringConstructorArgElement(reader.getAttributeValue(null, "type"));
                            if (reader.getAttributeValue(null, "ref") != null)
                            	constructorArg.addRef(reader.getAttributeValue(null, "ref"));
                            if (reader.getAttributeValue(null, "index") != null)
                            	constructorArg.setIndex((new Integer(reader.getAttributeValue(null, "index"))).intValue());
                            if (reader.getAttributeValue(null, "value") != null)
                            	constructorArg.addValue(reader.getAttributeValue(null, "value"));
                            bean.addCustructorArgs(constructorArg);
                        } else if (SpringImplementationConstants.REF_ELEMENT.equals(qname)) {
                        	String ref = reader.getAttributeValue(null, "bean");                            
                            // Check if the parent element is a property
                            if (property != null) property.addRef(ref);
                            // Check if the parent element is a constructor-arg
                            if (constructorArg != null) constructorArg.addRef(ref);
                        } else if (SpringImplementationConstants.VALUE_ELEMENT.equals(qname)) {
                            String value = reader.getElementText();
                            // Check if the parent element is a constructor-arg
                            if (constructorArg != null) constructorArg.addValue(value);
                        } else if (SpringImplementationConstants.LIST_ELEMENT.equals(qname) ||
                        		   SpringImplementationConstants.SET_ELEMENT.equals(qname)  ||
                        		   SpringImplementationConstants.MAP_ELEMENT.equals(qname)) {                        	
                        	if (property != null) 
                        		readCollections(reader, bean, beans, property, null);
                            if (constructorArg != null)
                            	readCollections(reader, bean, beans, null, constructorArg);
                        } // end if
                        break;
                    case END_ELEMENT:
                        if (SpringImplementationConstants.BEAN_ELEMENT.equals(reader.getName())) {
                            completed = true;
                            break;
                        } else if (SpringImplementationConstants.PROPERTY_ELEMENT.equals(reader.getName())) {
                            property = null;
                        } else if (SpringImplementationConstants.CONSTRUCTORARG_ELEMENT.equals(reader.getName())) {
                            constructorArg = null;
                        } // end if
                } // end switch
            } // end while
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    
    /**
     * Method which reads the collection elements from Spring application-context.xml file and identifies
     * the defined beans, list, maps and sets
     */
    private void readCollections(XMLStreamReader reader,
                                 SpringBeanElement bean,
                                 List<SpringBeanElement> beans,
                                 SpringPropertyElement property,
                                 SpringConstructorArgElement constructorArg) throws ContributionReadException {
    	
    	SpringBeanElement innerbean = null;
        
        try {
            boolean completed = false;            
            while (!completed) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (SpringImplementationConstants.BEAN_ELEMENT.equals(qname)) {
                        	innerbean = new SpringBeanElement(reader.getAttributeValue(null, "id"), reader
                                    .getAttributeValue(null, "class"));
                        	// Set the first name as bean name, when the @id attribute is absent.
                            if (reader.getAttributeValue(null, "id") == null)
                            	if (reader.getAttributeValue(null, "name") != null) {
                            		String[] names = (reader.getAttributeValue(null, "name")).split(",");
                            		innerbean.setId(names[0]);
                            	}
                            innerbean.setInnerBean(true);
                            beans.add(innerbean);
                            readBeanDefinition(reader, innerbean, beans);
                        } else if (SpringImplementationConstants.REF_ELEMENT.equals(qname)) {
                        	String ref = reader.getAttributeValue(null, "bean");
                            if (property != null) property.addRef(ref);
                            if (constructorArg != null) constructorArg.addRef(ref);                            
                        } else if (SpringImplementationConstants.LIST_ELEMENT.equals(qname) ||
                        		   SpringImplementationConstants.SET_ELEMENT.equals(qname) ||
                        		   SpringImplementationConstants.MAP_ELEMENT.equals(qname)) {
                        	if (property != null) 
                        		readCollections(reader, innerbean, beans, property, null);
                        	if (constructorArg != null)
                            	readCollections(reader, innerbean, beans, null, constructorArg);                        	
                        } else if (SpringImplementationConstants.ENTRY_ELEMENT.equals(qname)) {
                            String keyRef = reader.getAttributeValue(null, "key-ref");
                            String valueRef = reader.getAttributeValue(null, "value-ref");
                            if (property != null) {property.addRef(keyRef); property.addRef(valueRef);}
                            if (constructorArg != null) {constructorArg.addRef(keyRef); constructorArg.addRef(valueRef);}
                        } // end if
                        break;
                    case END_ELEMENT:
                        if (SpringImplementationConstants.LIST_ELEMENT.equals(reader.getName())) {
                        	completed = true;
                            break;
                        } else if (SpringImplementationConstants.SET_ELEMENT.equals(reader.getName())) {
                        	completed = true;
                            break;
                        } else if (SpringImplementationConstants.MAP_ELEMENT.equals(reader.getName())) {
                        	completed = true;
                            break;
                        } // end if
                } // end switch
            } // end while
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    /**
     * Generates the Spring implementation component type from the configuration contained in the
     * lists of beans, services, references and scaproperties derived from the application context
     */
    private void generateComponentType(SpringImplementation implementation,
                                       ModelResolver resolver,
                                       List<SpringBeanElement> beans,
                                       List<SpringSCAServiceElement> services,
                                       List<SpringSCAReferenceElement> references,
                                       List<SpringSCAPropertyElement> scaproperties,
                                       ProcessorContext context) throws ContributionReadException {
        /*
         * 1. Each sca:service becomes a service in the component type
         * 2. Each sca:reference becomes a reference in the component type
         * 3. Each sca:property becomes a property in the component type
         * 4. IF there are no explicit service elements, each bean becomes a service
         * 5. Each bean property which is a reference not pointing at another bean in the
         *    application context becomes a reference unless it is pointing at one of the references
         * 6. Each bean property which is not a reference and which is not pointing
         *    at another bean in the application context becomes a property in the component type
         */

    	JavaImplementation javaImplementation = null;
        ComponentType componentType = implementation.getComponentType();

        try {
            // Deal with the services first....
            Iterator<SpringSCAServiceElement> its = services.iterator();
            while (its.hasNext()) {
                SpringSCAServiceElement serviceElement = its.next();                
                Class<?> interfaze = resolveClass(resolver, serviceElement.getType(), context);
                Service theService = createService(interfaze, serviceElement.getName());
                // Spring allows duplication of bean definitions in multiple context scenario,
                // in such cases, the latest bean definition overrides the older ones, hence 
                // we will remove any older definition and use the latest.
                Service duplicate = null;
                for (Service service : componentType.getServices()) {
                	if (service.getName().equals(theService.getName()))
                		duplicate = service;
                }
                if (duplicate != null)
                	componentType.getServices().remove(duplicate);
                
                componentType.getServices().add(theService);
                // Add this service to the Service / Bean map
                String beanName = serviceElement.getTarget();
                for (SpringBeanElement beanElement : beans) {
                    if (beanName.equals(beanElement.getId())) {
                    	if (isvalidBeanForService(beanElement)) {
                    		// add the required intents and policySets for the service
                            theService.getRequiredIntents().addAll(serviceElement.getRequiredIntents());
                            theService.getPolicySets().addAll(serviceElement.getPolicySets());
                            implementation.setBeanForService(theService, beanElement);
                    	}
                    }
                } // end for
            } // end while

            // Next handle the references
            Iterator<SpringSCAReferenceElement> itr = references.iterator();
            while (itr.hasNext()) {
                SpringSCAReferenceElement referenceElement = itr.next();
                Class<?> interfaze = resolveClass(resolver, referenceElement.getType(), context);
                Reference theReference = createReference(interfaze, referenceElement.getName());
                // Override the older bean definition with the latest ones
                // for the duplicate definitions found.
                Reference duplicate = null;
                for (Reference reference : componentType.getReferences()) {
                	if (reference.getName().equals(theReference.getName()))
                		duplicate = reference;
                }
                if (duplicate != null)
                	componentType.getReferences().remove(duplicate);
                
                // add the required intents and policySets for this reference
                theReference.getRequiredIntents().addAll(referenceElement.getRequiredIntents());
                theReference.getPolicySets().addAll(referenceElement.getPolicySets());
                componentType.getReferences().add(theReference);
            } // end while

            // Next handle the properties
            Iterator<SpringSCAPropertyElement> itsp = scaproperties.iterator();
            while (itsp.hasNext()) {
                SpringSCAPropertyElement scaproperty = itsp.next();
                // Create a component type property if the SCA property element has a name
                // and a type declared...
                if (scaproperty.getType() != null && scaproperty.getName() != null) {
                    Property theProperty = assemblyFactory.createProperty();
                    theProperty.setName(scaproperty.getName());
                    // Get the Java class and then an XSD element type for the property
                    Class<?> propType = Class.forName(scaproperty.getType());
                    theProperty.setXSDType(JavaXMLMapper.getXMLType(propType));
                    // Override the older bean definition with the latest ones
                    // for the duplicate definitions found.
                    Property duplicate = null;
                    for (Property property : componentType.getProperties()) {
                    	if (property.getName().equals(theProperty.getName()))
                    		duplicate = property;
                    }
                    if (duplicate != null)
                    	componentType.getProperties().remove(duplicate);
                    
                    componentType.getProperties().add(theProperty);
                    // Remember the Java Class (ie the type) for this property
                    implementation.setPropertyClass(theProperty.getName(), propType);
                } // end if
            } // end while

            // Finally deal with the beans
            Iterator<SpringBeanElement> itb;
            // If there are no explicit service elements, then expose all the beans
            if (services.isEmpty()) {
                itb = beans.iterator();
                // Loop through all the beans found
                while (itb.hasNext()) {
                    SpringBeanElement beanElement = itb.next();
                    // If its not a valid bean for service, ignore it
                    if (!isvalidBeanForService(beanElement)) continue;
                    // Load the Spring bean class
                    Class<?> beanClass = resolveClass(resolver, beanElement.getClassName(), context);
                    // Introspect the bean
                    beanIntrospector =
                        new SpringBeanIntrospector(assemblyFactory, javaFactory, policyFactory, beanElement.getCustructorArgs());
                    ComponentType beanComponentType = assemblyFactory.createComponentType();
                    javaImplementation = beanIntrospector.introspectBean(beanClass, beanComponentType);
                    // Set the service name as bean name
                    for (Service componentService : beanComponentType.getServices())                    	
                    	componentService.setName(beanElement.getId());
                    // Get the service interface defined by this Spring Bean and add to
                    // the component type of the Spring Assembly
                    List<Service> beanServices = beanComponentType.getServices();
                    componentType.getServices().addAll(beanServices);
                    // Add these services to the Service / Bean map
                    for (Service beanService : beanServices) {
                        implementation.setBeanForService(beanService, beanElement);
                    }
                } // end while
            } // end if

            itb = beans.iterator();
            while (itb.hasNext()) {
                SpringBeanElement beanElement = itb.next();
                // Ignore if the bean has no properties and constructor arguments
                if (beanElement.getProperties().isEmpty() && beanElement.getCustructorArgs().isEmpty())
                	continue;

                Class<?> beanClass = resolveClass(resolver, beanElement.getClassName(), context);
                // Introspect the bean
                beanIntrospector =
                    new SpringBeanIntrospector(assemblyFactory, javaFactory, policyFactory, beanElement.getCustructorArgs());
                ComponentType beanComponentType = assemblyFactory.createComponentType();
                javaImplementation = beanIntrospector.introspectBean(beanClass, beanComponentType);
                Map<String, JavaElementImpl> propertyMap = javaImplementation.getPropertyMembers();
                JavaConstructorImpl constructor = javaImplementation.getConstructor();
                // Get the references by this Spring Bean and add the unresolved ones to
                // the component type of the Spring Assembly
                List<Reference> beanReferences = beanComponentType.getReferences();
                List<Property> beanProperties = beanComponentType.getProperties();

                Iterator<SpringPropertyElement> itp = beanElement.getProperties().iterator();
                while (itp.hasNext()) {
                    SpringPropertyElement propertyElement = itp.next();
                    for (String propertyRef : propertyElement.getRefs()) {
	                    if (propertyRefUnresolved(propertyRef, beans, references, scaproperties)) {
	                        // This means an unresolved reference from the spring bean...
	                    	for (Reference reference : beanReferences) {
	                            if (propertyElement.getName().equals(reference.getName())) {
	                            	// The name of the reference in this case is the string in
	                                // the @ref attribute of the Spring property element, NOT the
	                                // name of the field in the Spring bean....
	                                reference.setName(propertyRef);
	                                componentType.getReferences().add(reference);
	                            } // end if
	                        } // end for
	
	                    	// Store the unresolved references as unresolvedBeanRef in the Spring Implementation type
	                    	for (Property scaproperty : beanProperties) {
	                            if (propertyElement.getName().equals(scaproperty.getName())) {
	                            	// The name of the reference in this case is the string in
	                                // the @ref attribute of the Spring property element, NOT the
	                                // name of the field in the Spring bean....
	                            	Class<?> interfaze = resolveClass(resolver, (propertyMap.get(propertyElement.getName()).getType()).getName(), context);
	                                Reference theReference = createReference(interfaze, propertyRef);
	                                implementation.setUnresolvedBeanRef(propertyRef, theReference);
	                            } // end if
	                    	} // end for
	                    } // end if 
                    } // end for
                } // end while

                Iterator<SpringConstructorArgElement> itcr = beanElement.getCustructorArgs().iterator();
                while (itcr.hasNext()) {
                	SpringConstructorArgElement conArgElement = itcr.next();
                	for (String constructorArgRef : conArgElement.getRefs()) {
	                	if (propertyRefUnresolved(constructorArgRef, beans, references, scaproperties)) {
	                    	for (JavaParameterImpl parameter : constructor.getParameters()) {
	                    		String paramType = parameter.getType().getName();
	                    		Class<?> interfaze = resolveClass(resolver, paramType, context);
	                    		// Create a component type reference/property if the constructor-arg element has a
	                            // type attribute OR index attribute declared...
	                    		if ((conArgElement.getType() != null && paramType.equals(conArgElement.getType())) ||
	                    		    (conArgElement.getIndex() != -1 && (conArgElement.getIndex() == parameter.getIndex())))
	                    		{
	                    			if (parameter.getClassifer().getName().equals("org.osoa.sca.annotations.Reference")) {
	                    				Reference theReference = createReference(interfaze, constructorArgRef);
	                    				componentType.getReferences().add(theReference);
	                    			}
	                    			if (parameter.getClassifer().getName().equals("org.osoa.sca.annotations.Property")) {
	                    				// Store the unresolved references as unresolvedBeanRef in the Spring Implementation type
	                                	// we might need to verify with the component definition later.
	                    				Reference theReference = createReference(interfaze, constructorArgRef);
	                        			implementation.setUnresolvedBeanRef(constructorArgRef, theReference);
	                    			}
	                    		}
	                    	} // end for
	                    } // end if
                	} // end for
                } // end while

            } // end while

        } catch (ClassNotFoundException e) {
            // Means that either an interface class, property class or a bean was not found
            throw new ContributionReadException(e);
        } catch (InvalidInterfaceException e) {
            throw new ContributionReadException(e);
        } catch (ContributionResolveException e) {

        } // end try

        // If we get here, the Spring assembly component type is resolved
        componentType.setUnresolved(false);
        implementation.setComponentType(componentType);
        return;
    } // end method generateComponentType

    /*
     * Determines whether a reference attribute of a Spring property element is resolved either
     * by a bean in the application context or by an SCA reference element or by an SCA property
     * element
     * @param ref - a String containing the name of the reference - may be null
     * @param beans - a List of SpringBean elements
     * @param references - a List of SCA reference elements
     * @return true if the property is not resolved, false if it is resolved
     */
    private boolean propertyRefUnresolved(String ref,
                                          List<SpringBeanElement> beans,
                                          List<SpringSCAReferenceElement> references,
                                          List<SpringSCAPropertyElement> scaproperties) {
        boolean unresolved = true;

        if (ref != null) {
            // Scan over the beans looking for a match
            Iterator<SpringBeanElement> itb = beans.iterator();
            while (itb.hasNext()) {
                SpringBeanElement beanElement = itb.next();
                // Does the bean name match the ref?
                if (ref.equals(beanElement.getId())) {
                    unresolved = false;
                    break;
                } // end if
            } // end while
            // Scan over the SCA reference elements looking for a match
            if (unresolved) {
                Iterator<SpringSCAReferenceElement> itr = references.iterator();
                while (itr.hasNext()) {
                    SpringSCAReferenceElement referenceElement = itr.next();
                    if (ref.equals(referenceElement.getName())) {
                        unresolved = false;
                        break;
                    } // end if
                } // end while
            } // end if
            // Scan over the SCA property elements looking for a match
            if (unresolved) {
                Iterator<SpringSCAPropertyElement> itsp = scaproperties.iterator();
                while (itsp.hasNext()) {
                    SpringSCAPropertyElement propertyElement = itsp.next();
                    if (ref.equals(propertyElement.getName())) {
                        unresolved = false;
                        break;
                    } // end if
                } // end while
            } // end if
        } else {
            // In the case where ref = null, the property is not going to be a reference of any
            // kind and can be ignored
            unresolved = false;
        } // end if

        return unresolved;

    } // end method propertyRefUnresolved
    
    /**
     * Validates whether the <sca:service>, <sca:reference> and <sca:property> elements
     * has unique names within the application context.
     */
    private void validateBeans(List<SpringBeanElement> beans,
            				   List<SpringSCAServiceElement> services,
            				   List<SpringSCAReferenceElement> references,
            				   List<SpringSCAPropertyElement> scaproperties) throws ContributionReadException {
    	        
    	// The @target attribute of a <service/> subelement of a <beans/> element 
    	// MUST have the value of the @name attribute of one of the <bean/> 
    	// subelements of the <beans/> element.    	
    	Iterator<SpringSCAServiceElement> its = services.iterator();
        while (its.hasNext()) {
        	SpringSCAServiceElement serviceElement = its.next();
        	boolean targetBeanExists = false;
        	Iterator<SpringBeanElement> itb = beans.iterator();
        	while (itb.hasNext()) {
        		SpringBeanElement beanElement = itb.next();
        		if (serviceElement.getTarget().equals(beanElement.getId()))
        			targetBeanExists = true;
        	}
        	if (!targetBeanExists)
        		error("TargetBeanDoesNotExist", beans);
        } // end while
        
    	// The value of the @name attribute of an <sca:reference/> subelement of a <beans/> 
    	// element MUST be unique amongst the @name attributes of the <sca:property/> 
        // subelements and the <bean/> subelements of the <beans/> element.
        // 									AND
        // The @default attribute of a <sca:reference/> subelement of a <beans/>  
        // element MUST have the value of the @name attribute of one of the <bean/> 
        // subelements of the <beans/> element.
        Iterator<SpringSCAReferenceElement> itr = references.iterator();
        while (itr.hasNext()) {
        	SpringSCAReferenceElement referenceElement = itr.next();
        	boolean defaultBeanExists = true;
        	boolean isUniqueReferenceName = true;
        	Iterator<SpringBeanElement> itb = beans.iterator();
        	while (itb.hasNext()) {
        		SpringBeanElement beanElement = itb.next();
        		if (referenceElement.getDefaultBean() != null)
        			if (referenceElement.getDefaultBean().equals(beanElement.getId()))
        				defaultBeanExists = false;
        		if (referenceElement.getName().equals(beanElement.getId()))
        			isUniqueReferenceName = false;
        	}
        	Iterator<SpringSCAPropertyElement> itp = scaproperties.iterator();
        	while (itp.hasNext()) {
        		SpringSCAPropertyElement propertyElement = itp.next();
        		if (referenceElement.getName().equals(propertyElement.getName()))
        			isUniqueReferenceName = false;
        	}
        	if (!defaultBeanExists)
        		error("DefaultBeanDoesNotExist", beans);
        	if (!isUniqueReferenceName)
        		error("ScaReferenceNameNotUnique", beans);
        } // end while
            	
    	// The value of the @name attribute of an <sca:property/> subelement of a <beans/> 
    	// element MUST be unique amongst the @name attributes of the <sca:reference/> 
        // subelements and the <bean/> subelements of the <beans/> element.    	
        Iterator<SpringSCAPropertyElement> itp = scaproperties.iterator();
        while (itp.hasNext()) {
        	SpringSCAPropertyElement propertyElement = itp.next();
        	boolean isUniquePropertyName = true;
        	Iterator<SpringBeanElement> itb = beans.iterator();
        	while (itb.hasNext()) {
        		SpringBeanElement beanElement = itb.next();
        		if (propertyElement.getName().equals(beanElement.getId()))
        			isUniquePropertyName = false;	
        	}
        	Iterator<SpringSCAReferenceElement> itrp = references.iterator();
            while (itrp.hasNext()) {
            	SpringSCAReferenceElement referenceElement = itrp.next();
            	if (propertyElement.getName().equals(referenceElement.getName()))
        			isUniquePropertyName = false;
            }
        	if (!isUniquePropertyName)
        		error("ScaPropertyNameNotUnique", beans);
        } // end while
    }
    
    /**
     * Validates whether a bean definition is valid for exposing as service.
     */
    private boolean isvalidBeanForService(SpringBeanElement beanElement) {
    	
    	if (beanElement.isInnerBean())
    		return false;
    	if (beanElement.hasParentAttribute())
    		return false;
    	if (beanElement.hasFactoryMethodAttribute())
    		return false;
    	if (beanElement.hasFactoryBeanAttribute())
    		return false;
    	if (beanElement.getClassName() == null)
    		return false;
    	if (beanElement.getClassName().startsWith("org.springframework"))
    		return false;
        // return true otherwise	
    	return true;
    }
    
    
    /**
     * Gets hold of the application-context.xml file as a Spring resource
     * @param locationAttr - the location attribute from the <implementation.spring../> element
     * @param cl - the ClassLoader for the Spring implementation
     */
    protected List<URL> getApplicationContextResource(URL url)
        throws ContributionReadException {
        File manifestFile = null;
        File appXmlFile;
        File appXmlFolder;
        File locationFile = null;
        List<URL> appCtxResources = new ArrayList<URL>();
        
        if (url != null) {
            String path = url.getPath();
            locationFile = new File(path);
        } else {
            throw new ContributionReadException("SpringXMLComponentTypeLoader getApplicationContextResource: " 
            		              + "unable to find resource file " + url);
        }

        if (locationFile.isDirectory()) {
            try {
                manifestFile = new File(locationFile, "META-INF"+ File.separator +"MANIFEST.MF");
                if (manifestFile.exists()) {
                    Manifest mf = new Manifest(new FileInputStream(manifestFile));
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                    	String[] cxtPaths = appCtxPath.split(";");
	            		for (String path : cxtPaths) {
	            			appXmlFile = new File(locationFile, path.trim());
	            			if (appXmlFile.exists()) {
	            				appCtxResources.add(appXmlFile.toURI().toURL());
	            			}
	            		}
	            		return appCtxResources;
                    }
                }                
                // No MANIFEST.MF file OR no manifest-specified Spring context , then read all the 
                // xml files available in the META-INF/spring folder.
                appXmlFolder = new File(locationFile, "META-INF" + File.separator + "spring");
                if (appXmlFolder.exists()) {
                	File[] files = appXmlFolder.listFiles();
                	for (File appFile: files) {
                		if (appFile.getName().endsWith(".xml")) {
                			appCtxResources.add(appFile.toURI().toURL());
                		}
                	}
                	return appCtxResources;
                }
            } catch (IOException e) {
                throw new ContributionReadException("Error reading manifest " + manifestFile);
            }
        } else {
        	if (locationFile.isFile() && locationFile.getName().endsWith(".jar")) {
                try {
                    JarFile jf = new JarFile(locationFile);                    
                    JarEntry je;
                    Manifest mf = jf.getManifest();
                    if (mf != null) {
                        Attributes mainAttrs = mf.getMainAttributes();
                        String appCtxPath = mainAttrs.getValue("Spring-Context");
                        if (appCtxPath != null) {
                        	String[] cxtPaths = appCtxPath.split(";");
    	            		for (String path : cxtPaths) {
    	            			je = jf.getJarEntry(path.trim());
    	            			if (je != null)
    	            				appCtxResources.add(new URL("jar:" + locationFile.toURI().toURL() + "!/" + appCtxPath));
    	            		}
    	            		return appCtxResources;
                        }
                    }
                    // No MANIFEST.MF file OR no manifest-specified Spring context , then read all the 
                    // .xml files available in the META-INF/spring folder.
                    Enumeration<JarEntry> entries = jf.entries();
                    while (entries.hasMoreElements()) {
                    	je = entries.nextElement();
                    	if (je.getName().startsWith("META-INF/spring/") && je.getName().endsWith(".xml")) {
                    		appCtxResources.add(new URL("jar:" + locationFile.toURI().toURL() + "!/" + je.getName()));
                    	}
                    }
                    return appCtxResources;
                } catch (IOException e) {
                    // TODO: create a more appropriate exception type
                    throw new ContributionReadException("SpringXMLComponentTypeLoader getApplicationContextResource: "
                    												+ " IO exception reading context file.", e);
                }
        	}
        	else {
        		if (locationFile.getName().endsWith(".xml")) {
        			appCtxResources.add(url);
                	return appCtxResources;
        		}
        		else {
        			// Deal with the directory inside a jar file, in case the contribution itself is a JAR file.
        			try {
	        			if (locationFile.getPath().indexOf(".jar") > 0) {
	        				String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
	        				JarFile jf = new JarFile(jarPath);
	        				JarEntry je = jf.getJarEntry(url.getPath().substring(url.getPath().indexOf("!/")+2)
	        												+ "/" + "META-INF" + "/" + "MANIFEST.MF");
	        			    if (je != null) {
	        					Manifest mf = new Manifest(jf.getInputStream(je));
	        					Attributes mainAttrs = mf.getMainAttributes();
	                            String appCtxPath = mainAttrs.getValue("Spring-Context");
	                            if (appCtxPath != null) {
	                            	String[] cxtPaths = appCtxPath.split(";");
	        	            		for (String path : cxtPaths) {
		                                je = jf.getJarEntry(url.getPath().substring(url.getPath().indexOf("!/")+2) + "/" + path.trim());
		                                if (je != null) {
		                                	appCtxResources.add(new URL("jar:" + url.getPath() + "/" + path.trim()));		    	                        	
		                                }
	        	            		}
	        	            		return appCtxResources;
	                            }
	        				}	        			    
	        			    // No MANIFEST.MF file OR no manifest-specified Spring context , then read all the 
	                        // .xml files available in the META-INF/spring folder.
	        			    Enumeration<JarEntry> entries = jf.entries();
	                        while (entries.hasMoreElements()) {
	                        	je = entries.nextElement();
	                        	if (je.getName().startsWith("META-INF/spring/") && je.getName().endsWith(".xml")) {
	                        		appCtxResources.add(new URL("jar:" + url.getPath() + "/" + je.getName()));
	                        	}
	                        }
                            return appCtxResources;
	        			}
            		} catch (IOException e) {
                        throw new ContributionReadException("Error reading manifest " + manifestFile);
                    }
        		}
        	}
        }

        throw new ContributionReadException("SpringXMLComponentTypeLoader getApplicationContextResource: "
        																+ "unable to read resource file " + url);
    } // end method getApplicationContextResource

    /**
     * Creates a Service for the component type based on its name and Java interface
     */
    public Service createService(Class<?> interfaze, String name) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        // Set the name for the service
        service.setName(name);

        // Set the call interface and, if present, the callback interface
        JavaInterface callInterface = javaFactory.createJavaInterface(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    } // end method createService

    /**
     * Creates a Reference for the component type based on its name and Java interface
     */
    private org.apache.tuscany.sca.assembly.Reference createReference(Class<?> interfaze, String name)
        throws InvalidInterfaceException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);

        // Set the name of the reference to the supplied name and the multiplicity of the reference
        // to 1..1 - for Spring implementations, this is the only multiplicity supported
        reference.setName(name);
        reference.setMultiplicity(Multiplicity.ONE_ONE);

        // Set the call interface and, if present, the callback interface
        JavaInterface callInterface = javaFactory.createJavaInterface(interfaze);
        reference.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
            reference.getInterfaceContract().setCallbackInterface(callbackInterface);
        }

        return reference;
    }
    
    private class ContextClassLoader extends ClassLoader {
    	public ContextClassLoader(ModelResolver resolver, ProcessorContext context) {
    		super();
    		this.resolver = resolver;
    		this.context = context;
    	}

		private ModelResolver resolver;
		private ProcessorContext context;
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return SpringXMLComponentTypeLoader.this.resolveClass(resolver, name, context);
		}
		
		@Override
		protected URL findResource(String name) {
			try {
				return resolveLocation(resolver, name, context);
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected Enumeration<URL> findResources(String name) throws IOException {
			URL url = findResource(name);
			if (url != null) {
				return Collections.enumeration(Arrays.asList(url));
			} else {
				Collection<URL> urls = Collections.emptyList();
				return Collections.enumeration(urls);
			}
		}
    }
} // end class SpringXMLComponentTypeLoader