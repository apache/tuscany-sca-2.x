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

package org.apache.tuscany.sca.policy.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;

public class PolicyIntentProcessor implements StAXArtifactProcessor<Intent>, PolicyConstants {

    private PolicyFactory policyFactory;

    public PolicyIntentProcessor(PolicyFactory policyFactory) {
        this.policyFactory = policyFactory;
    }

    public Intent read(XMLStreamReader reader) throws ContributionReadException {
        try {
            // Read an <sca:intent>
            Intent policyIntent = policyFactory.createIntent();
            policyIntent.setName(new QName(reader.getAttributeValue(null, Constants.NAME)));
            
            readRequiredIntents(policyIntent, reader);
            readConstrainedArtifacts(policyIntent, reader);
            
            int event = reader.getEventType();
            QName name = null;
            while (reader.hasNext()) {
                event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT : {
                        name = reader.getName();
                        if (DESCRIPTION.equals(name)) {
                            policyIntent.setDescription(reader.getElementText());
                        }
                        break;
                    }
                }
                if (event == END_ELEMENT && POLICY_INTENT_QNAME.equals(reader.getName())) {
                    break;
                }
                
                //Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
            return policyIntent;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(Intent policyIntent, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <sca:intent>
            writer.writeStartElement(Constants.SCA10_NS, INTENT);
            if (policyIntent.getRequiredIntents() != null && 
                policyIntent.getRequiredIntents().size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (Intent requiredIntents : policyIntent.getRequiredIntents()) {
                    sb.append(requiredIntents.getName());
                    sb.append(" ");
                }
                writer.writeAttribute(Constants.REQUIRES, sb.toString());
            }
            
            if (policyIntent.getConstrains() != null && 
                policyIntent.getConstrains().size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (QName contrainedArtifact : policyIntent.getConstrains()) {
                    sb.append(contrainedArtifact.toString());
                    sb.append(" ");
                }
                writer.writeAttribute(CONSTRAINS, sb.toString());
            } else {
                throw new ContributionWriteException("Contrains attribute missing from " +
                                "Policy Intent Definition" + policyIntent.getName());
            }
            
            if ( policyIntent.getDescription() != null && policyIntent.getDescription().length() > 0) {
                writer.writeStartElement(Constants.SCA10_NS, DESCRIPTION);
                writer.writeCData(policyIntent.getDescription());
                writer.writeEndElement();
            }
            
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    private Intent resolveRequiredIntents(Intent policyIntent, ModelResolver resolver) throws ContributionResolveException {
        boolean isUnresolved = false;
        //FIXME: Need to check for cyclic references first i.e an A requiring B and then B requiring A... 
        if (policyIntent != null && policyIntent.isUnresolved()) {
            //resolve all required intents
            for (Intent requiredIntent : policyIntent.getRequiredIntents()) {
                if ( requiredIntent.isUnresolved() ) {
                    requiredIntent = resolver.resolveModel(Intent.class, requiredIntent);
                    if (!requiredIntent.isUnresolved()) {
                        isUnresolved = true;
                    }
                }
            }
        }
        policyIntent.setUnresolved(isUnresolved);
        
        return policyIntent;
    }
    
    public void resolve(Intent policyIntent, ModelResolver resolver) throws ContributionResolveException {
        resolveRequiredIntents(policyIntent, resolver);
    }
    
    public QName getArtifactType() {
        return POLICY_INTENT_QNAME;
    }
    
    public Class<Intent> getModelType() {
        return Intent.class;
    }
    
    protected void readConstrainedArtifacts(Intent policyIntent, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, CONSTRAINS);
        if (value != null) {
            List<QName> constrainedArtifacts = policyIntent.getConstrains();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                constrainedArtifacts.add(qname);
            }
        }
    }
    
    protected void readRequiredIntents(Intent policyIntent, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = policyIntent.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                requiredIntents.add(intent);
            }
        }
    }
    
    protected QName getQNameValue(XMLStreamReader reader, String value) {
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
