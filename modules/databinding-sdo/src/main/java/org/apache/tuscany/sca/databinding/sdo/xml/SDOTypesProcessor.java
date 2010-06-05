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
package org.apache.tuscany.sca.databinding.sdo.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

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
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.sdo.SDOTypes;


/**
 * Loader that handles &lt;import.sdo&gt; elements.
 *
 * @version $Rev$ $Date$
 */
public class SDOTypesProcessor implements StAXArtifactProcessor<SDOTypes> {

    public SDOTypesProcessor(ExtensionPointRegistry registry) {
    }

    public QName getXMLType() {
        return SDOTypes.SDO_TYPES;
    }

    public SDOTypes read(XMLStreamReader reader,ProcessorContext context) throws ContributionReadException, XMLStreamException {
        assert SDOTypes.SDO_TYPES.equals(reader.getName());

        // FIXME: How do we associate the application HelperContext with the one
        // imported by the composite
        SDOTypes sdoTypes = new SDOTypes();
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            sdoTypes.setFactory(factoryName);
        }
        String location = reader.getAttributeValue(null, "location");
        if (location != null) {
            sdoTypes.setSchemaLocation(location);
        }
        String ns = reader.getAttributeValue(null, "namespace");
        sdoTypes.setNamespace(ns);

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && SDOTypes.SDO_TYPES.equals(reader.getName())) {
                break;
            }
        }
        return sdoTypes;
    }

    public QName getArtifactType() {
        return SDOTypes.SDO_TYPES;
    }

    public void write(SDOTypes model, XMLStreamWriter writer,ProcessorContext context) throws ContributionWriteException {
        try {
            writer.writeStartElement(SDOTypes.SDO_TYPES.getNamespaceURI(), SDOTypes.SDO_TYPES.getLocalPart());
            if (model.getNamespace() != null) {
                writer.writeAttribute("namespace", model.getNamespace());
            }
            if (model.getSchemaLocation() != null) {
                writer.writeAttribute("location", model.getSchemaLocation());
            }
            if (model.getFactory() != null) {
                writer.writeAttribute("factory", model.getFactory());
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public Class<SDOTypes> getModelType() {
        return SDOTypes.class;
    }

    public void resolve(SDOTypes types, ModelResolver resolver,ProcessorContext context) throws ContributionResolveException {
        // Defer the resolution to SDOTypesResolver which aggragates the type registrations into an instance of HelperContext
        resolver.addModel(types,context);
    }

}
