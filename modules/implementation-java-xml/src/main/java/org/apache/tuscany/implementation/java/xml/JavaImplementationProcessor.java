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
import org.apache.tuscany.assembly.impl.ServiceImpl;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspection.IntrospectionRegistry;
import org.apache.tuscany.implementation.java.introspection.impl.IntrospectionRegistryImpl;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class JavaImplementationProcessor implements StAXArtifactProcessor<JavaImplementation>,
    JavaImplementationConstants {

    private JavaImplementationFactory javaFactory;
    private IntrospectionRegistry introspectionRegistry;

    public JavaImplementationProcessor(JavaImplementationFactory javaFactory) {
        this.javaFactory = javaFactory;
        this.introspectionRegistry = new IntrospectionRegistryImpl();
    }

    public JavaImplementationProcessor() {
        this(new DefaultJavaImplementationFactory(new DefaultAssemblyFactory()));
    }

    public JavaImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {

            // Read an <implementation.java>
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

    public void resolve(JavaImplementation javaImplementation, ArtifactResolver resolver) throws ContributionResolveException {
        try {
            Class javaClass = Class.forName(javaImplementation.getName(), true, Thread.currentThread().getContextClassLoader());
            javaImplementation.setJavaClass(javaClass);
            
            //FIXME JavaImplementationDefinition should not be mandatory 
            if (javaImplementation instanceof JavaImplementationDefinition) {
                introspectionRegistry.introspect(javaImplementation.getJavaClass(), (JavaImplementationDefinition)javaImplementation);
                
                //FIXME the introspector should always create at least one service
                if (javaImplementation.getServices().isEmpty()) {
                    javaImplementation.getServices().add(new ServiceImpl());
                }
            }
        } catch (Exception e) {
            throw new ContributionResolveException(e);
        }
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

    /**
     * @param introspectionRegistry the introspectionRegistry to set
     */
    public void setIntrospectionRegistry(IntrospectionRegistry introspectionRegistry) {
        this.introspectionRegistry = introspectionRegistry;
    }

    /**
     * @param javaFactory the javaFactory to set
     */
    public void setJavaFactory(JavaImplementationFactory javaFactory) {
        this.javaFactory = javaFactory;
    }
}
