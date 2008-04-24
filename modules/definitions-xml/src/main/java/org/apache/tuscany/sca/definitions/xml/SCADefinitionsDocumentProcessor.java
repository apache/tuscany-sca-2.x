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
import java.net.URLConnection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.definitions.impl.SCADefinitionsImpl;
import org.apache.tuscany.sca.definitions.util.SCADefinitionsUtil;
import org.apache.tuscany.sca.policy.DefaultIntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.xml.BindingTypeProcessor;
import org.apache.tuscany.sca.policy.xml.ImplementationTypeProcessor;
import org.apache.tuscany.sca.policy.xml.PolicySetProcessor;
import org.apache.tuscany.sca.policy.xml.ProfileIntentProcessor;
import org.apache.tuscany.sca.policy.xml.QualifiedIntentProcessor;
import org.apache.tuscany.sca.policy.xml.SimpleIntentProcessor;

/**
 * A SCA Definitions Document processor.
 * 
 */
public class SCADefinitionsDocumentProcessor  implements URLArtifactProcessor<SCADefinitions> {
    private StAXArtifactProcessor<Object> extensionProcessor;
    //private SCADefinitionsBuilder definitionsBuilder;
    private ModelResolver scaDefinitionsResolver;
    private XMLInputFactory inputFactory;
    private static final String TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.0";
    private static final String DEFINITIONS = "definitions";
    private static final QName tuscanyDefinitions = new QName(TUSCANY_NS,DEFINITIONS);

    /**
     * Construct a new SCADefinitions processor
     * @param assemblyFactory
     * @param policyFactory
     * @param staxProcessor
     */
    public SCADefinitionsDocumentProcessor(StAXArtifactProcessorExtensionPoint staxProcessors,
                                           StAXArtifactProcessor<Object> staxProcessor,
                                           XMLInputFactory inputFactory,
                                           PolicyFactory policyFactory) {
        this.extensionProcessor = (StAXArtifactProcessor<Object>)staxProcessor;
        this.inputFactory = inputFactory;
        //definitionsBuilder = new SCADefinitionsBuilderImpl();
        this.scaDefinitionsResolver = new SCADefinitionsResolver();
        
        IntentAttachPointTypeFactory intentAttachPointFactory = new DefaultIntentAttachPointTypeFactory();
            
        SCADefinitionsProcessor scaDefnProcessor = new SCADefinitionsProcessor(policyFactory, extensionProcessor, scaDefinitionsResolver);
        staxProcessors.addArtifactProcessor(scaDefnProcessor);
        
        staxProcessors.addArtifactProcessor(new SimpleIntentProcessor(policyFactory, extensionProcessor));
        staxProcessors.addArtifactProcessor(new ProfileIntentProcessor(policyFactory, extensionProcessor));
        staxProcessors.addArtifactProcessor(new QualifiedIntentProcessor(policyFactory, extensionProcessor));
        staxProcessors.addArtifactProcessor(new PolicySetProcessor(policyFactory, extensionProcessor));
        staxProcessors.addArtifactProcessor(new ImplementationTypeProcessor(policyFactory, intentAttachPointFactory, extensionProcessor));
        staxProcessors.addArtifactProcessor(new BindingTypeProcessor(policyFactory, intentAttachPointFactory, extensionProcessor));
    }
    

    public SCADefinitions read(URL contributionURL, final URI uri, final URL url) throws ContributionReadException {
        InputStream urlStream = null; 
        try {        	
            // Allow privileged access to open URL stream. Add FilePermission to added to security
            // policy file.
            try {
                urlStream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                    public InputStream run() throws IOException {
                        URLConnection connection = url.openConnection();
                        connection.setUseCaches(false);
                        return connection.getInputStream();
                    }
                });
            } catch (PrivilegedActionException e) {
                throw (IOException)e.getException();
            }
            
            //urlStream = createInputStream(url);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(url.toString(), urlStream);
            
            SCADefinitions scaDefns = new SCADefinitionsImpl();
            QName name = null;
            int event;
            while ( reader.hasNext() ) {
                event = reader.next();
                
                if ( event == XMLStreamConstants.START_ELEMENT  ||
                    event == XMLStreamConstants.END_ELEMENT ) {
                    name = reader.getName();
                    if ( name.equals(tuscanyDefinitions) ) {
                        if ( event == XMLStreamConstants.END_ELEMENT ) {
                            return scaDefns;
                        } 
                    } else {
                        SCADefinitions aDefn = (SCADefinitions)extensionProcessor.read(reader);
                        SCADefinitionsUtil.aggregateSCADefinitions(aDefn, scaDefns);
                    }
                }
            }
            
            return scaDefns;
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
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
    
    public void resolve(SCADefinitions scaDefinitions, ModelResolver resolver) throws ContributionResolveException {
        if ( resolver == null ) {
            resolver = this.scaDefinitionsResolver;
        }
        SCADefinitionsUtil.stripDuplicates(scaDefinitions);
        extensionProcessor.resolve(scaDefinitions, resolver);
    }
    
    public String getArtifactType() {
        return "definitions.xml";
    }
    
    public Class<SCADefinitions> getModelType() {
        return SCADefinitions.class;
    }

    public ModelResolver getSCADefinitionsResolver() {
        return scaDefinitionsResolver;
    }

    public void setDomainModelResolver(ModelResolver scaDefnsModelResolver) {
        this.scaDefinitionsResolver = scaDefnsModelResolver;
    }
}
