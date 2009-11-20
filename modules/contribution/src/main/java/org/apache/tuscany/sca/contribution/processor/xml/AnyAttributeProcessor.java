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
package org.apache.tuscany.sca.contribution.processor.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 * A Policy Processor used for testing.
 *
 * @version $Rev$ $Date$
 */
public class AnyAttributeProcessor extends BaseStAXArtifactProcessor implements StAXAttributeProcessor<Extension> {
    
    private AssemblyFactory assemblyFactory;

    public AnyAttributeProcessor(FactoryExtensionPoint modelFactories) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    }
	
    public QName getArtifactType() {
        return ExtensibleStAXAttributeProcessor.ANY_ATTRIBUTE;
    }

    public Class<Extension> getModelType() {
        return Extension.class;
    }

    public Extension read(QName attributeName, XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        String attributeValue = reader.getAttributeValue(attributeName.getNamespaceURI(), attributeName.getLocalPart());
        Extension ext = assemblyFactory.createExtension();
        ext.setQName(attributeName);
        ext.setAttribute(true);
        ext.setValue(attributeValue);
        return ext;
    }

    public void write(Extension attributeExtension, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
    	writer.writeAttribute(attributeExtension.getQName().getPrefix(), 
    	                      attributeExtension.getQName().getNamespaceURI(), 
    	                      attributeExtension.getQName().getLocalPart(), 
    	                      attributeExtension.getValue().toString());  //for extended attributes, we can assume values 
    	                                                                  // are just the string representation fo the attribute
    } 

    public void resolve(Extension model, ModelResolver modelResolver, ProcessorContext context) throws ContributionResolveException {
    	
    }
}
