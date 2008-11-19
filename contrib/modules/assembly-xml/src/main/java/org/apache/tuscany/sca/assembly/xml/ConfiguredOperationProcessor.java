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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Processor for dealing with  'operation' elements from composite definitions
 *
 * @version $Rev$ $Date$
 */
public class ConfiguredOperationProcessor implements StAXArtifactProcessor<ConfiguredOperation>, Constants{
    
    private AssemblyFactory assemblyFactory;
    private PolicyAttachPointProcessor policyProcessor;
    private PolicyFactory policyFactory;
    private Monitor monitor;
    
    public ConfiguredOperationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
        this.monitor = monitor;
    }
    
    public ConfiguredOperation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        ConfiguredOperation  configuredOp = assemblyFactory.createConfiguredOperation();
        
        //Read an <operation>
        configuredOp.setName(reader.getAttributeValue(null, NAME));
        configuredOp.setContractName(reader.getAttributeValue(null, SERVICE));
        configuredOp.setUnresolved(true);
        
        // Read policies
        policyProcessor.readPolicies(configuredOp, reader);

        //Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && OPERATION_QNAME.equals(reader.getName())) {
                break;
            }
        }
        
        return configuredOp;
    }
    
    public void write(ConfiguredOperation configuredOperation, XMLStreamWriter writer)
        throws ContributionWriteException, XMLStreamException {

        // Write an <operation>
        policyProcessor.writePolicyPrefixes(configuredOperation, writer);
        writer.writeStartElement(Constants.SCA10_NS, OPERATION);
        policyProcessor.writePolicyAttributes(configuredOperation, writer);

        writer.writeAttribute(NAME, configuredOperation.getName());
        if ( configuredOperation.getContractName() != null ) {
            writer.writeAttribute(SERVICE, configuredOperation.getContractName());
        }
        writer.writeEndElement();
    }
    
    public void resolve(ConfiguredOperation configuredOperation, ModelResolver resolver)
        throws ContributionResolveException {
    }
    
    public QName getArtifactType() {
        return OPERATION_QNAME;
    }
    
    public Class<ConfiguredOperation> getModelType() {
        return ConfiguredOperation.class;
    }

}


