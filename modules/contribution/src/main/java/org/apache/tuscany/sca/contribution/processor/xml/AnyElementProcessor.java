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

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.Constants;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;

public class AnyElementProcessor implements StAXArtifactProcessor<Extension> {
    private static final QName ANY_ELEMENT = new QName(Constants.XMLSCHEMA_NS, "any");

    private AssemblyFactory assemblyFactory;
    private StAXHelper helper;
    
    @SuppressWarnings("unused")
    private Monitor monitor;


    public AnyElementProcessor(ExtensionPointRegistry extensionPoints, StAXArtifactProcessor<Object> extensionProcessor, Monitor monitor) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.helper = StAXHelper.getInstance(extensionPoints);
        this.monitor = monitor;
    }

    public QName getArtifactType() {
        return ANY_ELEMENT;
    }

    public Class<Extension> getModelType() {
        return Extension.class;
    }

    /**
     * Reads the contetns of the unknown elements and generates a custom
     * implementation of XMLStreamReader i.e. XMLEventsStreamReader
     * 
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    public Extension read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName name = reader.getName();
        String xml = helper.saveAsString(reader);
        Extension ext = assemblyFactory.createExtension();
        ext.setQName(name);
        ext.setValue(xml);
        
        return ext;
    }

    /**
     * Writes unknown portions back to the writer
     * 
     * @param model
     * @param writer
     */
    public void write(Extension model, XMLStreamWriter writer) throws XMLStreamException {
        Object value = model.getValue();
        if (!(value instanceof String)) {
            return;
        }
        String xml = (String) value;
        XMLStreamReader reader = helper.createXMLStreamReader(new StringReader(xml));
        // Position the reader to the root element
        reader.nextTag();
        helper.save(reader, writer);
    }

    public void resolve(Extension model, ModelResolver resolver) throws ContributionResolveException {
    }
}
