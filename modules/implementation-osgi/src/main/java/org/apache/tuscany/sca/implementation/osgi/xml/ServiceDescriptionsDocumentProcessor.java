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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;

/**
 * The service descriptions document processor
 */
public class ServiceDescriptionsDocumentProcessor implements URLArtifactProcessor<ServiceDescriptions> {
    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor extensionProcessor;

    public ServiceDescriptionsDocumentProcessor(FactoryExtensionPoint modelFactories,
                                                StAXArtifactProcessor staxProcessor) {
        super();
        this.extensionProcessor = staxProcessor;
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
    }

    public String getArtifactType() {
        return "/OSGI-INF/remote-service/*.xml";
    }

    public ServiceDescriptions read(URL contributionURL, URI artifactURI, URL artifactURL, ProcessorContext context)
        throws ContributionReadException {
        InputStream is = null;
        try {
            URLConnection connection = artifactURL.openConnection();
            connection.setUseCaches(false);
            is = connection.getInputStream();
        } catch (IOException e) {
            ContributionReadException ce = new ContributionReadException(e);
            throw ce;
        }
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            ValidatingXMLInputFactory.setMonitor(reader, context.getMonitor());
            Object result = extensionProcessor.read(reader, context);
            return (ServiceDescriptions)result;
        } catch (XMLStreamException e) {
            ContributionReadException ce = new ContributionReadException(e);
            throw ce;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException ioe) {
                //ignore
            }
        }

    }

    public Class<ServiceDescriptions> getModelType() {
        return ServiceDescriptions.class;
    }

    public void resolve(ServiceDescriptions model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    }

}
