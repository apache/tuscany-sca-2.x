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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SCADefinitions;
import org.apache.tuscany.sca.policy.impl.SCADefinitionsImpl;

/**
 * A composite processor.
 * 
 */
public class SCADefinitionsProcessor implements StAXArtifactProcessor<SCADefinitions>, PolicyConstants {
    
    protected PolicyFactory policyFactory;
    protected StAXArtifactProcessor<Object> extensionProcessor;
    /**
     * Construct a new (sca) definitions processor
     * @param assemblyFactory
     * @param policyFactory
     * @param extensionProcessor 
     */
    public SCADefinitionsProcessor(PolicyFactory policyFactory,
                              StAXArtifactProcessor extensionProcessor) {
        this.policyFactory = policyFactory;
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
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
    
    /**
     * Returns the qname value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }
    
    public SCADefinitions read(XMLStreamReader reader) throws ContributionReadException {
        QName name = null;
        SCADefinitions scaDefns = null;
        try {
            // Read the composite document
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT: {
                        if ( SCA_DEFNS_QNAME.equals(name)) {
                            scaDefns = new SCADefinitionsImpl();
                            scaDefns.setTargetNamespace(getQName(reader, TARGET_NAMESPACE ));
                        } else {
                            Object extension = extensionProcessor.read(reader);
                            if (extension != null) {
                                if ( extension instanceof Intent ) {
                                    scaDefns.getPolicyIntents().add((Intent)extension);
                                } else if ( extension instanceof PolicySet ) {
                                    scaDefns.getPolicySets().add((PolicySet)extension);
                                } else if ( extension instanceof BindingType ) {
                                    scaDefns.getBindingTypes().add((BindingType)extension);
                                } else if ( extension instanceof ImplementationType ) {
                                    scaDefns.getImplementationTypes().add((ImplementationType)extension);
                                }
                            }
                            break;
                        }
                    }
    
                    case XMLStreamConstants.CHARACTERS:
                        break;
    
                    case END_ELEMENT:
                        break;
                }
                
                //Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
            return scaDefns;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    /**
     * Write attributes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.write(writer);
        }
    }
    
    /**
     * Start an element.
     * @param uri
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String uri, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartElement(uri, name);
        writeAttributes(writer, attrs);
    }

    /**
     * Start an element.
     * @param writer
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartElement(SCA10_NS, name);
        writeAttributes(writer, attrs);
    }
    
    /**
     * Start a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartDocument();
        writer.setDefaultNamespace(SCA10_NS);
        writeStart(writer, name, attrs);
        writer.writeDefaultNamespace(SCA10_NS);
    }
    
    /**
     * End a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEndDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndDocument();
    }
    
    public void write(SCADefinitions scaDefns, XMLStreamWriter writer) throws ContributionWriteException {

        try {
            writeStartDocument(writer, 
                                   SCA_DEFINITIONS,
                                   new XAttr(TARGET_NAMESPACE, scaDefns.getTargetNamespace())
                );
    
            for (Intent policyIntent : scaDefns.getPolicyIntents()) {
                extensionProcessor.write(policyIntent, writer);
            }
            
            for (PolicySet policySet : scaDefns.getPolicySets()) {
                extensionProcessor.write(policySet, writer);
            }
            
            for (BindingType bindingType : scaDefns.getBindingTypes()) {
                extensionProcessor.write(bindingType, writer);
            }
            
            for (ImplementationType implType : scaDefns.getImplementationTypes()) {
                extensionProcessor.write(implType, writer);
            }

            writeEndDocument(writer);
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(SCADefinitions scaDefns, ModelResolver resolver) throws ContributionResolveException {
        
        for (int count = 0, size = scaDefns.getPolicyIntents().size(); count < size; count++) {
            Intent policyIntent = scaDefns.getPolicyIntents().get(count);
            policyIntent = resolver.resolveModel(Intent.class, policyIntent);
            scaDefns.getPolicyIntents().set(count, policyIntent);
        }
        
        for (int count = 0, size = scaDefns.getPolicySets().size(); count < size; count++) {
            PolicySet policySet = scaDefns.getPolicySets().get(count);
            policySet = resolver.resolveModel(PolicySet.class, policySet);
            scaDefns.getPolicySets().set(count, policySet);
        }
        
        for (int count = 0, size = scaDefns.getBindingTypes().size(); count < size; count++) {
            BindingType bindingType = scaDefns.getBindingTypes().get(count);
            bindingType = resolver.resolveModel(BindingType.class, bindingType);
            scaDefns.getBindingTypes().set(count, bindingType);
        }
        
        for (int count = 0, size = scaDefns.getImplementationTypes().size(); count < size; count++) {
            ImplementationType implType = scaDefns.getImplementationTypes().get(count);
            implType = resolver.resolveModel(ImplementationType.class, implType);
            scaDefns.getImplementationTypes().set(count, implType);
        }
    }

    public QName getArtifactType() {
        return SCA_DEFNS_QNAME;
    }
    
    public Class<SCADefinitions> getModelType() {
        return SCADefinitions.class;
    }
}
