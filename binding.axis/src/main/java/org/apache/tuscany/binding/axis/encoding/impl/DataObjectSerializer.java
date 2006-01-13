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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import commonj.sdo.DataObject;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import org.apache.tuscany.common.io.util.UTF8String;

/**
 */

public class DataObjectSerializer implements Serializer {
    private static final URI SOAP_ELEMENT_URI = URI.createURI("sca:/soapElement.xml");

    /**
     * Serialize a DOM Document
     */
    public void serialize(QName name, Attributes attributes, Object value,
                          SerializationContext context) throws IOException {
        if (!(value instanceof DataObject))
            throw new IOException(Messages.getMessage("cantSerialize01"));

        context.setWriteXMLType(null);

        XMLResource resource = new XMLResourceImpl(SOAP_ELEMENT_URI);
        Map options = new HashMap();
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
        options.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);

        DataObject dataObject = (DataObject) value;

        EStructuralFeature feature = ExtendedMetaData.INSTANCE.getElement(name.getNamespaceURI(), name.getLocalPart());
        EObject root = EcoreUtil.create(feature.getEContainingClass());
        root.eSet(feature, value);

        resource.getContents().add(root);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resource.save(bos, options);
        context.writeString(UTF8String.toString(bos));
    }

    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }

    /**
     * Return XML schema for the specified type, suitable for insertion into the
     * &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types    the Java2WSDL Types object which holds the context for the
     *                 WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }

}
