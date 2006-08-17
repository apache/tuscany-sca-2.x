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
package org.apache.tuscany.binding.axis2.util;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.databinding.sdo.XMLDocument2XMLStreamReader;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2XMLDocument;
import org.apache.tuscany.databinding.xml.StAXBinding;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * DataBinding for converting between AXIOM OMElement and Java Objects
 */
public class SDODataBinding {

    private TypeHelper typeHelper;

    private boolean isWrapped;

    private QName elementName;

    public SDODataBinding(TypeHelper typeHelper, boolean isWrapped, QName elementName) {
        this.typeHelper = typeHelper;
        this.isWrapped = isWrapped;
        this.elementName = elementName;
    }

    public Object[] fromOMElement(OMElement omElement) {
        XMLStreamReader reader = omElement.getXMLStreamReader();
        // HACK: [rfeng] We should use the transformer in an interceptor
        XMLStreamReader2XMLDocument transformer = new XMLStreamReader2XMLDocument();
        TransformationContext context = new TransformationContextImpl();
        StAXBinding binding = new StAXBinding();
        binding.setAttribute(TypeHelper.class.getName(), typeHelper);
        context.setTargetDataBinding(binding);
        XMLDocument document = transformer.transform(reader, context);
        return toObjects(document, isWrapped);
    }

    public OMElement toOMElement(Object[] os) {
        XMLDocument document = toXMLDocument(typeHelper, os, elementName, isWrapped);
        // HACK: [rfeng] We should use the transformer in an interceptor
        XMLDocument2XMLStreamReader transformer = new XMLDocument2XMLStreamReader();
        XMLStreamReader reader = transformer.transform(document, null);
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);
        OMElement omElement = builder.getDocumentElement();
        return omElement;
    }

    /**
     * Convert a typed DataObject to Java objects
     * 
     * @param dataObject
     * @param isWrapped
     * @return the array of Objects from the DataObject
     */
    public static Object[] toObjects(XMLDocument document, boolean isWrapped) {
        DataObject dataObject = document.getRootObject();
        if (isWrapped) {
            List ips = dataObject.getInstanceProperties();
            Object[] os = new Object[ips.size()];
            for (int i = 0; i < ips.size(); i++) {
                os[i] = dataObject.get((Property) ips.get(i));
            }
            return os;
        } else {
            Object object = dataObject;
            Type type = dataObject.getType();
            if (type.isSequenced()) {
                object = dataObject.getSequence().getValue(0);
            }
            return new Object[] { object };
        }
    }

    /**
     * Convert objects to typed DataObject
     * 
     * @param typeNS
     * @param typeName
     * @param os
     * @return the DataObject
     */
    private static XMLDocument toXMLDocument(TypeHelper typeHelper, Object[] os, QName elementQName, boolean isWrapped) {
        XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);

        Property property = xsdHelper.getGlobalProperty(elementQName.getNamespaceURI(), elementQName.getLocalPart(), true);
        if (null == property) {
            throw new InvocationRuntimeException("Type '" + elementQName.toString() + "' not found in registered SDO types.");
        }
        DataObject dataObject = null;
        if (isWrapped) {
            DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
            dataObject = dataFactory.create(property.getType());
            List ips = dataObject.getInstanceProperties();
            for (int i = 0; i < ips.size(); i++) {
                dataObject.set(i, os[i]);
            }
        } else {
            Object value = os[0];
            Type type = property.getType();
            if (!type.isDataType()) {
                dataObject = (DataObject) value;
            } else {
                dataObject = SDOUtil.createDataTypeWrapper(type, value);
            }
        }

        XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
        return xmlHelper.createDocument(dataObject, elementQName.getNamespaceURI(), elementQName.getLocalPart());

    }

}
