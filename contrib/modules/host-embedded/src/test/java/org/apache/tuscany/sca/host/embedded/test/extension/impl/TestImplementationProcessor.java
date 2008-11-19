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
package org.apache.tuscany.sca.host.embedded.test.extension.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.host.embedded.test.extension.TestImplementation;
import org.apache.tuscany.sca.host.embedded.test.extension.TestImplementationFactory;



/**
 * Implements a StAX artifact processor for test implementations.
 *
 * @version $Rev$ $Date$
 */
public class TestImplementationProcessor implements StAXArtifactProcessor<TestImplementation> {
    private static final QName IMPLEMENTATION_TEST = new QName("http://test/extension", "implementation.test");
    
    private TestImplementationFactory testFactory;
    
    public TestImplementationProcessor(TestImplementationFactory testFactory) {
        this.testFactory = testFactory;
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_TEST;
    }

    public Class<TestImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return TestImplementation.class;
    }

    public TestImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.test> element

        // Read the message attribute.
        String message = reader.getAttributeValue(null, "greeting");

        // Create and initialize the test implementation model
        TestImplementation implementation = testFactory.createTestImplementation();
        implementation.setGreeting(message);
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_TEST.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(TestImplementation impl, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(TestImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        writer.writeStartElement(IMPLEMENTATION_TEST.getNamespaceURI(), IMPLEMENTATION_TEST.getLocalPart());
        
        if (implementation.getGreeting() != null) {
            writer.writeAttribute("greeting", implementation.getGreeting());
        }
        
        writer.writeEndElement();
    }
}
