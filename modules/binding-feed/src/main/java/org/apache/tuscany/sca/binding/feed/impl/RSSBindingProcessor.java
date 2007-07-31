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

import org.apache.tuscany.sca.binding.feed.RSSBinding;
import org.apache.tuscany.sca.binding.feed.RSSBindingFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * A processor for <binding.rss> elements.
 */
public class RSSBindingProcessor implements StAXArtifactProcessor<RSSBinding> {

    private final static QName BINDING_RSS = new QName(SCA_NS, "binding.rss");
    private final static String NAME = "name"; 
    private final static String URI = "uri"; 

    private final RSSBindingFactory factory;

    /**
     * Constructs a new binding processor.
     * 
     * @param factory
     */
    public RSSBindingProcessor(RSSBindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return BINDING_RSS;
    }

    public Class<RSSBinding> getModelType() {
        return RSSBinding.class;
    }

    public RSSBinding read(XMLStreamReader reader) throws ContributionReadException {

        // Read the <binding.rss> element
        RSSBinding binding = factory.createRSSBinding();
        String name = reader.getAttributeValue(null, NAME);
        if (name != null) {
            binding.setName(name);
        }
        String uri = reader.getAttributeValue(null, URI);
        if (uri != null) {
            binding.setURI(uri);
        }
        return binding;
    }

    public void write(RSSBinding binding, XMLStreamWriter writer) throws ContributionWriteException {
    }

    public void resolve(RSSBinding binding, ModelResolver resolver) throws ContributionResolveException {
    }

}
