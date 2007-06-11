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
package org.apache.tuscany.implementation.spring.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.implementation.spring.SpringImplementation;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;

/**
 * Introspects a Spring XML application-context configuration file to create <implementation-spring../>
 * component type information. 
 * 
 *
 * @version $Rev: 512919 $ $Date: 2007-02-28 19:32:56 +0000 (Wed, 28 Feb 2007) $
 */
public class SpringXMLComponentTypeLoader {
    private static final String SCA_NS = "http://www.springframework.org/schema/sca";
    private static final String SPRING_NS = "http://www.springframework.org/schema/beans";
    private static final QName SERVICE_ELEMENT = new QName(SCA_NS, "service");
    private static final QName REFERENCE_ELEMENT = new QName(SCA_NS, "reference");
    private static final QName BEANS_ELEMENT = new QName(SPRING_NS, "beans");
    private static final QName BEAN_ELEMENT = new QName(SPRING_NS, "bean");
    private static final QName PROPERTY_ELEMENT = new QName(SPRING_NS, "property");
    private static final String APPLICATION_CONTEXT = "application-context.xml";

    private AssemblyFactory 			assemblyFactory;
    private JavaInterfaceIntrospector 	interfaceIntrospector;
    private JavaInterfaceFactory 		javaFactory;
    private PolicyFactory 				policyFactory;
    private ClassLoader 				cl;
    
    private SpringBeanIntrospector		beanIntrospector;

    public SpringXMLComponentTypeLoader( AssemblyFactory 	  assemblyFactory,
    									 JavaInterfaceIntrospector interfaceIntrospector,
    									 JavaInterfaceFactory javaFactory,
    									 PolicyFactory 		  policyFactory) {
        super();
        this.assemblyFactory 		= assemblyFactory;
        this.interfaceIntrospector 	= interfaceIntrospector;
        this.javaFactory 			= javaFactory;
        this.policyFactory			= policyFactory;
        beanIntrospector = new SpringBeanIntrospector( assemblyFactory,
   			 interfaceIntrospector, javaFactory, policyFactory );
    }

    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    /**
     * Base method which loads the component type from the application-context attached to the 
     * Spring implementation
     * 
     */
    public void load( SpringImplementation implementation ) throws ContributionReadException {
    	//System.out.println("Spring TypeLoader - load method start");
    	ComponentType componentType = implementation.getComponentType();
    	/* Check that there is a component type object already set	*/
        if ( componentType == null) {
            throw new ContributionReadException("SpringXMLLoader load: implementation has no ComponentType object");
        } 
    	if ( componentType.isUnresolved() ) {
	    	/* Fetch the location of the application-context file from the implementation */
	        loadFromXML( implementation );
	        if( !componentType.isUnresolved() ) implementation.setUnresolved( false );
    	} // end if
    	//System.out.println("Spring TypeLoader - load method complete");
    } // end method load

    /**
     * Method which fills out the component type for a Spring implementation by reading the 
     * Spring application-context.xml file
     * @param componentType - the component type to complete
     * @param location - a string containing the relative location of the application-context.xml
     * @return a Spring Resource for the application-context
     * file
     */
    private void loadFromXML( SpringImplementation implementation )
        throws ContributionReadException {
        XMLStreamReader reader;
        List<SpringBeanElement> beans = new ArrayList<SpringBeanElement>();
        List<SpringSCAServiceElement> services = new ArrayList<SpringSCAServiceElement>();
        List<SpringSCAReferenceElement> references = new ArrayList<SpringSCAReferenceElement>();
        SpringBeanElement bean = null;
        
        Resource resource;
        
        String location = implementation.getSpringLocation();
        
        try {
            // FIXME - is the ContextClassLoader the right place to start the search?
        	cl = Thread.currentThread().getContextClassLoader();

            resource = getApplicationContextResource( location, cl );
            implementation.setResource( resource );
            // The URI is used to uniquely identify the Implementation
            implementation.setURI( resource.getURL().toString() );
        	// FIXME - need a better way to handle the XMLInputFactory than allocating a new one every time
            XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
            reader = xmlFactory.createXMLStreamReader(resource.getInputStream());
            
            // System.out.println("Spring TypeLoader - starting to read context file");
            
            boolean completed = false;
            while ( !completed ) {
                switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    //System.out.println("Spring TypeLoader - found element with name: " + qname.toString());
                    if (SERVICE_ELEMENT.equals(qname)) {
                        SpringSCAServiceElement service = new SpringSCAServiceElement(
                        		reader.getAttributeValue(null, "name"),
                        		reader.getAttributeValue(null, "type"),
                        		reader.getAttributeValue(null, "target") );
                        services.add( service );
                    } else if (REFERENCE_ELEMENT.equals(qname)) {
                        SpringSCAReferenceElement reference = new SpringSCAReferenceElement(
                        		reader.getAttributeValue(null, "name"),
                        		reader.getAttributeValue(null, "type") );
                        references.add( reference );
                    } else if (BEAN_ELEMENT.equals(qname)) {
                    	// TODO FIX THIS !!
                    	int count=reader.getAttributeCount();
                    	bean = new SpringBeanElement(
                    			reader.getAttributeValue( null, "id"),
                    			reader.getAttributeValue( null, "class") );
                    	beans.add( bean );
                    } else if (PROPERTY_ELEMENT.equals(qname)) {
                    	SpringPropertyElement property = new SpringPropertyElement(
                    			reader.getAttributeValue(null, "name"),
                    			reader.getAttributeValue(null, "ref"));
                    	bean.addProperty( property );
                    } // end if
                    break;
                case END_ELEMENT:
                    if (BEANS_ELEMENT.equals(reader.getName())) {
                    	//System.out.println("Spring TypeLoader - finished read of context file");
                        completed = true;
                        break;
                    } // end if
                } // end switch
            } // end while

        } catch (IOException e) {
            throw new ContributionReadException(e);
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
        
        /* At this point, the complete application-context.xml file has been read and its contents  */
        /* stored in the lists of beans, services, references.  These are now used to generate      */
        /* the implied componentType for the application context								    */
        generateComponentType( implementation, beans, services, references );
        
        return;
    } // end method loadFromXML
    
    /**
     * Generates the Spring implementation component type from the configuration contained in the
     * lists of beans, services and references derived from the application context
     */
    private void generateComponentType( SpringImplementation implementation,
    		                                     List<SpringBeanElement> beans,
    		                                     List<SpringSCAServiceElement> services,
    		                                     List<SpringSCAReferenceElement> references )
    	throws ContributionReadException {
    	/*
    	 * Each service becomes a service in the component type
    	 * Each reference becomes a reference in the component type
    	 * Each bean becomes a service, unless it is the target of a service element or unless it
    	 * is the target of a bean reference property
    	 * Each bean property which is a reference not pointing at another bean in the 
    	 * application context becomes a reference unless it is pointing at one of the references
    	 */
    	
    	ComponentType componentType = implementation.getComponentType();
    	
    	try {
	    	// Deal with the services first....												
	    	Iterator<SpringSCAServiceElement> its = services.iterator(); 
	    	while( its.hasNext() ) {
	    		SpringSCAServiceElement serviceElement = its.next();
	    		Class<?> interfaze = cl.loadClass( serviceElement.getType() );
	    		Service theService = createService( interfaze, serviceElement.getName() );
	    		componentType.getServices().add( theService );
	    		// Add this service to the Service / Bean map
	    		String beanName = serviceElement.getTarget();
	    		for( SpringBeanElement beanElement : beans ) {
	    			if( beanName.equals( beanElement.getId() ) ) {
	    				implementation.setBeanForService( theService, beanElement );
	    			}
	    		} // end for
	    	} // end while
	    	
	    	// Next handle the references
	    	Iterator<SpringSCAReferenceElement> itr = references.iterator(); 
	    	while( itr.hasNext() ) {
	    		SpringSCAReferenceElement referenceElement = itr.next();
	    		Class<?> interfaze = cl.loadClass( referenceElement.getType() );
	    		Reference theReference = createReference( interfaze, referenceElement.getName() );
	    		componentType.getReferences().add( theReference );
	    	} // end while
	    	
	    	// Finally deal with the beans
	    	Iterator<SpringBeanElement> itb;
	    	// If there are no explicit service elements, then expose all the beans
	    	if( services.isEmpty() ) {
	    		itb = beans.iterator();
	    		// Loop through all the beans found
	    		while( itb.hasNext() ) {
	    			SpringBeanElement beanElement = itb.next();
	    			// Load the Spring bean class
	    			Class<?> beanClass = cl.loadClass( beanElement.getClassName() );
	    			// Introspect the bean 
	    			ComponentType beanComponentType = assemblyFactory.createComponentType();
	    			beanIntrospector.introspectBean( beanClass, beanComponentType );
	    			// Get the service interface defined by this Spring Bean and add to
	    			// the component type of the Spring Assembly
	    			List<Service> beanServices = beanComponentType.getServices();
	    			componentType.getServices().addAll( beanServices );
	    			// Add these services to the Service / Bean map
	    			for( Service beanService : beanServices ) {
	    			    implementation.setBeanForService( beanService, beanElement );
	    			}
	    		} // end while
	    	} // end if
	    	// Now check to see if there are any more references from beans that are not satisfied
	    	itb = beans.iterator();
	    	while( itb.hasNext() ) {
	    		SpringBeanElement beanElement = itb.next();
	    		boolean unresolvedProperties = false;
	    		if( !beanElement.getProperties().isEmpty() ) {
	    			// Scan through the properties
	    			Iterator<SpringPropertyElement> itp = beanElement.getProperties().iterator();
	    			while( itp.hasNext() ) {
	    				SpringPropertyElement propertyElement = itp.next();
	    				if( propertyRefUnresolved( propertyElement.getRef(), beans, references ) ) {
	    					// This means an unresolved reference from the spring bean...
	    					unresolvedProperties = true;
	    				} // end if
	    			} // end while 
	    			// If there are unresolved properties, then find which ones are references
	    			if( unresolvedProperties ) {
		    			Class<?> beanClass = cl.loadClass( beanElement.getClassName() );
		    			// Introspect the bean 
		    			ComponentType beanComponentType = assemblyFactory.createComponentType();
		    			beanIntrospector.introspectBean( beanClass, beanComponentType );
		    			// Get the references by this Spring Bean and add the unresolved ones to
		    			// the component type of the Spring Assembly
		    			List<Reference> beanReferences = beanComponentType.getReferences();
		    			itp = beanElement.getProperties().iterator();
		    			while( itp.hasNext() ) {
		    				SpringPropertyElement propertyElement = itp.next();
		    				if( propertyRefUnresolved( propertyElement.getRef(), beans, references ) ) {
		    					// This means an unresolved reference from the spring bean...add it to
		    					// the references for the Spring application context
		    					for ( Reference reference : beanReferences ) {
		    						if( propertyElement.getName().equals(reference.getName()) ) {
		    							// The name of the reference in this case is the string in
		    							// the @ref attribute of the Spring property element, NOT the
		    							// name of the field in the Spring bean....
		    							reference.setName(propertyElement.getRef());
		    							componentType.getReferences().add( reference );
		    						} // end if
		    					} // end for
		    				} // end if
		    			} // end while 
	    			} // end if
	    		} // end if
	    		
	    	} // end while
		
    	} catch ( ClassNotFoundException e ) {
    		// Means that either an interface class or a bean was not found
    		throw new ContributionReadException( e );
    	} catch ( InvalidInterfaceException e ) {
    		throw new ContributionReadException( e );
    	} catch ( ContributionResolveException e ) {
    		
    	} // end try
    	
    	// If we get here, the Spring assembly component type is resolved
    	componentType.setUnresolved( false );
    	implementation.setComponentType( componentType );
    	return;
    } // end method generateComponentType
    
    /*
     * Determines whether a reference attribute of a Spring property element is resolved either
     * by a bean in the application context or by an SCA reference element
     * @param ref - a String containing the name of the reference - may be null
     * @param beans - a List of SpringBean elements
     * @param references - a List of SCA reference elements
     * @return true if the property is not resolved, false if it is resolved
     */
    private boolean propertyRefUnresolved( String ref, 
    									   List<SpringBeanElement> beans, 
    									   List<SpringSCAReferenceElement> references ) {
    	boolean unresolved = true;
    	
    	if( ref != null ) {
    		// Scan over the beans looking for a match
	    	Iterator<SpringBeanElement> itb = beans.iterator();
    		while( itb.hasNext() ) {
    			SpringBeanElement beanElement = itb.next();
    			// Does the bean name match the ref?
    			if( ref.equals(beanElement.getId()) ) { 
    				unresolved = false;
    				break;
    			} // end if
    		} // end while
	    	// Scan over the SCA reference elements looking for a match
    		if( unresolved ) {
    			Iterator<SpringSCAReferenceElement> itr = references.iterator();
    			while( itr.hasNext() ) {
    				SpringSCAReferenceElement referenceElement = itr.next();
    				if( ref.equals(referenceElement.getName()) ) {
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
     * Gets hold of the application-context.xml file as a Spring resource
     * @param locationAttr - the location attribute from the <implementation.spring../> element
     * @param cl - the classloader for the Spring implementation
     */
    protected Resource getApplicationContextResource( String locationAttr, ClassLoader cl ) 
    	throws ContributionReadException {
        File manifestFile = null;
        File appXmlFile;
        File locationFile = new File(locationAttr);
        
        if (!locationFile.exists()) {
            // FIXME hack
            URL url = cl.getResource(locationAttr);
            if (url != null) {
                return new UrlResource(url);
            }
            throw new ContributionReadException("SpringXMLLoader getApplicationContextResource: "
            		+ "unable to find resource file " + locationFile.toString());
        }

        if (locationFile.isDirectory()) {
            try {
                manifestFile = new File(locationFile, "META-INF/MANIFEST.MF");
                if (manifestFile.exists()) {
                    Manifest mf = new Manifest(new FileInputStream(manifestFile));
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                        appXmlFile = new File(locationFile, appCtxPath);
                        if (appXmlFile.exists()) {
                            return new UrlResource(appXmlFile.toURL());
                        }
                    }
                }
                // no manifest-specified Spring context, use default
                appXmlFile = new File(locationFile, APPLICATION_CONTEXT);
                if (appXmlFile.exists()) {
                    return new UrlResource(appXmlFile.toURL());
                }
            } catch (IOException e) {
                throw new ContributionReadException("Error reading manifest " + manifestFile);
            }
        } else {
            try {
                JarFile jf = new JarFile(locationFile);
                JarEntry je;
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                        je = jf.getJarEntry(appCtxPath);
                        if (je != null) {
                            // TODO return a Spring specific Resouce type for jars
                            return new UrlResource(new URL("jar:" + locationFile.toURL() + "!/" + appCtxPath));
                        }
                    }
                }
                je = jf.getJarEntry(APPLICATION_CONTEXT);
                if (je != null) {
                    return new UrlResource(new URL("jar:" + locationFile.toURI().toURL() + "!" + APPLICATION_CONTEXT));
                }
            } catch (IOException e) {
                // bad archive
                // TODO: create a more appropriate exception type
                throw new ContributionReadException("SpringXMLLoader getApplicationContextResource: "
                		+ " IO exception reading context file.", e);
            }
        }

        throw new ContributionReadException("SpringXMLLoader getApplicationContextResource: "
        		+ APPLICATION_CONTEXT + "not found");
    } // end method getApplicationContextResource
    
    /**
     * Creates a Service for the component type based on its name and Java interface
     */
    public Service createService( Class<?> interfaze, String name ) 
    	throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        // Set the name for the service
        service.setName( name );

        // Set the call interface and, if present, the callback interface
        JavaInterface callInterface = interfaceIntrospector.introspect(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = interfaceIntrospector.introspect(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    } // end method createService
    
    /**
     * Creates a Reference for the component type based on its name and Java interface
     */
    private org.apache.tuscany.sca.assembly.Reference createReference( Class<?> interfaze, String name ) 
    	throws InvalidInterfaceException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        
        // Set the name of the reference to the supplied name and the multiplicity of the reference
        // to 1..1 - for Spring implementations, this is the only multiplicity supported
        reference.setName(name);
        reference.setMultiplicity(Multiplicity.ONE_ONE);
        
        // Set the call interface and, if present, the callback interface
        JavaInterface callInterface = interfaceIntrospector.introspect( interfaze );
        reference.getInterfaceContract().setInterface( callInterface );
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = 
            	interfaceIntrospector.introspect(callInterface.getCallbackClass());
            reference.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
    
        return reference;
    }
} // end class SpringXMLComponentTypeLoader