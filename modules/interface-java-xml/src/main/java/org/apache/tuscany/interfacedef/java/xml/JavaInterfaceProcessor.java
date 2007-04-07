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

package org.apache.tuscany.interfacedef.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class JavaInterfaceProcessor implements StAXArtifactProcessor<JavaInterface>, JavaConstants {

    private JavaFactory javaFactory;

    public JavaInterfaceProcessor(JavaFactory javaFactory) {
        this.javaFactory = javaFactory;
    }
    
    public JavaInterfaceProcessor() {
        this(new DefaultJavaFactory());
    }

    public JavaInterface read(XMLStreamReader reader) throws ContributionReadException {
        try {
    
            // Read an <interface.java>
            JavaInterface javaInterface = javaFactory.createJavaInterface();
            javaInterface.setUnresolved(true);
            javaInterface.setName(reader.getAttributeValue(null, INTERFACE));
    
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && INTERFACE_JAVA_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return javaInterface;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(JavaInterface javaInterface, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <interface.java>
            writer.writeStartElement(Constants.SCA10_NS, INTERFACE_JAVA);
            if (javaInterface.getName() != null) {
                writer.writeAttribute(INTERFACE, javaInterface.getName());
            }
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(JavaInterface model, ArtifactResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }
    
    public void wire(JavaInterface model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public QName getArtifactType() {
        return INTERFACE_JAVA_QNAME;
    }
    
    public Class<JavaInterface> getModelType() {
        return JavaInterface.class;
    }
}
