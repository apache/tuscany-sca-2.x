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

package org.apache.tuscany.sca.binding.jms.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * A processor to read the XML that describes the JMS binding...
 * 
 * <binding.jms correlationScheme="string"?
 *              initialContextFactory="xs:anyURI"?
 *              jndiURL="xs:anyURI"?
 *              requestConnection="QName"?
 *              responseConnection="QName"?
 *              operationProperties="QName"?
 *              ...>
 * 
 *     <destination name="xs:anyURI" type="string"? create="string"?>
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *     </destination>?
 * 
 *     <connectionFactory name="xs:anyURI" create="string"?>
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *     </connectionFactory>?
 * 
 *     <activationSpec name="xs:anyURI" create="string"?>
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *     </activationSpec>?
 * 
 *     <response>
 *         <destination name="xs:anyURI" type="string"? create="string"?>
 *             <property name="NMTOKEN" type="NMTOKEN">*
 *         </destination>?
 * 
 *         <connectionFactory name="xs:anyURI" create="string"?>
 *             <property name="NMTOKEN" type="NMTOKEN">*
 *         </connectionFactory>?
 * 
 *         <activationSpec name="xs:anyURI" create="string"?>
 *             <property name="NMTOKEN" type="NMTOKEN">*
 *         </activationSpec>?
 *     </response>?
 * 
 *     <resourceAdapter name="NMTOKEN">?
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *     </resourceAdapter>?
 * 
 *     <headers JMSType="string"?
 *              JMSCorrelationId="string"?
 *              JMSDeliveryMode="string"?
 *              JMSTimeToLive="int"?
 *              JMSPriority="string"?>
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *     </headers>?
 * 
 *     <operationProperties name="string" nativeOperation="string"?>
 *         <property name="NMTOKEN" type="NMTOKEN">*
 *         <headers JMSType="string"?
 *                  JMSCorrelationId="string"?
 *                  JMSDeliveryMode="string"?
 *                  JMSTimeToLive="int"?
 *                  JMSPriority="string"?>
 *             <property name="NMTOKEN" type="NMTOKEN">*
 *         </headers>?
 *     </operationProperties>*
 * </binding.jms>
 */

public class JMSBindingProcessor implements StAXArtifactProcessor<JMSBinding>{
    
    private PolicyFactory policyFactory;
    private PolicyAttachPointProcessor policyProcessor;


    public JMSBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
    }

    public QName getArtifactType() {
        return JMSBindingConstants.BINDING_JMS_QNAME;
    }

    public Class<JMSBinding> getModelType() {
        return JMSBinding.class;
    }

    public JMSBinding read(XMLStreamReader reader)  throws ContributionReadException, XMLStreamException {
        JMSBinding jmsBinding = new JMSBinding();
        
        // Read policies
        policyProcessor.readPolicies(jmsBinding, reader);

        // Read binding name
        String name = reader.getAttributeValue(null, "name");
        if (name != null) {
            jmsBinding.setName(name);
        }
        
        // Read binding URI
        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            jmsBinding.setURI(uri);
            System.err.println("JMS Binding doesn't process uri yet");
        }

        // Read correlation scheme
        String correlationScheme = reader.getAttributeValue(null, "correlationScheme");
        if (correlationScheme != null && correlationScheme.length() > 0) {
            if (JMSBindingConstants.VALID_CORRELATION_SCHEMES.contains(correlationScheme.toLowerCase())) {
                jmsBinding.setCorrelationScheme(correlationScheme);
            } else {
                throw new JMSBindingException("invalid correlationScheme: " + correlationScheme);
            }
            System.err.println("JMS Binding doesn't process correlationScheme yet");
        }

        // Read initial context factory
        String initialContextFactory = reader.getAttributeValue(null, "initialContextFactory");
        if (initialContextFactory != null && initialContextFactory.length() > 0) {
            jmsBinding.setInitialContextFactoryName(initialContextFactory);
        }

        // Read jndi URL
        String jndiURL = reader.getAttributeValue(null, "jndiURL");
        if (jndiURL != null && jndiURL.length() > 0) {
            jmsBinding.setJndiURL(jndiURL);
        }
        
        // Read requestConnection
        // TODO
        // Read reponseConnection
        // TODO
        // Read operationProperties
        // TODO
        
        // Read subelements of binding.jms
        boolean endFound = false;
        while (!endFound) {
            switch (reader.next()) {
                case START_ELEMENT:
                    String elementName = reader.getName().getLocalPart();
                    if ("destination".equals(elementName)) {
                        parseDestination(reader, jmsBinding);
                    } else if ("connectionFactory".equals(elementName)) {
                        parseConnectionFactory(reader, jmsBinding);
                    } else if ("activationSpec".equals(elementName)) {
                        parseActivationSpec(reader, jmsBinding);
                    } else if ("response".equals(elementName)) {
                        parseResponse(reader, jmsBinding);
                    } else if ("resourceAdapter".equals(elementName)) {
                        parseResourceAdapter(reader, jmsBinding);                            
                    } else if ("headers".equals(elementName)) {
                        parseHeaders(reader, jmsBinding);
                    } else if ("operationProperties".equals(elementName)) {
                        parseOperationProperties(reader, jmsBinding);
                    } 
                    reader.next();
                    break;
                case END_ELEMENT:
                    QName x = reader.getName();
                    if (x.equals(JMSBindingConstants.BINDING_JMS_QNAME)) {
                        endFound = true;
                    } else {
                        throw new RuntimeException("Incomplete binding.jms definition found unexpected element " + x.toString());
                    }
            }
        }
        
        jmsBinding.validate();           
        
        return jmsBinding;
    }
    
    public void resolve(JMSBinding model, ModelResolver resolver) throws ContributionResolveException {
    }    

    public void write(JMSBinding rmiBinding, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        // Write a <binding.jms>
        writer.writeStartElement(Constants.SCA10_NS, JMSBindingConstants.BINDING_JMS);

        // FIXME Implement
        
        writer.writeEndElement();
    }


    private void parseDestination(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setDestinationName(name);
        }
        
        String type = reader.getAttributeValue(null, "type");
        if (type != null && type.length() > 0) {
            if (JMSBindingConstants.DESTINATION_TYPE_QUEUE.equalsIgnoreCase(type)) {
                jmsBinding.setDestinationType(JMSBindingConstants.DESTINATION_TYPE_QUEUE);
            } else if (JMSBindingConstants.DESTINATION_TYPE_TOPIC.equalsIgnoreCase(type)) {
                jmsBinding.setDestinationType(JMSBindingConstants.DESTINATION_TYPE_TOPIC);
            } else {
                throw new RuntimeException("invalid destination type: " + type);
            }
            System.err.println("JMS Binding doesn't process destination type yet");
        }
        
        String create = reader.getAttributeValue(null, "create");
        if (create != null && create.length() > 0) {
            jmsBinding.setDestinationCreate(create);
        }
    } 
    
    private void parseConnectionFactory(XMLStreamReader reader, JMSBinding jmsBinding) {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setConnectionFactoryName(name);
        } else {
            throw new RuntimeException("missing connectionFactory name");
        }
    }    

    private void parseActivationSpec(XMLStreamReader reader, JMSBinding jmsBinding) {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setActivationSpecName(name);
            System.err.println("JMS Binding doesn't process activationSpec yet");
        } else {
            throw new RuntimeException("missing ActivationSpec name");
        }
    }

    private void parseResponseDestination(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setResponseDestinationName(name);
        }      
        
        String type = reader.getAttributeValue(null, "type");
        if (type != null && type.length() > 0) {
            if (JMSBindingConstants.DESTINATION_TYPE_QUEUE.equalsIgnoreCase(type)) {
                jmsBinding.setResponseDestinationType(JMSBindingConstants.DESTINATION_TYPE_QUEUE);
            } else if (JMSBindingConstants.DESTINATION_TYPE_TOPIC.equalsIgnoreCase(type)) {
                jmsBinding.setResponseDestinationType(JMSBindingConstants.DESTINATION_TYPE_TOPIC);
            } else {
                throw new RuntimeException("invalid response destination type: " + type);
            }
            System.err.println("JMS Binding doesn't process response destination type yet");
        }
        
        String create = reader.getAttributeValue(null, "create");
        if (create != null && create.length() > 0) {
            jmsBinding.setResponseDestinationCreate(create);
        }
    } 
    
    private void parseResponseConnectionFactory(XMLStreamReader reader, JMSBinding jmsBinding) {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setResponseConnectionFactoryName(name);
            System.err.println("JMS Binding doesn't process response connectionFactory yet");
        } else {
            throw new RuntimeException("missing response connectionFactory name");
        }
    }    

    private void parseResponseActivationSpec(XMLStreamReader reader, JMSBinding jmsBinding) {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setResponseActivationSpecName(name);
            System.err.println("JMS Binding doesn't process response activationSpec yet");
        } else {
            throw new RuntimeException("missing response ActivationSpec name");
        }
    }
    
    private void parseResponse(XMLStreamReader reader, JMSBinding jmsBinding)
      throws XMLStreamException {
        // Read subelements of response
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    String elementName = reader.getName().getLocalPart();
                    if ("destination".equals(elementName)) {
                        parseResponseDestination(reader, jmsBinding);
                    } else if ("connectionFactory".equals(elementName)) {
                        parseResponseConnectionFactory(reader, jmsBinding);
                    } else if ("activationSpec".equals(elementName)) {
                        parseResponseActivationSpec(reader, jmsBinding);
                    } 
                    reader.next();
                    break;
                case END_ELEMENT:
                    QName x = reader.getName();
                    if (x.getLocalPart().equals("response")) {
                        return;
                    } else {
                        throw new RuntimeException("Incomplete binding.jms/response definition found unexpected element " + x.toString());
                    }
            }
        }
    }

    private void parseResourceAdapter(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        System.err.println("JMS Binding doesn't process resourceAdapter yet");
    }
    
    private void parseHeaders(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        System.err.println("JMS Binding doesn't process headers yet");
    }    

    private void parseOperationProperties(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        System.err.println("JMS Binding doesn't process operationProperties yet");
    }

}
