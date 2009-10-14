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

import org.apache.tuscany.sca.assembly.Composite;
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
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeDocumentProcessor extends BaseAssemblyProcessor implements URLArtifactProcessor<Composite> {
    private XMLInputFactory inputFactory;
    private Monitor monitor;

    /**
     * Constructs a composite document processor
     * @param modelFactories
     * @param staxProcessor
     * @param monitor
     */
    public CompositeDocumentProcessor(FactoryExtensionPoint modelFactories,
                                      StAXArtifactProcessor<?> staxProcessor,
                                      Monitor monitor) {
        super(modelFactories, staxProcessor, monitor);
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
        this.monitor = monitor;
    }
    
    /**
     * Reads the contents of a Composite document and returns a Composite object
     * @param contributionURL - the URL of the contribution containing the Composite - can be null
     * @param uri - the URI of the composite document
     * @param url - the URL of the composite document
     * @return a Composite object built from the supplied Composite document
     */
    public Composite read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
    	if( uri == null || url == null ) {
    		throw new ContributionReadException("Request to read composite with uri or url NULL");
    	} // end if
        InputStream scdlStream = null;
        
        try {
            scdlStream = IOHelper.openStream(url);;
        } catch (IOException e) {
            ContributionReadException ce = new ContributionReadException("Exception reading " + uri, e);
            error("ContributionReadException", url, ce);
            throw ce;
        } 
        return read(uri, scdlStream);
    }

    public Composite read(URI uri, InputStream scdlStream) throws ContributionReadException {
        try {       
            
            Composite composite = null;
            
            // Tag the monitor with the name of the composite artifact
            if( monitor != null ) {
            	monitor.setArtifactName(uri.toString());
            } //end if
            
            XMLStreamReader reader = inputFactory.createXMLStreamReader(scdlStream);
            
            reader.nextTag();
            
            // Read the composite model
            composite = (Composite)extensionProcessor.read(reader);
            if (composite != null) {
                composite.setURI(uri.toString());
            }

            return composite;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ce = new ContributionReadException("Exception reading " + uri, e);
        	error("ContributionReadException", inputFactory, ce);
            throw ce;
        } finally {
            try {
                if (scdlStream != null) {
                    scdlStream.close();
                    scdlStream = null;
                }
            } catch (IOException ioe) {
                //ignore
            }
        }
    }
    
    public void resolve(Composite composite, ModelResolver resolver) throws ContributionResolveException {
    	try {
	        if (composite != null)
	    	    extensionProcessor.resolve(composite, resolver);
    	} catch (Throwable e ) {
    		// Add information about which composite was being processed when the exception occurred
    		String newMessage = "Processing composite " + composite.getName() + ": " + e.getMessage();
    		throw new ContributionResolveException( newMessage, e );
    	} // end try
    }

    public String getArtifactType() {
        return ".composite";
    }
    
    public Class<Composite> getModelType() {
        return Composite.class;
    }
    
} // end class
