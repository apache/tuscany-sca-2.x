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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A contrainingType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ConstrainingTypeDocumentProcessor extends BaseAssemblyProcessor implements URLArtifactProcessor<ConstrainingType> {
    private XMLInputFactory inputFactory;

    /**
     * Constructs a new constrainingType processor.
     * @param modelFactories
     * @param staxProcessor
     */
    public ConstrainingTypeDocumentProcessor(FactoryExtensionPoint modelFactories,
                                             StAXArtifactProcessor staxProcessor,
                                             Monitor monitor) {
        super(modelFactories, staxProcessor, monitor);
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
    }
    
    public ConstrainingType read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            
            // Create a stream reader
            urlStream = IOHelper.openStream(url);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);
            reader.nextTag();
            
            // Read the constrainingType model 
            ConstrainingType constrainingType = (ConstrainingType)extensionProcessor.read(reader);
           
            return constrainingType;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error("ContributionReadException", inputFactory, ce);
            throw ce;
        } catch (IOException e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error("ContributionReadException", inputFactory, ce);
            throw ce;
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
    
    public void resolve(ConstrainingType constrainingType, ModelResolver resolver) throws ContributionResolveException {
        extensionProcessor.resolve(constrainingType, resolver);
    }
    
    public String getArtifactType() {
        return ".constrainingType";
    }
    
    public Class<ConstrainingType> getModelType() {
        return ConstrainingType.class;
    }
}
