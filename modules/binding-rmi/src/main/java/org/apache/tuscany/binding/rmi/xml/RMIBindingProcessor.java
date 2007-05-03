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

package org.apache.tuscany.binding.rmi.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.xml.BaseArtifactProcessor;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.binding.rmi.RMIBinding;
import org.apache.tuscany.binding.rmi.RMIBindingConstants;
import org.apache.tuscany.binding.rmi.RMIBindingFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.policy.PolicyFactory;

public class RMIBindingProcessor extends BaseArtifactProcessor implements
    StAXArtifactProcessor<RMIBinding>, RMIBindingConstants {

    private RMIBindingFactory rmiBindingFactory;

    public RMIBindingProcessor(AssemblyFactory assemblyFactory,
                               PolicyFactory policyFactory,
                               RMIBindingFactory rmiBindingFactory) {
        super(assemblyFactory, policyFactory, null);
        this.rmiBindingFactory = rmiBindingFactory;
    }

    public RMIBinding read(XMLStreamReader reader) throws ContributionReadException {
        try {
            RMIBinding rmiBinding = rmiBindingFactory.createWebServiceBinding();
            rmiBinding.setUnresolved(true);
            
            //Read policies
            readPolicies(rmiBinding, reader);
            
            //Read host, port and service name
            rmiBinding.setRmiHostName(getString(reader, RMI_HOST));
            rmiBinding.setRmiPort(getString(reader, RMI_PORT));
            rmiBinding.setRmiServiceName(getString(reader, RMI_SERVICE));
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && BINDING_RMI_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return rmiBinding;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void write(RMIBinding rmiBinding, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write a <binding.ws>
            writer.writeStartElement(Constants.SCA10_NS, BINDING_RMI);
            
            if (rmiBinding.getRmiHostName() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_HOST, rmiBinding.getRmiHostName());
            }
            
            if (rmiBinding.getRmiPort() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_PORT, rmiBinding.getRmiPort());
            }
            
            if (rmiBinding.getRmiServiceName() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_SERVICE, rmiBinding.getRmiServiceName());
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(RMIBinding model, ArtifactResolver resolver) throws ContributionResolveException {
    }

    public QName getArtifactType() {
        return RMIBindingConstants.BINDING_RMI_QNAME;
    }

    public Class<RMIBinding> getModelType() {
        return RMIBinding.class;
    }
}
