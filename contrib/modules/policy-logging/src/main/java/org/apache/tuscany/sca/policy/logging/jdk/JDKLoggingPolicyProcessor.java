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
package org.apache.tuscany.sca.policy.logging.jdk;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 *
 * @version $Rev$ $Date$
 */
public class JDKLoggingPolicyProcessor implements StAXArtifactProcessor<JDKLoggingPolicy> {
    private static final QName JDK_LOGGING_POLICY_QNAME = new QName(Constants.SCA10_TUSCANY_NS, "jdkLogger");
    private static final String LOG_LEVEL = "logLevel";
    private static final String RESOURCE_BUNDLE = "resourceBundle";
    private static final String USE_PARENT_HANDLERS = "useParentHandlers";
    private static final String TUSACNY_NS = "http://tuscany.apache.org/xmlns/sca/1.0";
        
    public QName getArtifactType() {
        return JDK_LOGGING_POLICY_QNAME;
    }
    
    public JDKLoggingPolicyProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    
    public JDKLoggingPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        JDKLoggingPolicy policy = new JDKLoggingPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( name.equals(JDK_LOGGING_POLICY_QNAME) ) {
                        String loggerName = reader.getAttributeValue(null, Constants.NAME);
                        policy.setLoggerName(loggerName);
                    } else if ( LOG_LEVEL.equals(name.getLocalPart()) ) {
                        policy.setLogLevel(Level.parse(reader.getElementText()));
                    } else if ( RESOURCE_BUNDLE.equals(name.getLocalPart()) ) {
                        policy.setResourceBundleName(reader.getElementText());
                    } else if ( USE_PARENT_HANDLERS.equals(name.getLocalPart()) ) {
                        policy.setUseParentHandlers(Boolean.getBoolean(reader.getElementText()));
                    }
                    break;
                }
            }
            
            if ( event == END_ELEMENT ) {
                if ( JDK_LOGGING_POLICY_QNAME.equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
         
        return policy;
    }

    public void write(JDKLoggingPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
                                                        XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 JDK_LOGGING_POLICY_QNAME.getLocalPart(),
                                 JDK_LOGGING_POLICY_QNAME.getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA10_TUSCANY_NS);
        
        if (policy.getLoggerName() != null) {
            writer.writeAttribute(Constants.NAME, policy.getLoggerName());
        }
        if ( policy.getLogLevel() != null ) {
            writer.writeStartElement(prefix, 
                                     LOG_LEVEL,
                                     JDK_LOGGING_POLICY_QNAME.getNamespaceURI());
            writer.writeCharacters(policy.getLogLevel().getLocalizedName());
            writer.writeEndElement();
        }
        
        if ( policy.getResourceBundleName() != null ) {
            writer.writeStartElement(prefix,
                                     RESOURCE_BUNDLE,
                                     JDK_LOGGING_POLICY_QNAME.getNamespaceURI());
            writer.writeCharacters(policy.getResourceBundleName());
            writer.writeEndElement();
        }
        
        writer.writeEndElement();
    }

    public Class<JDKLoggingPolicy> getModelType() {
        return JDKLoggingPolicy.class;
    }

    public void resolve(JDKLoggingPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
}
