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
package org.apache.tuscany.sca.contribution.processor;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implementation of an extensible URL artifact processor.
 * 
 * Takes a URLArtifactProcessorExtensionPoint and delegates to the proper URLArtifactProcessor
 * by either fileName or fileExtention
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleURLArtifactProcessor implements URLArtifactProcessor<Object> {

    private URLArtifactProcessorExtensionPoint processors;

    /**
     * Constructs a new ExtensibleURLArtifactProcessor.
     * 
     * @param processors
     */
    public ExtensibleURLArtifactProcessor(URLArtifactProcessorExtensionPoint processors) {
        this.processors = processors;
    }

    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    @SuppressWarnings("unchecked")
    public Object read(URL contributionURL, URI sourceURI, URL sourceURL, ProcessorContext context) throws ContributionReadException {
        URLArtifactProcessor<Object> processor = null;
        if (sourceURI != null) {
            //try to retrieve a processor for the specific URI
            String uri = sourceURI.toString();
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
            // Register the URI as the artifact type starts with /
            processor = (URLArtifactProcessor<Object>)processors.getProcessor(uri);
        }
        
        /*
        if (processor == null) {
            // Delegate to the processor associated with file extension
            String fileName = getFileName(sourceURL);

            //try to retrieve a processor for the specific filename
            processor = (URLArtifactProcessor<Object>)processors.getProcessor(fileName);
        }

        if (processor == null) {
            //try to find my file type (extension)
            String extension = sourceURL.getPath();

            int extensionStart = extension.lastIndexOf('.');
            //handle files without extension (e.g NOTICE)
            if (extensionStart > 0) {
                // File extensions are registered as .<extension>
                extension = extension.substring(extensionStart);
                processor = (URLArtifactProcessor<Object>)processors.getProcessor(extension);
            }
        }
        */

        if (processor == null) {
            return null;
        }
        return processor.read(contributionURL, sourceURI, sourceURL, context);
    }

    @SuppressWarnings("unchecked")
    public void resolve(Object model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            URLArtifactProcessor processor = processors.getProcessor(model.getClass());
            if (processor != null) {
                processor.resolve(model, resolver, context);
            }
        }
    }

    public <M> M read(URL contributionURL, URI artifactURI, URL artifactUrl, ProcessorContext context, Class<M> type)
        throws ContributionReadException {
        Object mo = read(contributionURL, artifactURI, artifactUrl, context);
        if (type.isInstance(mo)) {
            return type.cast(mo);
        } else {
            UnrecognizedElementException e = new UnrecognizedElementException(null);
            e.setResourceURI(artifactURI.toString());
            error(context.getMonitor(), "UnrecognizedElementException", processors, artifactURI.toString());
            throw e;
        }
    }

    public String getArtifactType() {
        return "";
    }

    public Class<Object> getModelType() {
        return Object.class;
    }

}
