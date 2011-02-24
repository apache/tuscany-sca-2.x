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

package sample;

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

public class ImplementationGenericPolicyProcessor implements StAXArtifactProcessor<ImplementationGenericPolicy> {
    
    public ImplementationGenericPolicyProcessor(FactoryExtensionPoint modelFactories) {
    }
    
    public QName getArtifactType() {
        return ImplementationGenericPolicy.NAME;
    }

    public Class<ImplementationGenericPolicy> getModelType() {
        return ImplementationGenericPolicy.class;
    }

    public ImplementationGenericPolicy read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        ImplementationGenericPolicy policy = new ImplementationGenericPolicy();
        
        String name = reader.getAttributeValue(null, "name");
        
        if (name != null) {
            policy.setName(name);
        } else {
            Monitor.error(context.getMonitor(), 
                    this, 
                    "policy-security-validation-messages", 
                    "RequiredAttributeKeyStoreTypeMissing");
        }
    
        return policy;
    }

    public void write(ImplementationGenericPolicy model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {
        // TODO 

    }

    public void resolve(ImplementationGenericPolicy model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // It's resolved when it's read
    }

}
