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

package org.apache.tuscany.sca.implementation.osgi.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.NAME;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.PROPERTY_QNAME;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.TYPE;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.VALUE;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;

/**
 * A processor for <tuscany:osgi.property>
 */
public class OSGiPropertyProcessor implements StAXArtifactProcessor<OSGiProperty> {
    private OSGiImplementationFactory factory;
    

    public OSGiPropertyProcessor(FactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(OSGiImplementationFactory.class);
    }

    public OSGiProperty read(XMLStreamReader reader, ProcessorContext context) throws XMLStreamException {
        int event = reader.getEventType();
        OSGiProperty prop = null;
        while (true) {
            switch (event) {
                case START_ELEMENT:
                    QName name = reader.getName();
                    if (PROPERTY_QNAME.equals(name)) {
                        String propName = reader.getAttributeValue(null, NAME);
                        String propValue = reader.getAttributeValue(null, VALUE);
                        String propType = reader.getAttributeValue(null, TYPE);

                        if (propValue == null) {
                            propValue = reader.getElementText();
                        }
                        if (propValue != null) {
                            propValue = propValue.trim();
                        }

                        prop = factory.createOSGiProperty(propName, propValue, propType);
                        return prop;
                    }
                    break;
                case END_ELEMENT:
                    name = reader.getName();
                    if (PROPERTY_QNAME.equals(name)) {
                        return prop;
                    }
                    break;
            }
            if (reader.hasNext()) {
                event = reader.next();
            } else {
                return prop;
            }
        }
    }

    public QName getArtifactType() {
        return PROPERTY_QNAME;
    }

    public void write(OSGiProperty model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        writer.writeStartElement(PROPERTY_QNAME.getNamespaceURI(), PROPERTY_QNAME.getLocalPart());
        writer.writeAttribute(NAME, model.getName());
        writer.writeAttribute(TYPE, model.getType());
        writer.writeCharacters(model.getStringValue());
        writer.writeEndElement();
    }

    public Class<OSGiProperty> getModelType() {
        return OSGiProperty.class;
    }

    public void resolve(OSGiProperty model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // TODO: To be implemented
    }
}
