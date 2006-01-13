/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis.encoding.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import commonj.sdo.DataObject;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 */
public class DataObjectDeserializer extends DeserializerImpl implements Deserializer {
    private static final URI SOAP_ELEMENT_URI = URI.createURI("sca:/soapElement.xml");

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        try {
            XMLResource resource = new XMLResourceImpl(SOAP_ELEMENT_URI);
            Element element = context.getCurElement();
            // FIXME: [rfeng]
            String str = element.toString();
            InputStream inputStream = new ByteArrayInputStream(str.getBytes());
            Map options = new HashMap();
            options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
            resource.load(inputStream, options);
            DataObject root = (DataObject) resource.getContents().get(0);
            DataObject dataObject = root.getDataObject(element.getLocalName());
            setValue(dataObject);
        } catch (Exception xe) {
            throw new SAXException(xe);
        }

    }

}
