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
package org.apache.tuscany.sca.policy.security.jaas;

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
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 *
 * @version $Rev$ $Date$
 */
public class JaasAuthenticationPolicyProcessor implements StAXArtifactProcessor<JaasAuthenticationPolicy> {
    private static final QName JAAS_AUTHENTICATION_POLICY_QNAME = JaasAuthenticationPolicy.NAME;
    private static final String callbackHandler = "callbackHandler";
    private static final String SCA10_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";
    public static final QName CALLBACK_HANDLER_QNAME = new QName(SCA10_TUSCANY_NS, callbackHandler);
    public static final QName CONFIGURATION_QNAME = new QName(SCA10_TUSCANY_NS, "configurationName");
    
    
    public QName getArtifactType() {
        return JAAS_AUTHENTICATION_POLICY_QNAME;
    }
    
    public JaasAuthenticationPolicyProcessor(FactoryExtensionPoint modelFactories) {
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
    
    public JaasAuthenticationPolicy read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        JaasAuthenticationPolicy policy = new JaasAuthenticationPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if (name.equals(CALLBACK_HANDLER_QNAME)) {
                        String callbackHandlerClassName = reader.getElementText();
                        if (callbackHandlerClassName != null) {
                            policy.setCallbackHandlerClassName(callbackHandlerClassName.trim());
                        }
                    }
                    if (name.equals(CONFIGURATION_QNAME)) {
                        String configurationName = reader.getElementText();
                        if (configurationName != null) {
                            policy.setConfigurationName(configurationName.trim());
                        }
                    } 

                    break;
                }
            }
            
            if ( event == END_ELEMENT ) {
                if ( JAAS_AUTHENTICATION_POLICY_QNAME.equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
         
        return policy;
    }

    public void write(JaasAuthenticationPolicy policy, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
                                                        XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 JAAS_AUTHENTICATION_POLICY_QNAME.getLocalPart(),
                                 JAAS_AUTHENTICATION_POLICY_QNAME.getNamespaceURI());
        writer.writeNamespace("tuscany", SCA10_TUSCANY_NS);
        
       
        writer.writeEndElement();
    }

    public Class<JaasAuthenticationPolicy> getModelType() {
        return JaasAuthenticationPolicy.class;
    }

    public void resolve(JaasAuthenticationPolicy policy, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

         if (policy.getCallbackHandlerClassName() != null) {
             ClassReference classReference = new ClassReference(policy.getCallbackHandlerClassName());
             classReference = resolver.resolveModel(ClassReference.class, classReference, context);
             Class<?> callbackClass = classReference.getJavaClass();
             if (callbackClass == null) {
            	 error(context.getMonitor(), "ClassNotFoundException", resolver, policy.getCallbackHandlerClassName());
                 //throw new ContributionResolveException(new ClassNotFoundException(policy.getCallbackHandlerClassName()));
             } else {
                 policy.setCallbackHandlerClass(callbackClass);
                 policy.setUnresolved(false);
             }             
         }
    }
    
}
