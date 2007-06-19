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

package org.apache.tuscany.sca.topology.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.topology.Runtime;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class TopologyDocumentProcessor extends BaseArtifactProcessor implements URLArtifactProcessor<Runtime> {
    private XMLInputFactory inputFactory;

    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param staxProcessor
     */
    public TopologyDocumentProcessor(StAXArtifactProcessor staxProcessor, XMLInputFactory inputFactory) {
        super(null, null, staxProcessor);
        this.inputFactory = inputFactory;
    }

    public Runtime read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            urlStream = url.openStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
            reader.nextTag();
            Runtime node = (Runtime)extensionProcessor.read(reader);
            return node;
            
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
    
    public void resolve(Runtime node, ModelResolver resolver) throws ContributionResolveException {
        extensionProcessor.resolve(node, resolver);
    }

    public String getArtifactType() {
        return ".topology";
    }
    
    public Class<Runtime> getModelType() {
        return Runtime.class;
    }
}
