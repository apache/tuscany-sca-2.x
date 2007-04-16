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

package org.apache.tuscany.assembly.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtension;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeDocumentProcessor extends BaseArtifactProcessor implements URLArtifactProcessorExtension<Composite> {
    private XMLInputFactory inputFactory;

    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param staxProcessor
     */
    public CompositeDocumentProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessorExtension staxProcessor, XMLInputFactory inputFactory) {
        super(factory, policyFactory, staxProcessor);
        this.inputFactory = inputFactory;
    }

    /**
     * Construct a new composite processor.
     * @param staxProcessor
     */
    public CompositeDocumentProcessor(StAXArtifactProcessorExtension staxProcessor) {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), staxProcessor, XMLInputFactory.newInstance());
    }

    public Composite read(URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            urlStream = url.openStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
            reader.nextTag();
            Composite composite = (Composite)extensionProcessor.read(reader);
            return composite;
            
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
    
    public void write(Composite model, URL url) throws ContributionWriteException {
        // Can't write to a URL
        throw new UnsupportedOperationException();
    }
    
    public void resolve(Composite composite, ArtifactResolver resolver) throws ContributionResolveException {
        extensionProcessor.resolve(composite, resolver);
    }

    public void wire(Composite composite) throws ContributionWireException {
        extensionProcessor.wire(composite);
    }

    public String getArtifactType() {
        return ".composite";
    }
    
    public Class<Composite> getModelType() {
        return Composite.class;
    }
}
