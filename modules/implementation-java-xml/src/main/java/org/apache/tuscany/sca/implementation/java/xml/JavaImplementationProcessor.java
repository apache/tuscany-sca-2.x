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

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospector;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class JavaImplementationProcessor implements
    StAXArtifactProcessor<JavaImplementation>, JavaImplementationConstants {

    private JavaImplementationFactory javaFactory;
    private JavaClassIntrospector introspector;
    private AssemblyFactory assemblyFactory;
    private PolicyFactory policyFactory;

    public JavaImplementationProcessor(AssemblyFactory assemblyFactory,
                                       PolicyFactory policyFactory,
                                       JavaImplementationFactory javaFactory,
                                       JavaClassIntrospector introspector) {
        this.assemblyFactory = assemblyFactory;
        this.policyFactory = policyFactory;
        this.javaFactory = javaFactory;
        this.introspector = introspector;
    }

    public JavaImplementation read(XMLStreamReader reader) throws ContributionReadException {

        try {

            // Read an <implementation.java>
            JavaImplementation javaImplementation = javaFactory.createJavaImplementation();
            javaImplementation.setUnresolved(true);
            javaImplementation.setName(reader.getAttributeValue(null, CLASS));

            // Read policies
            readPolicies(javaImplementation, reader);

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
            introspector.introspect(javaImplementation.getJavaClass(), javaImplementation);
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

    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    private void readPolicies(PolicySetAttachPoint attachPoint, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                requiredIntents.add(intent);
            }
        }

        value = reader.getAttributeValue(null, Constants.POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                policySets.add(policySet);
            }
        }
    }
    
    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
    private QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

}
