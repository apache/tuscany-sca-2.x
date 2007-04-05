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

package org.apache.tuscany.idl.wsdl.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.idl.wsdl.WSDLFactory;
import org.apache.tuscany.idl.wsdl.WSDLInterface;
import org.apache.tuscany.idl.wsdl.impl.DefaultWSDLFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class WSDLInterfaceProcessor implements StAXArtifactProcessor<WSDLInterface>, WSDLConstants {

    private WSDLFactory wsdlFactory;

    public WSDLInterfaceProcessor(WSDLFactory wsdlFactory) {
        this.wsdlFactory = wsdlFactory;
    }
    
    public WSDLInterfaceProcessor() {
        this(new DefaultWSDLFactory());
    }

    public WSDLInterface read(XMLStreamReader reader) throws ContributionReadException {
        
        try {
    
            // Read an <interface.java>
            WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
            wsdlInterface.setUnresolved(true);
            // TODO handle qname
            wsdlInterface.setName(new QName("", reader.getAttributeValue(null, INTERFACE)));
    
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && INTERFACE_WSDL_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return wsdlInterface;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void resolve(WSDLInterface model, ArtifactResolver resolver) throws ContributionException {
        // TODO Auto-generated method stub
    }
    
    public void optimize(WSDLInterface model) throws ContributionException {
        // TODO Auto-generated method stub
    }
    
    public QName getArtifactType() {
        return WSDLConstants.INTERFACE_WSDL_QNAME;
    }
    
    public Class<WSDLInterface> getModelType() {
        return WSDLInterface.class;
    }
}
