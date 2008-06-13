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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.util.PolicyComputationUtils;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeDocumentProcessor extends BaseAssemblyProcessor implements URLArtifactProcessor<Composite> {
    private XMLInputFactory inputFactory;
    private List scaDefnSink;
    private Collection<PolicySet> domainPolicySets = null;
    private int scaDefnsCount = 0;

    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param staxProcessor
     */
    public CompositeDocumentProcessor(StAXArtifactProcessor staxProcessor, 
    								  XMLInputFactory inputFactory, List scaDefnsSink, Monitor monitor) {
        super(null, null, staxProcessor, monitor);
        this.inputFactory = inputFactory;
        this.scaDefnSink = scaDefnsSink;
    }

    /**
     * Constructs a new composite processor.
     * @param modelFactories
     * @param staxProcessor
     */
    public CompositeDocumentProcessor(ModelFactoryExtensionPoint modelFactories, 
    								  StAXArtifactProcessor staxProcessor, Monitor monitor) {
        super(null, null, staxProcessor, monitor);
        this.inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);
    }
    
    public Composite read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
        InputStream scdlStream = null;
        try {
            if (scaDefnSink != null ) {
                fillDomainPolicySets(scaDefnSink);
            }
            
            Composite composite = null;
            
            byte[] transformedArtifactContent;
            try {
                if ( domainPolicySets != null ) {
                    transformedArtifactContent =
                        PolicyComputationUtils.addApplicablePolicySets(url, domainPolicySets);
                    scdlStream = new ByteArrayInputStream(transformedArtifactContent);
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    scdlStream = connection.getInputStream();
                }
            } catch ( IOException e ) {
            	ContributionReadException ce = new ContributionReadException(e);
            	error("ContributionReadException", scdlStream, ce);
            	throw ce;
            } catch ( Exception e ) {
            	ContributionReadException ce = new ContributionReadException(e);
            	error("ContributionReadException", scdlStream, ce);
                //throw ce;
            }
            
            XMLStreamReader reader = inputFactory.createXMLStreamReader(scdlStream);
            
            reader.nextTag();
            
            // Read the composite model
            composite = (Composite)extensionProcessor.read(reader);
            if (composite != null) {
                composite.setURI(uri.toString());
            }

            // For debugging purposes, write it back to XML
//            if (composite != null) {
//                try {
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
//                    outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
//                    extensionProcessor.write(composite, outputFactory.createXMLStreamWriter(bos));
//                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bos.toByteArray()));
//                    OutputFormat format = new OutputFormat();
//                    format.setIndenting(true);
//                    format.setIndent(2);
//                    XMLSerializer serializer = new XMLSerializer(System.out, format);
//                    serializer.serialize(document);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            
            return composite;
            
        } catch (XMLStreamException e) {
        	ContributionReadException ce = new ContributionReadException(e);
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
        if (composite != null)
    	    extensionProcessor.resolve(composite, resolver);
    }

    public String getArtifactType() {
        return ".composite";
    }
    
    public Class<Composite> getModelType() {
        return Composite.class;
    }
    
    private void fillDomainPolicySets(List scaDefnsSink) {
        Map<QName, PolicySet> domainPolicySetMap = null;
        if ( scaDefnsSink.size() > scaDefnsCount ) {
        //if ( !scaDefnsSink.isEmpty() ) {
            domainPolicySetMap = new Hashtable<QName, PolicySet>();
            
            if ( domainPolicySets != null ) {
                for ( PolicySet policySet : domainPolicySets ) {
                    domainPolicySetMap.put(policySet.getName(), policySet);
                } 
            }
            
            for ( Object object : scaDefnsSink ) {
                if ( object instanceof SCADefinitions ) {
                    for ( PolicySet policySet : ((SCADefinitions)object).getPolicySets() ) {
                        domainPolicySetMap.put( policySet.getName(), policySet);
                    }
                }
            }
            domainPolicySets =  domainPolicySetMap.values();
            //scaDefnsSink.clear();
            scaDefnsCount = scaDefnsSink.size();
        }
    }
}
