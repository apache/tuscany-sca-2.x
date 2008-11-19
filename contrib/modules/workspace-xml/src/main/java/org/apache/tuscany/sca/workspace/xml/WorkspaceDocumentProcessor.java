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

package org.apache.tuscany.sca.workspace.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.workspace.Workspace;

/**
 * A contribution workspace document processor.
 * 
 * @version $Rev$ $Date$
 */
public class WorkspaceDocumentProcessor implements URLArtifactProcessor<Workspace> {
    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;
    
    /**
     * Constructs a new componentType processor.
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public WorkspaceDocumentProcessor(StAXArtifactProcessor<Object> staxProcessor, 
    		                          XMLInputFactory inputFactory,
    		                          Monitor monitor) {
        this.staxProcessor = staxProcessor;
        this.inputFactory = inputFactory;
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
    		 Problem problem = new ProblemImpl(this.getClass().getName(), "workspace-xml-validation-messages", Severity.ERROR, model, message, ex);
    	     monitor.problem(problem);
    	 }        
     }
    
    public Workspace read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            
            // Create a stream reader
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            urlStream = connection.getInputStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);
            reader.nextTag();
            
            // Read the workspace model 
            Workspace workspace = (Workspace)staxProcessor.read(reader);
            if (workspace != null) {
                workspace.setURI(uri.toString());
            }

            return workspace;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error("ContributionReadException", inputFactory, ce);
            throw ce;
        } catch (IOException e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error("ContributionReadException", staxProcessor, ce);
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
    
    public void resolve(Workspace workspace, ModelResolver resolver) throws ContributionResolveException {
        staxProcessor.resolve(workspace, resolver);
    }
    
    public String getArtifactType() {
        return ".workspace";
    }
    
    public Class<Workspace> getModelType() {
        return Workspace.class;
    }
}
