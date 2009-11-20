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
package org.apache.tuscany.sca.policy.identity;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 *
 * @version $Rev$ $Date$
 */
public class SecurityIdentityPolicyProcessor implements StAXArtifactProcessor<SecurityIdentityPolicy> {
    private static final String ROLE = "role";

    public QName getArtifactType() {
        return SecurityIdentityPolicy.NAME;
    }

    public SecurityIdentityPolicyProcessor(FactoryExtensionPoint modelFactories) {
    }

    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "policy-security-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }        
    }    

    public SecurityIdentityPolicy read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        SecurityIdentityPolicy policy = new SecurityIdentityPolicy();
        int event = reader.getEventType();
        QName start = reader.getName();
        while (true) {
            switch (event) {
                case START_ELEMENT:
                    String ac = reader.getName().getLocalPart();
                    if ("runAs".equals(ac)) {
                        String roleName = reader.getAttributeValue(null, ROLE);
                        if (roleName == null) {
                            error(context.getMonitor(), "RequiredAttributeRolesMissing", reader);
                            //throw new IllegalArgumentException("Required attribute 'roles' is missing.");
                        } else {
                            policy.setRunAsRole(roleName);
                        }
                    } else if ("useCallerIdentity".equals(ac)) {
                        policy.setUseCallerIdentity(true);
                    }
                    break;
                case END_ELEMENT:
                    if (start.equals(reader.getName())) {
                        if (reader.hasNext()) {
                            reader.next();
                        }
                        return policy;
                    }

            }
            if (reader.hasNext()) {
                event = reader.next();
            } else {
                return policy;
            }
        }
    }

    public void write(SecurityIdentityPolicy policy, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
    XMLStreamException {
        writer.writeStartElement(SecurityIdentityPolicy.NAME.getLocalPart());

        String child = policy.isUseCallerIdentity() ? "useCallerIdentity" : "runAs";
        writer.writeStartElement(child);

        if (!policy.isUseCallerIdentity()) {
            writer.writeAttribute(ROLE, policy.getRunAsRole());
        }

        writer.writeEndElement();
        writer.writeEndElement();
    }

    public Class<SecurityIdentityPolicy> getModelType() {
        return SecurityIdentityPolicy.class;
    }

    public void resolve(SecurityIdentityPolicy policy, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        
    	if (policy.getRunAsRole() != null)
    	    //right now nothing to resolve
            policy.setUnresolved(false);
    }

}
