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
package org.apache.tuscany.sca.implementation.widget;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 *
 * Implements a StAX artifact processor for Widget implementations.
 *
 * @version $Rev$ $Date$
 */
public class WidgetImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<WidgetImplementation> {
    private static final QName IMPLEMENTATION_WIDGET = new QName(Constants.SCA10_TUSCANY_NS, "implementation.widget");

    private AssemblyFactory assemblyFactory;
    private ContributionFactory contributionFactory;
    private WidgetImplementationFactory implementationFactory;
    private Monitor monitor;

    public WidgetImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    	assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        implementationFactory = modelFactories.getFactory(WidgetImplementationFactory.class);
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
	        Problem problem = monitor.createProblem(this.getClass().getName(), "impl-widget-validation-messages", Severity.ERROR, model, message, ex);
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
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-widget-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_WIDGET;
    }

    public Class<WidgetImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return WidgetImplementation.class;
    }

    public WidgetImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        // Read an <implementation.widget> element

        // Create and initialize the resource implementation model
        WidgetImplementation implementation = null;

        // Read the location attribute specifying the location of the resources
        String location = reader.getAttributeValue(null, "location");
        if (location != null) {
            implementation = implementationFactory.createWidgetImplementation();
            implementation.setLocation(location);
            implementation.setUnresolved(true);
        } else {
            error("LocationAttributeMissing", reader);
            //throw new ContributionReadException(MSG_LOCATION_MISSING);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_WIDGET.equals(reader.getName())) {
                break;
            }
        }

        return implementation;
    }

    public void resolve(WidgetImplementation implementation, ModelResolver resolver) throws ContributionResolveException {

    	if (implementation != null) {
    		// Resolve the resource directory location
            Artifact artifact = contributionFactory.createArtifact();
            artifact.setURI(implementation.getLocation());
            Artifact resolved = resolver.resolveModel(Artifact.class, artifact);
            if (resolved.getLocation() != null) {
                try {
                    implementation.setLocationURL(new URL(resolved.getLocation()));

                    //introspect implementation
                    WidgetImplementationIntrospector widgetIntrospector =
                    	new WidgetImplementationIntrospector(assemblyFactory, implementation);
                    widgetIntrospector.introspectImplementation();

                    implementation.setUnresolved(false);
                } catch (IOException e) {
                	ContributionResolveException ce = new ContributionResolveException(e);
                	error("ContributionResolveException", resolver, ce);
                    //throw ce;
                }
            } else {
                error("CouldNotResolveLocation", resolver, implementation.getLocation());
                //throw new ContributionResolveException("Could not resolve implementation.widget location: " + implementation.getLocation());
            }
    	}
    }

    public void write(WidgetImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        // Write <implementation.widget>        
        writeStart(writer, IMPLEMENTATION_WIDGET.getNamespaceURI(), IMPLEMENTATION_WIDGET.getLocalPart());

        if (implementation.getLocation() != null) {
            writer.writeAttribute("location", implementation.getLocation());
        }

        writeEnd(writer);
    }
}
