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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
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

public class SCABindingProcessor implements StAXArtifactProcessor<SCABinding> {
    private static final String NAME = "name";
    private static final String URI = "uri";

    private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    private static final String BINDING_SCA = "binding.sca";
    private static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    private PolicyFactory policyFactory;
    private SCABindingFactory scaBindingFactory;
    private PolicySubjectProcessor policyProcessor;
    private PolicyFactory  intentAttachPointTypeFactory;


    public SCABindingProcessor(FactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        policyProcessor = new PolicySubjectProcessor(policyFactory);
        this.intentAttachPointTypeFactory = modelFactories.getFactory(PolicyFactory.class);
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
        String uri = reader.getAttributeValue(null, URI);
        if (uri != null) {
            scaBinding.setURI(uri);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && BINDING_SCA_QNAME.equals(reader.getName())) {
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

        writer.writeEndElement();
    }

}
