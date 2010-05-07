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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.xml.sax.SAXException;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeDocumentProcessor extends BaseAssemblyProcessor implements URLArtifactProcessor<Composite>,
                                                                                 XMLStreamConstants {
    private ValidatingXMLInputFactory inputFactory;
    

    /**
     * Constructs a composite document processor
     * @param modelFactories
     * @param staxProcessor
     * @param monitor
     */
    public CompositeDocumentProcessor(FactoryExtensionPoint modelFactories,
                                      StAXArtifactProcessor<?> staxProcessor) {
        super(modelFactories, staxProcessor);
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
    }
    
    /**
     * Reads the contents of a Composite document and returns a Composite object
     * @param contributionURL - the URL of the contribution containing the Composite - can be null
     * @param uri - the URI of the composite document
     * @param url - the URL of the composite document
     * @return a Composite object built from the supplied Composite document
     */
    public Composite read(URL contributionURL, URI uri, URL url, ProcessorContext context) throws ContributionReadException {
    	if( uri == null || url == null ) {
    		throw new ContributionReadException("Request to read composite with uri or url NULL");
    	} // end if
        InputStream scdlStream = null;
        
        try {
            scdlStream = IOHelper.openStream(url);;
        } catch (IOException e) {
            ContributionReadException ce = new ContributionReadException("Exception reading " + uri, e);
            error(context.getMonitor(), "ContributionReadException", url, ce);
            throw ce;
        } 
        return read(uri, url, scdlStream, context);
    }

    public Composite read(URI uri, URL url, InputStream scdlStream, ProcessorContext context) throws ContributionReadException {
        try {       
            
            Composite composite = null;
            Monitor monitor = context.getMonitor();
            // Tag the monitor with the name of the composite artifact
            if( monitor != null ) {
            	monitor.setArtifactName(uri.toString());
            } //end if
            
            // Set up a StreamSource for the composite file, since this has an associated URL that can be used to
            // by the parser to find references to other files such as DTDs
            StreamSource scdlSource = new StreamSource( scdlStream, url.toString() );
            XMLStreamReader reader = inputFactory.createXMLStreamReader(scdlSource);
            
            //XMLStreamReader reader = inputFactory.createXMLStreamReader(scdlStream);
            
            // set the monitor on the input factory as the standard XMLInputFactory
            // methods used for creating readers don't allow for the context to
            // be passed in
            ValidatingXMLInputFactory.setMonitor(reader, context.getMonitor());
            
            //reader.nextTag();
            readCompositeFileHeader( reader );
            
            // Read the composite model
            composite = (Composite)extensionProcessor.read(reader, context);
            if (composite != null) {
                composite.setURI(uri.toString());
            }

            return composite;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ce = new ContributionReadException("Exception reading " + uri, e);
        	error(context.getMonitor(), "ContributionReadException", inputFactory, ce);
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
    
    /**
     * Reads the header portion of a composite file - i.e. the section of the file before the
     * <composite> start tag
     * In particular handle any DTD declarations
     * @param reader - an XMLStreamReader which is reading the composite file
     * @throws XMLStreamException 
     */
    private void readCompositeFileHeader( XMLStreamReader reader ) throws XMLStreamException {
    	    	
        while (true) {
        	int event = reader.next();

            if ( event == CHARACTERS
                || event == CDATA
                || event == SPACE
                || event == PROCESSING_INSTRUCTION
                || event == COMMENT 
                || event == DTD 
                || event == ENTITY_DECLARATION )  {
                continue;
            } // end if
            
            // The first start (or end) element terminates the header scan
            if (event == START_ELEMENT || event == END_ELEMENT) {
                return;
            } // end if
        } // end while
    } // end method readCompositeFileHeader
    
    public void resolve(Composite composite, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    	try {
	        if (composite != null)
	    	    extensionProcessor.resolve(composite, resolver, context);
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
