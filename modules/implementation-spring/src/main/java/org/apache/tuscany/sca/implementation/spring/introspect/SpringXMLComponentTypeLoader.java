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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringConstructorArgElement;
import org.apache.tuscany.sca.implementation.spring.SpringImplementation;
import org.apache.tuscany.sca.implementation.spring.SpringPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;
import org.apache.tuscany.sca.implementation.spring.xml.SpringXMLBeanDefinitionLoader;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Introspects a Spring XML application-context configuration file to create <implementation-spring../>
 * component type information.
 *
 * @version $Rev$ $Date$
 */
public class SpringXMLComponentTypeLoader {
    private final static Logger log = Logger.getLogger(SpringXMLComponentTypeLoader.class.getName());

    private ExtensionPointRegistry registry;
    private ContributionFactory contributionFactory;
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private SpringBeanIntrospector beanIntrospector;

    private SpringXMLBeanDefinitionLoader xmlBeanDefinitionLoader;

    public SpringXMLComponentTypeLoader(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.javaFactory = factories.getFactory(JavaInterfaceFactory.class);
        this.contributionFactory = factories.getFactory(ContributionFactory.class);
        this.xmlBeanDefinitionLoader =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(SpringXMLBeanDefinitionLoader.class);
    }

    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "impl-spring-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
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
    private void warning(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "impl-spring-validation-messages",
                                      Severity.WARNING,
                                      model,
                                      message,
                                      (Object[])messageParameters);
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
    public void load(SpringImplementation implementation, ModelResolver resolver, ProcessorContext context)
        throws ContributionReadException {
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

    private Class<?> resolveClass(ModelResolver resolver, String className, ProcessorContext context)
        throws ClassNotFoundException {
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
    private void loadFromXML(SpringImplementation implementation, ModelResolver resolver, ProcessorContext context)
        throws ContributionReadException {
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

            List<SpringBeanElement> appCxtBeans = new ArrayList<SpringBeanElement>();
            List<SpringSCAServiceElement> appCxtServices = new ArrayList<SpringSCAServiceElement>();
            List<SpringSCAReferenceElement> appCxtReferences = new ArrayList<SpringSCAReferenceElement>();
            List<SpringSCAPropertyElement> appCxtProperties = new ArrayList<SpringSCAPropertyElement>();

            if (xmlBeanDefinitionLoader != null) {
                xmlBeanDefinitionLoader.load(contextResources,
                                                   appCxtServices,
                                                   appCxtReferences,
                                                   appCxtProperties,
                                                   appCxtBeans,
                                                   context);
            }
            // Validate the beans from individual application context for uniqueness
            validateBeans(appCxtBeans, appCxtServices, appCxtReferences, appCxtProperties, context.getMonitor());
            // Add all the validated beans to the generic list
            beans.addAll(appCxtBeans);
            services.addAll(appCxtServices);
            references.addAll(appCxtReferences);
            scaproperties.addAll(appCxtProperties);
        } catch (Throwable e) {
            throw new ContributionReadException(e);
        }

        /* At this point, the complete application-context.xml file has been read and its contents  */
        /* stored in the lists of beans, services, references.  These are now used to generate      */
        /* the implied componentType for the application context								    */
        generateComponentType(implementation, resolver, beans, services, references, scaproperties, context);

        return;
    } // end method loadFromXML

    private URL resolveLocation(ModelResolver resolver, String contextPath, ProcessorContext context)
        throws MalformedURLException, ContributionReadException {
        URL resource = null;
        URI uri = URI.create(contextPath);
        if (!uri.isAbsolute()) {
            Artifact parent = context.getArtifact();
            if (parent != null && parent.getURI() != null) {
                URI base = URI.create("/" + parent.getURI());
                uri = base.resolve(uri);
                // Remove the leading / to make artifact resolver happy
                if (uri.toString().startsWith("/")) {
                    uri = URI.create(uri.toString().substring(1));
                }
            }
            Artifact artifact = contributionFactory.createArtifact();
            artifact.setUnresolved(true);
            artifact.setURI(uri.toString());
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
                boolean found = false;
                for (SpringBeanElement beanElement : beans) {
                    if (beanName.equals(beanElement.getId())) {
                        if (isValidBeanForService(beanElement)) {
                            // add the required intents and policySets for the service
                            theService.getRequiredIntents().addAll(serviceElement.getRequiredIntents());
                            theService.getPolicySets().addAll(serviceElement.getPolicySets());
                            implementation.setBeanForService(theService, beanElement);
                            found = true;
                            break;
                        }
                    }
                } // end for
                
                if (!found) {
                    // REVIEW: Adding a SpringBeanElement "proxy" so that the bean id can be used at runtime to look
                    // up the bean instance from the parent context
                    implementation.setBeanForService(theService,
                                                     new SpringBeanElement(serviceElement.getTarget(), null));
                }
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
                    if (!isValidBeanForService(beanElement)) {
                        continue;
                    }
                    try {
                        // Load the Spring bean class
                        Class<?> beanClass = resolveClass(resolver, beanElement.getClassName(), context);
                        // Introspect the bean
                        beanIntrospector = new SpringBeanIntrospector(registry, beanElement.getCustructorArgs());
                        ComponentType beanComponentType = assemblyFactory.createComponentType();
                        javaImplementation = beanIntrospector.introspectBean(beanClass, beanComponentType);
                        // Set the service name as bean name
                        for (Service componentService : beanComponentType.getServices()) {
                            componentService.setName(beanElement.getId());
                        }
                        // Get the service interface defined by this Spring Bean and add to
                        // the component type of the Spring Assembly
                        List<Service> beanServices = beanComponentType.getServices();
                        componentType.getServices().addAll(beanServices);
                        // Add these services to the Service / Bean map
                        for (Service beanService : beanServices) {
                            implementation.setBeanForService(beanService, beanElement);
                        }
                    } catch (Throwable e) {
                        // [rfeng] FIXME: Some Spring beans have constructors that take pararemters injected by Spring and
                        // Tuscany is not happy with that during the introspection
                        log.log(Level.SEVERE, e.getMessage(), e);
                    }
                } // end while
            } // end if

            itb = beans.iterator();
            while (itb.hasNext()) {
                SpringBeanElement beanElement = itb.next();

                // If its not a valid bean for service, ignore it
                if (!isValidBeanForService(beanElement)) {
                    continue;
                }
                // Ignore if the bean has no properties and constructor arguments
                if (beanElement.getProperties().isEmpty() && beanElement.getCustructorArgs().isEmpty())
                    continue;

                ComponentType beanComponentType = assemblyFactory.createComponentType();

                try {
                    Class<?> beanClass = resolveClass(resolver, beanElement.getClassName(), context);
                    // Introspect the bean
                    beanIntrospector = new SpringBeanIntrospector(registry, beanElement.getCustructorArgs());
                    javaImplementation = beanIntrospector.introspectBean(beanClass, beanComponentType);
                } catch (Exception e) {
                    // [rfeng] FIXME: Some Spring beans have constructors that take pararemters injected by Spring and
                    // Tuscany is not happy with that during the introspection
                    log.log(Level.SEVERE, e.getMessage(), e);
                    continue;
                }
                Map<String, JavaElementImpl> propertyMap = javaImplementation.getPropertyMembers();
                JavaConstructorImpl constructor = javaImplementation.getConstructor();
                // Get the references by this Spring Bean and add the unresolved ones to
                // the component type of the Spring Assembly
                List<Reference> beanReferences = beanComponentType.getReferences();
                List<Property> beanProperties = beanComponentType.getProperties();

                Set<String> excludedNames = new HashSet<String>();
                Iterator<SpringPropertyElement> itp = beanElement.getProperties().iterator();
                while (itp.hasNext()) {
                    SpringPropertyElement propertyElement = itp.next();
                    // Exclude the reference that is also known as a spring property
                    excludedNames.add(propertyElement.getName());
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
                                    break;
                                } // end if
                            } // end for

                            // Store the unresolved references as unresolvedBeanRef in the Spring Implementation type
                            for (Property scaproperty : beanProperties) {
                                if (propertyElement.getName().equals(scaproperty.getName())) {
                                    // The name of the reference in this case is the string in
                                    // the @ref attribute of the Spring property element, NOT the
                                    // name of the field in the Spring bean....
                                    Class<?> interfaze =
                                        resolveClass(resolver,
                                                     (propertyMap.get(propertyElement.getName()).getType()).getName(),
                                                     context);
                                    Reference theReference = createReference(interfaze, propertyRef);
                                    implementation.setUnresolvedBeanRef(propertyRef, theReference);
                                    break;
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
                                if ((conArgElement.getType() != null && paramType.equals(conArgElement.getType())) || (conArgElement
                                    .getIndex() != -1 && (conArgElement.getIndex() == parameter.getIndex()))) {
                                    // [rfeng] Commenting out the following code as the constructor parameter based SCA
                                    // references are added already
                                    /*
                                    if (parameter.getClassifer() == org.oasisopen.sca.annotation.Reference.class) {
                                        Reference theReference = createReference(interfaze, constructorArgRef);
                                        componentType.getReferences().add(theReference);
                                    }
                                    */
                                    if (parameter.getClassifer() == org.oasisopen.sca.annotation.Property.class) {
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

                // [rfeng] Add the remaining introspected references (w/ @Reference but without Spring property ref)
                for (Reference ref : beanReferences) {
                    if (!excludedNames.contains(ref.getName()) && componentType.getReference(ref.getName()) == null) {
                        // Only add the ones that not listed by sca:reference
                        componentType.getReferences().add(ref);
                    }
                }

            } // end while

        } catch (ClassNotFoundException e) {
            // Means that either an interface class, property class or a bean was not found
            throw new ContributionReadException(e);
        } catch (InvalidInterfaceException e) {
            throw new ContributionReadException(e);
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
                               List<SpringSCAPropertyElement> scaproperties,
                               Monitor monitor) throws ContributionReadException {

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
            if (!targetBeanExists) {
                // REVIEW: [rfeng] The target bean can exist in the parent Spring application context which we don't know
                // until runtime
                warning(monitor, "TargetBeanDoesNotExist", beans);
            }
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
                error(monitor, "DefaultBeanDoesNotExist", beans);
            if (!isUniqueReferenceName)
                error(monitor, "ScaReferenceNameNotUnique", beans);
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
                error(monitor, "ScaPropertyNameNotUnique", beans);
        } // end while
    }

    /**
     * Validates whether a bean definition is valid for exposing as service.
     */
    private boolean isValidBeanForService(SpringBeanElement beanElement) {

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
    protected List<URL> getApplicationContextResource(URL url) throws ContributionReadException {
        File manifestFile = null;
        File appXmlFile;
        File appXmlFolder;
        File locationFile = null;
        List<URL> appCtxResources = new ArrayList<URL>();

        if (url != null) {
            String path = url.getPath();
            locationFile = new File(path);
        } else {
            throw new ContributionReadException(
                                                "SpringXMLComponentTypeLoader getApplicationContextResource: " + "unable to find resource file "
                                                    + url);
        }

        if (locationFile.isDirectory()) {
            try {
                manifestFile = new File(locationFile, "META-INF" + File.separator + "MANIFEST.MF");
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
                    for (File appFile : files) {
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
                                    appCtxResources.add(new URL("jar:" + locationFile.toURI().toURL()
                                        + "!/"
                                        + appCtxPath));
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
                    throw new ContributionReadException(
                                                        "SpringXMLComponentTypeLoader getApplicationContextResource: " + " IO exception reading context file.",
                                                        e);
                }
            } else {
                if (locationFile.getName().endsWith(".xml")) {
                    appCtxResources.add(url);
                    return appCtxResources;
                } else {
                    // Deal with the directory inside a jar file, in case the contribution itself is a JAR file.
                    try {
                        if (locationFile.getPath().indexOf(".jar") > 0) {
                            String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                            JarFile jf = new JarFile(jarPath);
                            JarEntry je =
                                jf.getJarEntry(url.getPath().substring(url.getPath().indexOf("!/") + 2) + "/"
                                    + "META-INF"
                                    + "/"
                                    + "MANIFEST.MF");
                            if (je != null) {
                                Manifest mf = new Manifest(jf.getInputStream(je));
                                Attributes mainAttrs = mf.getMainAttributes();
                                String appCtxPath = mainAttrs.getValue("Spring-Context");
                                if (appCtxPath != null) {
                                    String[] cxtPaths = appCtxPath.split(";");
                                    for (String path : cxtPaths) {
                                        je =
                                            jf.getJarEntry(url.getPath().substring(url.getPath().indexOf("!/") + 2) + "/"
                                                + path.trim());
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

        throw new ContributionReadException(
                                            "SpringXMLComponentTypeLoader getApplicationContextResource: " + "unable to read resource file "
                                                + url);
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
