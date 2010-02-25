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

package org.apache.tuscany.sca.policy.security.http.ssl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;

public class HTTPSPolicyProcessor implements StAXArtifactProcessor<HTTPSPolicy> {
    private static final QName KEY_STORE_QNAME = new QName(Constants.SCA11_TUSCANY_NS, "keyStore");
    private static final QName TRUST_STORE_QNAME = new QName(Constants.SCA11_TUSCANY_NS, "trustStore");
    
    public HTTPSPolicyProcessor(FactoryExtensionPoint modelFactories) {
    }
    
    public QName getArtifactType() {
        return HTTPSPolicy.NAME;
    }

    public Class<HTTPSPolicy> getModelType() {
        return HTTPSPolicy.class;
    }

    public HTTPSPolicy read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        HTTPSPolicy policy = new HTTPSPolicy();
        int event = reader.getEventType();
        QName start = reader.getName();
        QName name = null;
        while (true) {
            switch (event) {
                case START_ELEMENT:
                    name = reader.getName();
                    if(KEY_STORE_QNAME.equals(name)) {
                        //<tuscany:keyStore type="JKS" file="conf/tomcat.keystore" password="apache"/>
                        String type = reader.getAttributeValue(null, "type");
                        if(type == null) {
                            Monitor.error(context.getMonitor(), 
                                          this, 
                                          "policy-security-validation-messages", 
                                          "RequiredAttributeKeyStoreTypeMissing");
                        } else {
                            policy.setKeyStoreType(type);    
                        }
                        
                        String file = reader.getAttributeValue(null, "file");
                        if(file == null) {
                            Monitor.error(context.getMonitor(), 
                                    this, 
                                    "policy-security-validation-messages", 
                                    "RequiredAttributeKeyStoreFileMissing");                            
                        } else {
                            policy.setKeyStore(file);
                        }
                        
                        String password = reader.getAttributeValue(null, "password");
                        if(file == null) {
                            Monitor.error(context.getMonitor(), 
                                    this, 
                                    "policy-security-validation-messages", 
                                    "RequiredAttributeKeyStorePasswordMissing");  
                        } else {
                            policy.setKeyStorePassword(password);
                        }
                        
                    } else if(TRUST_STORE_QNAME.equals(name)) {
                        //<tuscany:trustStore type="" file="" password=""/>
                        String type = reader.getAttributeValue(null, "type");
                        if(type == null) {
                            Monitor.error(context.getMonitor(), 
                                    this, 
                                    "policy-security-validation-messages", 
                                    "RequiredAttributeTrustStoreTypeMissing");                            
                        } else {
                            policy.setTrustStoreType(type);    
                        }
                        
                        String file = reader.getAttributeValue(null, "file");
                        if(file == null) {
                            Monitor.error(context.getMonitor(), 
                                    this, 
                                    "policy-security-validation-messages", 
                                    "RequiredAttributeTrustStoreFileMissing"); 
                        } else {
                            policy.setTrustStore(file);
                        }
                        
                        String password = reader.getAttributeValue(null, "password");
                        if(file == null) {
                            Monitor.error(context.getMonitor(), 
                                    this, 
                                    "policy-security-validation-messages", 
                                    "RequiredAttributeTrustStorePasswordMissing");  
                        } else {
                            policy.setTrustStorePassword(password);
                        }

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
        }    }

    public void write(HTTPSPolicy model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {
        // TODO 

    }

    public void resolve(HTTPSPolicy model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

    }

}
