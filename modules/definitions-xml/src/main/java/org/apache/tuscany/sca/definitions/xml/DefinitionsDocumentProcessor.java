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

package org.apache.tuscany.sca.definitions.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.definitions.DefinitionsFactory;
import org.apache.tuscany.sca.definitions.util.DefinitionsUtil;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * A SCA Definitions Document processor.
 *
 * @version $Rev$ $Date$
 */
public class DefinitionsDocumentProcessor implements URLArtifactProcessor<Definitions> {
    private StAXArtifactProcessor<Object> extensionProcessor;
    private XMLInputFactory inputFactory;
    private DefinitionsFactory definitionsFactory;
    private Monitor monitor;


    /**
     * Constructs a new SCADefinitions processor.
     * 
     * @param modelFactories
     * @param staxProcessor
     */
    public DefinitionsDocumentProcessor(FactoryExtensionPoint modelFactories,
                                        StAXArtifactProcessor<Object> staxProcessor,
                                        Monitor monitor) {
        this.extensionProcessor = (StAXArtifactProcessor<Object>)staxProcessor;
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
        this.definitionsFactory = modelFactories.getFactory(DefinitionsFactory.class);
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
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "definitions-xml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    public Definitions read(URL contributionURL, final URI uri, final URL url) throws ContributionReadException {
        InputStream urlStream = null;
        monitor.pushContext("Definitions: " + url);
        try {
            // Allow privileged access to open URL stream. Add FilePermission to added to security
            // policy file.
            try {
                urlStream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                    public InputStream run() throws IOException {
                        return IOHelper.openStream(url);
                    }
                });
            } catch (PrivilegedActionException e) {
                error("PrivilegedActionException", url, (IOException)e.getException());
                throw (IOException)e.getException();
            }

            //urlStream = createInputStream(url);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);

            Definitions definitions = definitionsFactory.createDefinitions();
            int event = reader.getEventType();
            while (reader.hasNext()) {
                event = reader.next();

                // We only deal with the root element
                if (event == XMLStreamConstants.START_ELEMENT) {
                    // QName name = reader.getName();
                    Object model = extensionProcessor.read(reader);
                    if (model instanceof Definitions) {
                        DefinitionsUtil.aggregate((Definitions)model, definitions, monitor);
                        return definitions;
                    } else {
                        error("ContributionReadException", model, null);
                    }
                }
            }

            return definitions;
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
            
            monitor.popContext();
        }
    }

    public void resolve(Definitions scaDefinitions, ModelResolver resolver) throws ContributionResolveException {
        extensionProcessor.resolve(scaDefinitions, resolver);
    }

    public String getArtifactType() {
        return "/META-INF/definitions.xml";
    }

    public Class<Definitions> getModelType() {
        return Definitions.class;
    }

}
