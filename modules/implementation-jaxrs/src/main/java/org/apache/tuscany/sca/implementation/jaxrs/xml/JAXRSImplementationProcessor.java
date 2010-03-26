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
package org.apache.tuscany.sca.implementation.jaxrs.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementation;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementationFactory;

/**
 * Implements a StAX artifact processor for Web implementations.
 */
public class JAXRSImplementationProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<JAXRSImplementation> {
    private static final QName IMPLEMENTATION_JAXRS = JAXRSImplementation.TYPE;

    private JAXRSImplementationFactory implementationFactory;

    public JAXRSImplementationProcessor(ExtensionPointRegistry extensionPoints) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.implementationFactory = modelFactories.getFactory(JAXRSImplementationFactory.class);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_JAXRS;
    }

    public Class<JAXRSImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return JAXRSImplementation.class;
    }

    public JAXRSImplementation read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException,
        XMLStreamException {

        // Read an <implementation.web> element
        JAXRSImplementation implementation = implementationFactory.createJAXRSImplementation();
        implementation.setUnresolved(true);

        String application = reader.getAttributeValue(null, "application");
        if (application != null) {
            implementation.setApplication(application);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_JAXRS.equals(reader.getName())) {
                break;
            }
        }

        return implementation;
    }

    public void resolve(JAXRSImplementation implementation, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {

        ClassReference classReference = new ClassReference(implementation.getApplication());
        classReference = resolver.resolveModel(ClassReference.class, classReference, context);
        implementation.setApplicationClass(classReference.getJavaClass());
        implementation.setUnresolved(false);
    }

    public void write(JAXRSImplementation implementation, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        // Write <implementation.jaxrs>
        writeStart(writer,
                   IMPLEMENTATION_JAXRS.getNamespaceURI(),
                   IMPLEMENTATION_JAXRS.getLocalPart(),
                   new XAttr("application", implementation.getApplication()));

        writeEnd(writer);
    }
}
