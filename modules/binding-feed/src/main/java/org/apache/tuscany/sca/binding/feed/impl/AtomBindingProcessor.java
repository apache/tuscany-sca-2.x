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

package org.apache.tuscany.sca.binding.feed.impl;

import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.binding.feed.AtomBinding;
import org.apache.tuscany.sca.binding.feed.AtomBindingFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * A processor for <binding.atom> elements.
 */
public class AtomBindingProcessor implements StAXArtifactProcessor<AtomBinding> {

    private QName BINDING_ATOM = new QName(SCA_NS, "binding.atom");

    private final AtomBindingFactory factory;

    public AtomBindingProcessor(AtomBindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return BINDING_ATOM;
    }

    public Class<AtomBinding> getModelType() {
        return AtomBinding.class;
    }

    public AtomBinding read(XMLStreamReader reader) throws ContributionReadException {
        String uri = reader.getAttributeValue(null, "uri");
        AtomBinding binding = factory.createAtomBinding();
        if (uri != null) {
            binding.setURI(uri.trim());
        }
        return binding;
    }

    public void write(AtomBinding binding, XMLStreamWriter writer) throws ContributionWriteException {
    }

    public void resolve(AtomBinding binding, ModelResolver resolver) throws ContributionResolveException {
    }

}
