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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A processor to read the XML that describes the SCA binding.
 *
 * @version $Rev$ $Date$
 */

public class SCABindingProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SCABinding> {
    private static final String NAME = "name";
    private static final String URI = "uri";
    private static final String DELEGATE_BINDING_TYPE = "delegateBindingType";
    private static final String DELEGATE_BINDING_URI = "delegateBindingURI";

    private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    private static final String BINDING_SCA = "binding.sca";
    private static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    private PolicyFactory policyFactory;
    private SCABindingFactory scaBindingFactory;
    private PolicySubjectProcessor policyProcessor;
    private PolicyFactory  intentAttachPointTypeFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;


    public SCABindingProcessor(FactoryExtensionPoint modelFactories, StAXArtifactProcessor<Object> extensionProcessor) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        policyProcessor = new PolicySubjectProcessor(policyFactory);
        this.intentAttachPointTypeFactory = modelFactories.getFactory(PolicyFactory.class);
        this.extensionProcessor = extensionProcessor;
    }

    public QName getArtifactType() {
        return BINDING_SCA_QNAME;
    }

    public Class<SCABinding> getModelType() {
        return SCABinding.class;
    }

    public SCABinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        SCABinding scaBinding = scaBindingFactory.createSCABinding();
        ExtensionType bindingType = intentAttachPointTypeFactory.createBindingType();
        bindingType.setType(getArtifactType());
        bindingType.setUnresolved(true);
        ((PolicySubject)scaBinding).setExtensionType(bindingType);

        // Read policies
        policyProcessor.readPolicies(scaBinding, reader);

        // Read binding name
        String name = reader.getAttributeValue(null, NAME);
        if (name != null) {
            scaBinding.setName(name);
        }

        // Read binding URI
        String uri = getURIString(reader, URI);
        if (uri != null) {
            scaBinding.setURI(uri);
        }
        
        // Read delegate binding type
        String delegateBindingType = getString(reader, DELEGATE_BINDING_TYPE);
        if (delegateBindingType != null) {
            scaBinding.setDelegateBindingType(delegateBindingType);
        }
        
        // Read delegate binding URI
        String delegateBindingURI = getURIString(reader, DELEGATE_BINDING_URI);
        if (delegateBindingURI != null) {
            scaBinding.setDelegateBindingURI(delegateBindingURI);
        }

        // Read any sub-elements
        boolean endFound = false;
        while (reader.hasNext() && endFound == false) {
            int nextElementType = reader.next();
            switch (nextElementType) {
                case START_ELEMENT:
                    Object extension = extensionProcessor.read(reader, context);
                    if (extension != null) {
                        if (extension instanceof WireFormat) {
                            scaBinding.setRequestWireFormat((WireFormat)extension);
                        }
                    }
                    break;
                case END_ELEMENT:
                	QName endElementName = reader.getName();
                	if(endElementName.equals(endElementName)){
                		endFound = true;
                	}
                	break;
            }
        }
        return scaBinding;
    }

    public void resolve(SCABinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        policyProcessor.resolvePolicies(model, resolver, context);
    }

    public void write(SCABinding scaBinding, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {

        // Write <binding.sca>
        writer.writeStartElement(SCA11_NS, BINDING_SCA);
        policyProcessor.writePolicyAttributes(scaBinding, writer);

        // Write binding name
        if (scaBinding.getName() != null) {
            writer.writeAttribute(NAME, scaBinding.getName());
        }

        // Write binding URI
        if (scaBinding.getURI() != null) {
            writer.writeAttribute(URI, scaBinding.getURI());
        }
        
        // Write delegate binding type
        if (scaBinding.getDelegateBindingType() != null) {
            writer.writeAttribute(DELEGATE_BINDING_TYPE, scaBinding.getDelegateBindingType());
        }
        
        // Write delegate binding URI
        if (scaBinding.getDelegateBindingURI() != null) {
            writer.writeAttribute(DELEGATE_BINDING_URI, scaBinding.getDelegateBindingURI());
        }        
        
        // write wireFormat
        extensionProcessor.write(scaBinding.getRequestWireFormat(), writer, context);

        writer.writeEndElement();
    }

}
