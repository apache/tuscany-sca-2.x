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
package org.apache.tuscany.sca.binding.jms.operationselector.jmsuserprop;


import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 *
 * @version $Rev$ $Date$
 */
public class OperationSelectorJMSUserPropProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<OperationSelectorJMSUserProp> {
    
    public QName getArtifactType() {
        return OperationSelectorJMSUserProp.OPERATION_SELECTOR_JMS_USERPROP_QNAME;
    }
    
    public OperationSelectorJMSUserPropProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    
    public OperationSelectorJMSUserProp read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        OperationSelectorJMSUserProp opSelector = new OperationSelectorJMSUserProp();
        String propertyName = reader.getAttributeValue(null, OperationSelectorJMSUserProp.OPERATION_SELECTOR_JMS_USERPROP_ATTR);
        if (propertyName != null && propertyName.length() > 0) {
            opSelector.setPropertyName(propertyName);
        } else {
            throw new ContributionReadException(OperationSelectorJMSUserProp.OPERATION_SELECTOR_JMS_USERPROP_QNAME.toString() + ": " + 
                    OperationSelectorJMSUserProp.OPERATION_SELECTOR_JMS_USERPROP_ATTR + " is a required attribute.");
        }
        
        return opSelector;
    }

    public void write(OperationSelectorJMSUserProp opSelector, XMLStreamWriter writer) 
        throws ContributionWriteException, XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 getArtifactType().getLocalPart(),
                                 getArtifactType().getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA11_TUSCANY_NS); 
        
        if (opSelector.getPropertyName() != null) {
            writer.writeAttribute(OperationSelectorJMSUserProp.OPERATION_SELECTOR_JMS_USERPROP_ATTR, opSelector.getPropertyName());
        }
        
        writer.writeEndElement();
    }

    public Class<OperationSelectorJMSUserProp> getModelType() {
        return OperationSelectorJMSUserProp.class;
    }

    public void resolve(OperationSelectorJMSUserProp arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
}
