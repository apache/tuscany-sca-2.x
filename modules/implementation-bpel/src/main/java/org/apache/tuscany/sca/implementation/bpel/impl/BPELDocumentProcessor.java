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

package org.apache.tuscany.sca.implementation.bpel.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;

/**
 * BPEL document processor responsible for reading a BPEL file and producing necessary model info about it
 * 
 * @version $Rev$ $Date$
 */
public class BPELDocumentProcessor extends BaseStAXArtifactProcessor implements URLArtifactProcessor<BPELProcessDefinition> {
    public final static QName BPEL_PROCESS_DEFINITION = new QName("http://schemas.xmlsoap.org/ws/2004/03/business-process/", "process");
    public final static String NAME_ELEMENT = "name";
    
    private final static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    
    private final BPELFactory factory;
    
    public BPELDocumentProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(BPELFactory.class);
    }
    
    public String getArtifactType() {
        return "*.bpel";
    }    

    public Class<BPELProcessDefinition> getModelType() {
        return BPELProcessDefinition.class;
    }

    public BPELProcessDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        BPELProcessDefinition processDefinition = null;
        try {
            //for now we are just using process name
            //and relying on componentType file for service definition
            //so it's ok to set resolved for now
            processDefinition = indexRead(artifactURL);
            processDefinition.setUnresolved(false);
        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
        
        return processDefinition;
    }


    public void resolve(BPELProcessDefinition model, ModelResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }
    
    /**
     * Read the namespace for the WSDL definition and inline schemas
     * 
     * @param doc
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    protected BPELProcessDefinition indexRead(URL doc) throws Exception {
        BPELProcessDefinition processDefinition = factory.createBPELProcessDefinition();
        processDefinition.setUnresolved(true);
        processDefinition.setLocation(doc);

        InputStream is = doc.openStream();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(is);
            int eventType = reader.getEventType();
            while (true) {
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    QName elementName = reader.getName();
                    if (BPEL_PROCESS_DEFINITION.equals(elementName)) {
                        QName processName = new QName(getString(reader, org.apache.tuscany.sca.assembly.xml.Constants.TARGET_NAMESPACE), getString(reader, NAME_ELEMENT));
                        processDefinition.setName(processName);
                        break;
                    }
                }
                if (reader.hasNext()) {
                    eventType = reader.next();
                } else {
                    break;
                }
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
            is.close();
        }
        
        return processDefinition;
    }
    

}
