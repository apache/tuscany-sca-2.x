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

package org.apache.tuscany.sca.binding.corba.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.PolicySubjectProcessor;
import org.apache.tuscany.sca.binding.corba.CorbaBinding;
import org.apache.tuscany.sca.binding.corba.CorbaBindingFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * @version $Rev$ $Date$
 */
public class CorbaBindingProcessor implements StAXArtifactProcessor<CorbaBinding> {
    private CorbaBindingFactory factory;
    private PolicyFactory policyFactory;
    private PolicySubjectProcessor policyProcessor;

    public CorbaBindingProcessor(FactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.factory = modelFactories.getFactory(CorbaBindingFactory.class);
        this.policyProcessor = new PolicySubjectProcessor(policyFactory);
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#getArtifactType()
     */
    public QName getArtifactType() {
        return CorbaBinding.BINDING_CORBA_QNAME;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#read(javax.xml.stream.XMLStreamReader)
     */
    public CorbaBinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        CorbaBinding binding = factory.createCorbaBinding();

        // Read the policies 
        policyProcessor.readPolicies(binding, reader);

        binding.setHost(reader.getAttributeValue(null, "host"));
        String port = reader.getAttributeValue(null, "port");
        if (port != null) {
            binding.setPort(Integer.parseInt(port));
        }

        // Read the name
        String name = reader.getAttributeValue(null, "name");
        if (name != null) {
            binding.setName(name);
        }

        // Read binding URI
        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            binding.setURI(uri);
        }
        
        // Read CORBA id
        String id = reader.getAttributeValue(null, "id");
        if (id != null) {
            binding.setId(id);
        }
        return binding;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#write(java.lang.Object, javax.xml.stream.XMLStreamWriter)
     */
    public void write(CorbaBinding model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        // Write a <binding.corba>
        writer.writeStartElement(CorbaBinding.BINDING_CORBA_QNAME.getNamespaceURI(), CorbaBinding.BINDING_CORBA);

        if (model.getName() != null) {
            writer.writeAttribute("name", model.getName());
        }

        if (model.getURI() != null) {
            writer.writeAttribute("uri", model.getURI());
        }

        if (model.getHost() != null) {
            writer.writeAttribute("host", model.getHost());
        }

        if (model.getPort() != -1) {
            writer.writeAttribute("port", String.valueOf(model.getPort()));
        }
        
        if (model.getId() != null) {
            writer.writeAttribute("id", model.getId());
        }
        writer.writeEndElement();
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.ArtifactProcessor#getModelType()
     */
    public Class<CorbaBinding> getModelType() {
        return CorbaBinding.class;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.ArtifactProcessor#resolve(java.lang.Object, org.apache.tuscany.sca.contribution.resolver.ModelResolver)
     */
    public void resolve(CorbaBinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    }

}
