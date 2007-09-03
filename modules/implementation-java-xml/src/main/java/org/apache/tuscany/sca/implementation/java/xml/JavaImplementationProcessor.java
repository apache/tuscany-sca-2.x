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

package org.apache.tuscany.sca.implementation.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

public class JavaImplementationProcessor implements
    StAXArtifactProcessor<JavaImplementation>, JavaImplementationConstants {

    private JavaImplementationFactory javaFactory;
    private AssemblyFactory assemblyFactory;
    private PolicyFactory policyFactory;
    private PolicyAttachPointProcessor policyProcessor;

    public JavaImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.javaFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
    }

    public JavaImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        // Read an <implementation.java>
        JavaImplementation javaImplementation = javaFactory.createJavaImplementation();
        javaImplementation.setUnresolved(true);
        javaImplementation.setName(reader.getAttributeValue(null, CLASS));

        // Read policies
        policyProcessor.readPolicies(javaImplementation, reader);

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_JAVA_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return javaImplementation;
    }

    public void write(JavaImplementation javaImplementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        // Write an <implementation.java>
        policyProcessor.writePolicyPrefixes(javaImplementation, writer);
        writer.writeStartElement(Constants.SCA10_NS, IMPLEMENTATION_JAVA);
        policyProcessor.writePolicyAttributes(javaImplementation, writer);
        
        if (javaImplementation.getName() != null) {
            writer.writeAttribute(CLASS, javaImplementation.getName());
        }
        
        writer.writeEndElement();
    }

    public void resolve(JavaImplementation javaImplementation, ModelResolver resolver)
        throws ContributionResolveException {

        ClassReference classReference = new ClassReference(javaImplementation.getName());
        classReference = resolver.resolveModel(ClassReference.class, classReference);
        Class javaClass = classReference.getJavaClass();
        if (javaClass == null) {
            throw new ContributionResolveException(new ClassNotFoundException(javaImplementation.getName()));
        }
        javaImplementation.setJavaClass(javaClass);
        javaImplementation.setUnresolved(false);

        try {
            javaFactory.createJavaImplementation(javaImplementation, javaImplementation.getJavaClass());
        } catch (IntrospectionException e) {
            throw new ContributionResolveException(e);
        }

        // FIXME the introspector should always create at least one service
        if (javaImplementation.getServices().isEmpty()) {
            javaImplementation.getServices().add(assemblyFactory.createService());
        }
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_JAVA_QNAME;
    }

    public Class<JavaImplementation> getModelType() {
        return JavaImplementation.class;
    }

}
