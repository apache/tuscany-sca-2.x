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
package org.apache.tuscany.sca.binding.ejb.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
import org.apache.tuscany.sca.binding.ejb.EJBBinding;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A processor to read the XML that describes the EJB binding...
 * 
 * <binding.ejb
 *  homeInterface="NCName"?
 *  ejb-link-name="NCName"?
 *  session-type="stateful or stateless"?
 *  ejb-version="EJB2 or EJB3"?
 *  name="NCName"?
 *  policySets=" sca:listOfQNames"?
 *  requires="sca:listOfQNames"?
 *  uri="anyURI"?
 * >
 *
 *  <!-- additional elements here -->
 *  
 * </binding.ejb>
 */
public class EJBBindingProcessor implements StAXArtifactProcessor<EJBBindingImpl> {
    private PolicyFactory policyFactory;
    private PolicyAttachPointProcessor policyProcessor;

    public EJBBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
    }

    /**
     * {@inheritDoc}
     */
    public QName getArtifactType() {
        return EJBBinding.BINDING_EJB_QNAME;
    }

    /**
     * {@inheritDoc}
     */
    public EJBBindingImpl read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        EJBBindingImpl ejbBinding = new EJBBindingImpl();

        // Read the policies 
        policyProcessor.readPolicies(ejbBinding, reader);

        // Read the name
        String name = reader.getAttributeValue(null, EJBBinding.NAME);
        if (name != null) {
            ejbBinding.setName(name);
        }

        // Read binding URI
        String uri = reader.getAttributeValue(null, EJBBinding.URI);
        if (uri != null) {
            ejbBinding.setURI(uri);
        }

        String homeInterface = reader.getAttributeValue(null, EJBBinding.HOME_INTERFACE);
        if (homeInterface != null) {
            ejbBinding.setHomeInterface(homeInterface);
        }

        String ejbLinkName = reader.getAttributeValue(null, EJBBinding.EJB_LINK_NAME);
        if (ejbLinkName != null) {
            ejbBinding.setEjbLinkName(ejbLinkName);
        }

        String sessionType = reader.getAttributeValue(null, EJBBinding.SESSION_TYPE);
        if (sessionType != null) {
            if (sessionType.equals("stateless")) {
                ejbBinding.setSessionType(EJBBinding.SessionType.STATELESS);
            } else if (sessionType.equals("stateful")) {
                ejbBinding.setSessionType(EJBBinding.SessionType.STATEFUL);
            } else {
                throw new ContributionReadException("Unknown EJB Session Type of " + sessionType + " for " + name);
            }
        }

        String ejbVersion = reader.getAttributeValue(null, EJBBinding.EJB_VERSION);
        if (ejbVersion != null) {
            if (ejbVersion.equals("EJB2")) {
                ejbBinding.setEjbVersion(EJBBinding.EJBVersion.EJB2);
            } else if (ejbVersion.equals("EJB3")) {
                ejbBinding.setEjbVersion(EJBBinding.EJBVersion.EJB3);
            } else {
                throw new ContributionReadException("Unknown EJB Version of " + sessionType + " for " + name);
            }
        }

        // TODO: Read requires
        String requires = reader.getAttributeValue(null, EJBBinding.REQUIRES);
        if (requires != null) {
            ejbBinding.setRequires(requires);
        }

        return ejbBinding;
    }

    public void write(EJBBindingImpl ejbBinding, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        // Write a <binding.ejb>
        writer.writeStartElement(Constants.SCA10_NS, EJBBinding.BINDING_EJB);

        if (ejbBinding.getName() != null){
            writer.writeAttribute(EJBBinding.NAME, ejbBinding.getName());
        }
        
        if (ejbBinding.getURI() != null){
            writer.writeAttribute(EJBBinding.URI, ejbBinding.getURI());
        }        
        
        
        // FIXME Implement the rest

        writer.writeEndElement();
    }

    public Class<EJBBindingImpl> getModelType() {
        return EJBBindingImpl.class;
    }

    public void resolve(EJBBindingImpl ejbBinding, ModelResolver modelResolver) throws ContributionResolveException {
    }
}
