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

package org.apache.tuscany.sca.policy.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.policy.ExtensionTypeFactory;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.PolicyFactory;


/* 
 * Processor for handling xml models of ImplementationType meta data definitions
 */
public class ImplementationTypeProcessor extends ExtensionTypeProcessor<ImplementationType> {

    public ImplementationTypeProcessor(PolicyFactory policyFactory, ExtensionTypeFactory extnTypeFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        super(policyFactory, extnTypeFactory, extensionProcessor);
    }

    public ImplementationType read(XMLStreamReader reader) throws ContributionReadException {
        QName type = getQName(reader, TYPE);
        
        if ( type != null ) {
            ImplementationType implType = extnTypeFactory.createImplementationType();
            implType.setTypeName(type);
            
            readAlwaysProvidedIntents(implType, reader);
            readMayProvideIntents(implType, reader);
            return implType;
        } else { 
            throw new ContributionReadException("Required attribute '" + TYPE + 
                                                "' missing from ImplementationType Definition");
        }
    }
    
    public QName getArtifactType() {
        return IMPLEMENTATION_TYPE_QNAME;
    }
    
    public Class<ImplementationType> getModelType() {
        return ImplementationType.class;
    }
}
