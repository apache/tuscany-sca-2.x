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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.neethi.All;
import org.apache.neethi.Constants;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.PolicyComponent;
import org.apache.neethi.PolicyEngine;
import org.apache.neethi.PolicyOperator;
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
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.policy.wspolicy.WSPolicy;

/**
 * Processor for handling xml models of PolicySet definitions
 *
 * @version $Rev$ $Date$
 */
public class WSPolicyProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<WSPolicy> {
    
    protected ExtensionPointRegistry registry;
    protected StAXArtifactProcessor<Object> extensionProcessor;
    protected StAXAttributeProcessor<Object> extensionAttributeProcessor;
    protected XMLInputFactory inputFactory;
    protected XMLOutputFactory outputFactory;

    public WSPolicyProcessor(ExtensionPointRegistry registry,
                             StAXArtifactProcessor extensionProcessor,
                             StAXAttributeProcessor extensionAttributeProcessor) {
        this.registry = registry;
        this.extensionProcessor = extensionProcessor;
        this.extensionAttributeProcessor = extensionAttributeProcessor;
        
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        this.outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
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
        readPolicyAssertions(wsPolicy,neethiPolicy, context);
        
        return wsPolicy;
    }
    
    private void readPolicyAssertions(WSPolicy wsPolicy, PolicyComponent policyComponent, ProcessorContext context){
        
        // recurse into the policy alternatives
        // TODO - lots of todos here as this just walks down the neethi hierarchy
        //        looking for assertions to drive Tuscany processors without
        //        regard to the policy alternatives. Undecided about whether to 
        //        commit to prepresenting this hierarchy in Tuscany or whether
        //        to rely on neethi
        if (policyComponent.getType() != Constants.TYPE_ASSERTION){
            PolicyOperator policyOperator = (PolicyOperator)policyComponent;
            for(Object childComponent : policyOperator.getPolicyComponents()){
                // TODO - create assertion hierarchy in wsPolicy model
                //        how we do this depends on if we continue to use neethi
                readPolicyAssertions(wsPolicy, (PolicyComponent)childComponent, context);
            }
        } else {
            try {
                // TODO - not sure we should keep the neethi model but hack for the
                //        time being to get Tuscany processors to process the OMElements
                //        within the neethi model
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
                
                policyComponent.serialize(writer);
                
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

                Object tuscanyAssertion = extensionProcessor.read(reader, context);
                
                if (tuscanyAssertion != null) {
                    wsPolicy.getPolicyAssertions().add(tuscanyAssertion);
                }
            } catch (Exception ex) {
                // TODO - report the error properly
                ex.printStackTrace();
            }
        }
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
