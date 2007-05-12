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

package org.apache.tuscany.sca.interfacedef.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;

public class JavaInterfaceProcessor implements StAXArtifactProcessor<JavaInterfaceContract>, JavaConstants {

    private JavaInterfaceFactory javaFactory;
    private JavaInterfaceIntrospector introspector;

    public JavaInterfaceProcessor(JavaInterfaceFactory javaFactory, JavaInterfaceIntrospector introspector) {
        this.javaFactory = javaFactory;
        this.introspector = introspector;
    }
    
    private JavaInterface createJavaInterface(String interfaceName) {
        JavaInterface javaInterface = javaFactory.createJavaInterface();
        javaInterface.setUnresolved(true);
        javaInterface.setName(interfaceName);
        return javaInterface;
    }

    public JavaInterfaceContract read(XMLStreamReader reader) throws ContributionReadException {
        try {
            // Read an <interface.java>
            JavaInterfaceContract javaInterfaceContract = javaFactory.createJavaInterfaceContract();
            String interfaceName = reader.getAttributeValue(null, INTERFACE);
            if (interfaceName != null) {
                JavaInterface javaInterface = createJavaInterface(interfaceName);
                javaInterfaceContract.setInterface(javaInterface);
            }

            String callbackInterfaceName = reader.getAttributeValue(null, CALLBACK_INTERFACE);
            if (callbackInterfaceName != null) {
                JavaInterface javaCallbackInterface = createJavaInterface(callbackInterfaceName);
                javaInterfaceContract.setCallbackInterface(javaCallbackInterface);
            }
    
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && INTERFACE_JAVA_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return javaInterfaceContract;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(JavaInterfaceContract javaInterfaceContract, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <interface.java>
            writer.writeStartElement(Constants.SCA10_NS, INTERFACE_JAVA);
            JavaInterface javaInterface = (JavaInterface)javaInterfaceContract.getInterface();
            if (javaInterface != null && javaInterface.getName() != null) {
                writer.writeAttribute(INTERFACE, javaInterface.getName());
            }
            JavaInterface javaCallbackInterface = (JavaInterface)javaInterfaceContract.getCallbackInterface();
            if (javaCallbackInterface != null && javaCallbackInterface.getName() != null) {
                writer.writeAttribute(CALLBACK_INTERFACE, javaCallbackInterface.getName());
            }
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    private JavaInterface resolveJavaInterface(JavaInterface javaInterface, ModelResolver resolver) throws ContributionResolveException {
        
        if (javaInterface != null && javaInterface.isUnresolved()) {

            // Resolve the Java interface
            javaInterface = resolver.resolveModel(JavaInterface.class, javaInterface);
            if (javaInterface.isUnresolved()) {

                // If the Java interface has never been resolved yet, do it now
                ClassReference classReference = new ClassReference(javaInterface.getName());
                classReference = resolver.resolveModel(ClassReference.class, classReference);
                Class javaClass = classReference.getJavaClass();
                if (javaClass == null) {
                    throw new ContributionResolveException(new ClassNotFoundException(javaInterface.getName()));
                }
                try {
                        
                    // Introspect the Java interface and populate the interface and
                    // operations
                    javaInterface = introspector.introspect(javaClass);
                
                } catch (InvalidInterfaceException e) {
                    throw new ContributionResolveException(e);
                }

                // Cache the resolved interface
                javaInterface.setUnresolved(false);
                resolver.addModel(javaInterface);
            }
        }
        return javaInterface;
    }
    
    public void resolve(JavaInterfaceContract javaInterfaceContract, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the interface and callback interface
        JavaInterface javaInterface = resolveJavaInterface((JavaInterface)javaInterfaceContract.getInterface(), resolver);
        javaInterfaceContract.setInterface(javaInterface);
        
        JavaInterface javaCallbackInterface = resolveJavaInterface((JavaInterface)javaInterfaceContract.getCallbackInterface(), resolver);
        javaInterfaceContract.setCallbackInterface(javaCallbackInterface);
    }
    
    public QName getArtifactType() {
        return INTERFACE_JAVA_QNAME;
    }
    
    public Class<JavaInterfaceContract> getModelType() {
        return JavaInterfaceContract.class;
    }
}
