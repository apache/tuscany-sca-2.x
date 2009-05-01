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

package org.apache.tuscany.sca.implementation.spring.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.assembly.xml.PolicySubjectProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.spring.SpringImplementation;
import org.apache.tuscany.sca.implementation.spring.introspect.SpringXMLComponentTypeLoader;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * SpringArtifactProcessor is responsible for processing the XML of an <implementation.spring.../>
 * element in an SCA SCDL file.
 * 
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $ 
 */
public class SpringImplementationProcessor implements StAXArtifactProcessor<SpringImplementation> {

    private static final String LOCATION = "location";
    private static final String IMPLEMENTATION_SPRING = "implementation.spring";
    private static final QName IMPLEMENTATION_SPRING_QNAME = new QName(Constants.SCA11_NS, IMPLEMENTATION_SPRING);
    private static final String MSG_LOCATION_MISSING = "Reading implementation.spring - location attribute missing";

    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private PolicyFactory policyFactory;
    private PolicySubjectProcessor policyProcessor;
    private Monitor monitor;
    
    public SpringImplementationProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.policyProcessor = new PolicySubjectProcessor(policyFactory);
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

    /*
     * Read the XML and parse out the attributes.
     * 
     * <implementation.spring.../> has a single required attribute:
     * "location" - which is the target URI of of an archive file or a directory that contains the Spring
     * application context files.
     * If the resource identified by the location attribute is an archive file, then the file 
     * META-INF/MANIFEST.MF is read from the archive. 
     * If the location URI identifies a directory, then META-INF/MANIFEST.MF must exist
     * underneath that directory. 
     * If the manifest file contains a header "Spring-Context" of the format:
     *    Spring-Context ::= path ( ';' path )*
     *    
     * Where path is a relative path with respect to the location URI, then the set of paths 
     * specified in the header identify the context configuration files. 
     * If there is no MANIFEST.MF file or no Spring-Context header within that file, 
     * then the default behaviour is to build an application context using all the *.xml files 
     * in the METAINF/spring directory.
     */
    public SpringImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        // Create the Spring implementation
        SpringImplementation springImplementation = null;

        // Read the location attribute for the spring implementation
        String springLocation = reader.getAttributeValue(null, LOCATION);
        if (springLocation != null) {
        	springImplementation = new SpringImplementation();
        	springImplementation.setLocation(springLocation);
        	springImplementation.setUnresolved(true);
            processComponentType(springImplementation);
        } else {
        	error("LocationAttributeMissing", reader);
            //throw new ContributionReadException(MSG_LOCATION_MISSING);
        }
        
        // Read policies
        policyProcessor.readPolicies(springImplementation, reader);

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_SPRING_QNAME.equals(reader.getName())) {
                break;
            }
        } // end while

        return springImplementation;
    } // end read

    /*
     * Handles the component type for the Spring implementation
     * @param springImplementation - a Spring implementation.  The component type information
     * is created for this implementation
     *  
     */
    private void processComponentType(SpringImplementation springImplementation) {

        // Create a ComponentType and mark it unresolved
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        springImplementation.setComponentType(componentType);
    } // end processComponentType

    /*
     * Write out the XML representation of the Spring implementation
     * <implementation.spring location="..." />
     */
    public void write(SpringImplementation springImplementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        // Write <implementation.spring>
        policyProcessor.writePolicyPrefixes(springImplementation, writer);
        writer.writeStartElement(Constants.SCA11_NS, IMPLEMENTATION_SPRING);
        policyProcessor.writePolicyAttributes(springImplementation, writer);
        
        if (springImplementation.getLocation() != null) {
            writer.writeAttribute(LOCATION, springImplementation.getLocation());
        }

        writer.writeEndElement();

    } // end write

    /**
     * Resolves the Spring implementation - loads the Spring application-context XML and
     * derives the spring implementation componentType from it
     */
    public void resolve(SpringImplementation springImplementation, ModelResolver resolver)
        throws ContributionResolveException {
    	
    	if (springImplementation == null)
    		return;

        /* Load the Spring component type by reading the Spring application context */
        SpringXMLComponentTypeLoader springLoader =
            new SpringXMLComponentTypeLoader(assemblyFactory, javaFactory, policyFactory);
        try {
            // Load the Spring Implementation information from its application context file...
            springLoader.load(springImplementation);
        } catch (ContributionReadException e) {
        	ContributionResolveException ce = new ContributionResolveException(e);
        	error("ContributionResolveException", resolver, ce);
            throw ce;
        }

        ComponentType ct = springImplementation.getComponentType();
        if (ct.isUnresolved()) {
            // If the introspection fails to resolve, try to find a side file...
            ComponentType componentType = resolver.resolveModel(ComponentType.class, ct);
            if (componentType.isUnresolved()) {
            	error("UnableToResolveComponentType", resolver);
                //throw new ContributionResolveException("SpringArtifactProcessor: unable to resolve componentType for Spring component");             
            } else {
                springImplementation.setComponentType(componentType);
                springImplementation.setUnresolved(false);
           }            
   
        } // end if

    } // end method resolve

    public QName getArtifactType() {
        return IMPLEMENTATION_SPRING_QNAME;
    }

    public Class<SpringImplementation> getModelType() {
        return SpringImplementation.class;
    }

} // end class SpringArtifactProcessor
