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
import org.apache.tuscany.sca.assembly.xml.PolicySubjectProcessor;
import org.apache.tuscany.sca.binding.ejb.EJBBinding;
import org.apache.tuscany.sca.binding.ejb.EJBBindingFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
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
 *
 * @version $Rev$ $Date$
 */
public class EJBBindingProcessor implements StAXArtifactProcessor<EJBBinding> {
    private PolicyFactory policyFactory;
    private PolicySubjectProcessor policyProcessor;
    private Monitor monitor;
    private EJBBindingFactory ejbBindingFactory;

    public EJBBindingProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.ejbBindingFactory = modelFactories.getFactory(EJBBindingFactory.class);
        this.policyProcessor = new PolicySubjectProcessor(policyFactory);
        this.monitor = monitor;
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
     private void error(String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
    		 Problem problem = monitor.createProblem(this.getClass().getName(), "binding-ejb-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
    	     monitor.problem(problem);
    	 }        
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
    public EJBBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        EJBBinding ejbBinding = ejbBindingFactory.createEJBBinding();

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
            	error("UnknownEJBSessionType", reader, sessionType, name);            	
            }
        }

        String ejbVersion = reader.getAttributeValue(null, EJBBinding.EJB_VERSION);
        if (ejbVersion != null) {
            if (ejbVersion.equals("EJB2")) {
                ejbBinding.setEjbVersion(EJBBinding.EJBVersion.EJB2);
            } else if (ejbVersion.equals("EJB3")) {
                ejbBinding.setEjbVersion(EJBBinding.EJBVersion.EJB3);
            } else {
            	error("UnknownEJBVersion", reader, ejbVersion, name);
            }
        }

        // TODO: Read requires
        String requires = reader.getAttributeValue(null, EJBBinding.REQUIRES);
        if (requires != null) {
            ejbBinding.setRequires(requires);
        }

        return ejbBinding;
    }

    public void write(EJBBinding ejbBinding, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        // Write a <binding.ejb>
        writer.writeStartElement(Constants.SCA11_NS, EJBBinding.BINDING_EJB);

        if (ejbBinding.getName() != null){
            writer.writeAttribute(EJBBinding.NAME, ejbBinding.getName());
        }
        
        if (ejbBinding.getURI() != null){
            writer.writeAttribute(EJBBinding.URI, ejbBinding.getURI());
        }
        
        // FIXME Implement the rest
        writer.writeEndElement();
    }

    public Class<EJBBinding> getModelType() {
        return EJBBinding.class;
    }

    public void resolve(EJBBinding ejbBinding, ModelResolver modelResolver) throws ContributionResolveException {
    }
}
