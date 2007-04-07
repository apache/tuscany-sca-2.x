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

package org.apache.tuscany.implementation.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class JavaImplementationProcessor implements StAXArtifactProcessor<JavaImplementation>, JavaImplementationConstants {

    private JavaImplementationFactory javaFactory;

    public JavaImplementationProcessor(JavaImplementationFactory javaFactory) {
        this.javaFactory = javaFactory;
    }

    public JavaImplementationProcessor() {
        this(new DefaultJavaImplementationFactory(new DefaultAssemblyFactory()));
    }

    public JavaImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {
            
            // Read an <interface.java>
            JavaImplementation javaImplementation = javaFactory.createJavaImplementation();
            javaImplementation.setUnresolved(true);
            javaImplementation.setName(reader.getAttributeValue(null, CLASS));
    
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_JAVA_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return javaImplementation;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(JavaImplementation javaImplementation, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <interface.java>
            writer.writeStartElement(Constants.SCA10_NS, IMPLEMENTATION_JAVA);
            if (javaImplementation.getName() != null) {
                writer.writeAttribute(CLASS, javaImplementation.getName());
            }
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(JavaImplementation model, ArtifactResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }
    
    public void wire(JavaImplementation model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public QName getArtifactType() {
        return IMPLEMENTATION_JAVA_QNAME;
    }
    
    public Class<JavaImplementation> getModelType() {
        return JavaImplementation.class;
    }
}
