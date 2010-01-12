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

package org.apache.tuscany.sca.policy.wspolicy.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.neethi.PolicyEngine;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.common.xml.stax.reader.XMLDocumentStreamReader;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.policy.wspolicy.WSPolicy;

/**
 * Processor for handling xml models of PolicySet definitions
 *
 * @version $Rev$ $Date$
 */
public class WSPolicyProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<WSPolicy> {
    
    protected StAXArtifactProcessor<Object> extensionProcessor;

    public WSPolicyProcessor(ExtensionPointRegistry registry) {
    }

    public QName getArtifactType() {
        return WSPolicy.WS_POLICY_QNAME;
    }

    public Class<WSPolicy> getModelType() {
        return WSPolicy.class;
    }

    public WSPolicy read(XMLStreamReader reader, ProcessorContext context)
        throws ContributionReadException, XMLStreamException {
        org.apache.neethi.Policy neethiPolicy = null;
        XMLDocumentStreamReader doc = new XMLDocumentStreamReader(reader);
        StAXOMBuilder builder = new StAXOMBuilder(doc);
        OMElement element = builder.getDocumentElement();
        neethiPolicy = PolicyEngine.getPolicy(element);
        
        WSPolicy wsPolicy = new WSPolicy();
        wsPolicy.setNeethiPolicy(neethiPolicy);
        
        // read policy assertions        
        
        return wsPolicy;
    }

    public void write(WSPolicy wsPolicy, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        // Write an <sca:policySet>
        writer.writeStartElement(WSPolicy.WS_POLICY_NS, WSPolicy.WS_POLICY);

        if (wsPolicy != null) {
            wsPolicy.getNeethiPolicy().serialize(writer);
        }

        writer.writeEndElement();
    }

    public void resolve(WSPolicy wsPolicy, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        
        // resolve policy assertions
    }

}
