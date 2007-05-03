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
package org.apache.tuscany.contribution.processor;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.contribution.service.UnrecognizedElementException;

/**
 * The default implementation of a StAX artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultURLArtifactProcessorExtensionPoint
    extends DefaultArtifactProcessorExtensionPoint
    implements URLArtifactProcessorExtensionPoint, URLArtifactProcessorExtension<Object> {

    /**
     * Constructs a new loader registry.
     * @param assemblyFactory
     * @param policyFactory
     * @param factory
     */
    public DefaultURLArtifactProcessorExtensionPoint() {
    }

    public Object read(URL contributionURL, URI sourceURI, URL sourceURL) throws ContributionReadException {
        URLArtifactProcessorExtension<Object> processor = null;
        
        // Delegate to the processor associated with file extension
        String extension = sourceURL.getFile();
        int extensionStart = extension.lastIndexOf('.');
        //handle files without extension (e.g NOTICE)
        if (extensionStart > 0) {
            extension = extension.substring(extensionStart);
            processor = (URLArtifactProcessorExtension<Object>)this.getProcessor(extension);            
        }
        if (processor == null) {
            return null;
        }
        return processor.read(contributionURL, sourceURI, sourceURL);
    }

    public void write(Object model, URL outputSource) throws ContributionWriteException {
        
        // Delegate to the processor associated with the particular model type
        if (model != null) {
            URLArtifactProcessorExtension<Object> processor = 
                (URLArtifactProcessorExtension<Object>)this.getProcessor((Class<Object>)model.getClass());
            if (processor != null) {
                processor.write(model, outputSource);
            }
        }
    }
    
    
    
    public void resolve(Object model, ArtifactResolver resolver) throws ContributionResolveException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            URLArtifactProcessorExtension<Object> processor = 
                (URLArtifactProcessorExtension<Object>)this.getProcessor((Class<Object>)model.getClass());
            if (processor != null) {
                processor.resolve(model, resolver);
            }
        }
    }
    
    public void wire(Object model) throws ContributionWireException {

        // Delegate to the processor associated with the model type
        if (model != null) {
            URLArtifactProcessorExtension<Object> processor = 
                (URLArtifactProcessorExtension<Object>)this.getProcessor((Class<Object>)model.getClass());
            if (processor != null) {
                processor.wire(model);
            }
        }
    }
    
    public <MO> MO read(URL contributionURL, URI artifactURI, URL artifactUrl, Class<MO> type) 
        throws ContributionReadException {
        Object mo = read(contributionURL, artifactURI, artifactUrl);
        if (type.isInstance(mo)) {
            return type.cast(mo);
        } else {
            UnrecognizedElementException e = new UnrecognizedElementException(null);
            e.setResourceURI(artifactURI.toString());
            throw e;
        }
    }
    
    public void addExtension(URLArtifactProcessorExtension artifactProcessor) {
        processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
    }
    
    public void removeExtension(URLArtifactProcessorExtension artifactProcessor) {
        processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        processorsByModelType.remove(artifactProcessor.getModelType());        
    }

    public String getArtifactType() {
        return null;
    }
    
    public Class<Object> getModelType() {
        return null;
    }

}
