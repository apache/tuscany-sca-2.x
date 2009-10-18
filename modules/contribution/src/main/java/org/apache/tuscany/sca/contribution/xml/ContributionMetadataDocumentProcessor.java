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
package org.apache.tuscany.sca.contribution.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * URLArtifactProcessor that handles sca-contribution.xml files.
 *
 * @version $Rev$ $Date$
 */
public class ContributionMetadataDocumentProcessor implements URLArtifactProcessor<ContributionMetadata>{
    private final StAXArtifactProcessor staxProcessor;
    private final XMLInputFactory inputFactory;

    public ContributionMetadataDocumentProcessor(XMLInputFactory inputFactory,
                                                 StAXArtifactProcessor staxProcessor) {
        this.inputFactory = inputFactory;
        this.staxProcessor = staxProcessor;
    }

    public ContributionMetadataDocumentProcessor(FactoryExtensionPoint modelFactories,
                                                 StAXArtifactProcessor staxProcessor) {
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
        this.staxProcessor = staxProcessor;
    }

    public String getArtifactType() {
        return "/META-INF/sca-contribution.xml";
    }

    public Class<ContributionMetadata> getModelType() {
        return ContributionMetadata.class;
    }

    public ContributionMetadata read(URL contributionURL, URI uri, URL url, ProcessorContext context) throws ContributionReadException {
        InputStream urlStream = null;
        try {

            // Create a stream reader
            urlStream = IOHelper.openStream(url);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);
            reader.nextTag();

            // Read the contribution model
            ContributionMetadata contribution = (ContributionMetadata)staxProcessor.read(reader, context);

            return contribution;

        } catch (XMLStreamException e) {
        	ContributionReadException ex = new ContributionReadException(e);
        	error(context.getMonitor(), "XMLStreamException", inputFactory, ex);
        	throw ex;
        } catch (IOException e) {
        	ContributionReadException ex = new ContributionReadException(e);
        	error(context.getMonitor(), "IOException", inputFactory, ex);
            throw ex;
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

    public void resolve(ContributionMetadata contribution, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        staxProcessor.resolve(contribution, resolver, context);
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "contribution-xml-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }
}
