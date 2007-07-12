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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.policy.SCADefinitions;

/**
 * A SCA Definitions Document processor.
 * 
 */
public class SCADefinitionsDocumentProcessor  implements URLArtifactProcessor<SCADefinitions> {
    protected StAXArtifactProcessor<Object> extensionProcessor;

    private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    static {
        domFactory.setNamespaceAware(true);
    }
    private XMLInputFactory inputFactory;

    /**
     * Construct a new SCADefinitions processor
     * @param assemblyFactory
     * @param policyFactory
     * @param staxProcessor
     */
    public SCADefinitionsDocumentProcessor(StAXArtifactProcessor staxProcessor, XMLInputFactory inputFactory) {
        this.extensionProcessor = (StAXArtifactProcessor<Object>)staxProcessor;
        this.inputFactory = inputFactory;
    }

    public SCADefinitions read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            urlStream = url.openStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
            reader.nextTag();
            SCADefinitions scaDefns = (SCADefinitions)extensionProcessor.read(reader);
            return scaDefns;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        } finally {
            try {
                if (urlStream != null) {
                    urlStream.close();
                    urlStream = null;
                }
            } catch (IOException ioe) {
                //ignore
            }
        }
    }
    
    public void resolve(SCADefinitions composite, ModelResolver resolver) throws ContributionResolveException {
        extensionProcessor.resolve(composite, resolver);
    }

    public String getArtifactType() {
        return "definitions.composite";
    }
    
    public Class<SCADefinitions> getModelType() {
        return SCADefinitions.class;
    }
}
