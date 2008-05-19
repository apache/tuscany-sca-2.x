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
import java.net.URLConnection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
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
    private final Monitor monitor;

    public ContributionMetadataDocumentProcessor(XMLInputFactory inputFactory, 
    											 StAXArtifactProcessor staxProcessor,
    											 Monitor monitor) {
        this.inputFactory = inputFactory;
        this.staxProcessor = staxProcessor;
        this.monitor = monitor;
    }
    
    public ContributionMetadataDocumentProcessor(ModelFactoryExtensionPoint modelFactories, 
    											 StAXArtifactProcessor staxProcessor,
    											 Monitor monitor) {
        this.inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        this.staxProcessor = staxProcessor;
        this.monitor = monitor;
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
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-xml-validation-messages", Severity.ERROR, model, message, ex);
	        monitor.problem(problem);
    	}
    }
    
    public String getArtifactType() {
        return "sca-contribution.xml";
    }
    
    public Class<ContributionMetadata> getModelType() {
        return ContributionMetadata.class;
    }
    
    public ContributionMetadata read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            
            // Create a stream reader
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            urlStream = connection.getInputStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);
            reader.nextTag();
            
            // Read the contribution model
            ContributionMetadata contribution = (ContributionMetadata)staxProcessor.read(reader);

            return contribution;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ex = new ContributionReadException(e);
        	error("XMLStreamException", inputFactory, ex);
        	throw ex;
        } catch (IOException e) {
        	ContributionReadException ex = new ContributionReadException(e);
        	error("IOException", inputFactory, ex);
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
    
    public void resolve(ContributionMetadata contribution, ModelResolver resolver) throws ContributionResolveException {
        staxProcessor.resolve(contribution, resolver);
    }

}
