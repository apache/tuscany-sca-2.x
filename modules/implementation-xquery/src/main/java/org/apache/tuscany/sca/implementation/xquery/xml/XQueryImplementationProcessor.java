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
package org.apache.tuscany.sca.implementation.xquery.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.xquery.XQueryImplementation;
import org.apache.tuscany.sca.implementation.xquery.XQueryImplementationFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Processor for the XQuery implementation type artifact
 * @version $Rev$ $Date$
 */
public class XQueryImplementationProcessor implements StAXArtifactProcessor<XQueryImplementation> {

    private static final String LOCATION = "location";
    private static final String IMPLEMENTATION_XQUERY = "implementation.xquery";
    private static final QName IMPLEMENTATION_XQUERY_QNAME =
        new QName(Constants.SCA10_TUSCANY_NS, IMPLEMENTATION_XQUERY);
    private static final String MSG_LOCATION_MISSING = "Reading implementation.xquery - location attribute missing";

    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private ContributionFactory contributionFactory;
    private Monitor monitor;

    public XQueryImplementationProcessor(ModelFactoryExtensionPoint modelFactoryExtensionPoint, Monitor monitor) {
        assemblyFactory = modelFactoryExtensionPoint.getFactory(AssemblyFactory.class);
        javaFactory = modelFactoryExtensionPoint.getFactory(JavaInterfaceFactory.class);
        contributionFactory = modelFactoryExtensionPoint.getFactory(ContributionFactory.class);
        this.monitor = monitor;
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
	        Problem problem = new ProblemImpl(this.getClass().getName(), "impl-xquery-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	 }
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_XQUERY_QNAME;
    }

    /**
     * Reads  from the stream and sets the location attribute of the implementation correspondingly
     */
    public XQueryImplementation read(XMLStreamReader reader)
        throws ContributionReadException, XMLStreamException {

        /* Read the location attribute for the XQuery implementation  */
        String xqueryLocation = reader.getAttributeValue(null, LOCATION);
        if (xqueryLocation == null) {
        	error("LocationAttributeMissing", reader);
            throw new ContributionReadException(MSG_LOCATION_MISSING);
        }
        /* Create the XQuery implementation and set the location into it */
        XQueryImplementation xqueryImplementation =
            XQueryImplementationFactory.INSTANCE.createXQueryImplementation();
        xqueryImplementation.setLocation(xqueryLocation);

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_XQUERY_QNAME.equals(reader.getName())) {
                break;
            }
        } // end while

        xqueryImplementation.setUnresolved(true);

        return xqueryImplementation;
    }

    public void write(XQueryImplementation xqueryImplementation, XMLStreamWriter writer)
        throws ContributionWriteException, XMLStreamException {
        
        writer.writeStartElement(Constants.SCA10_TUSCANY_NS, IMPLEMENTATION_XQUERY);
        if (xqueryImplementation.getLocation() != null) {
            writer.writeAttribute(LOCATION, xqueryImplementation.getLocation());
        }
        writer.writeEndElement();

    }

    public Class<XQueryImplementation> getModelType() {
        return XQueryImplementation.class;
    }

    /**
     * Resolves the implementation: its services and references, by invoking the XQuery
     * introspector
     */
    public void resolve(XQueryImplementation xqueryImplementation, ModelResolver resolver)
        throws ContributionResolveException {

        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI(xqueryImplementation.getLocation());
    	artifact = resolver.resolveModel(Artifact.class, artifact);
    	if (artifact.getLocation() == null) {
    		error("CouldNotLocateFile", resolver, xqueryImplementation.getLocation());
            throw new ContributionResolveException("Could not locate file: " + xqueryImplementation.getLocation());
        }
    	xqueryImplementation.setLocationURL(artifact.getLocation());

        XQueryIntrospector introspector = new XQueryIntrospector(assemblyFactory, javaFactory);

        boolean success = introspector.introspect(xqueryImplementation, resolver);

        if (success) {
            xqueryImplementation.setUnresolved(false);
        }
    }

}
