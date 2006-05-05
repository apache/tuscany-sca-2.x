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
package org.apache.tuscany.binding.axis2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.tuscany.core.wire.InvocationRuntimeException;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * Utility methods to convert between Axis2 AXIOM, SDO DataObjects and Java objects.
 * 
 * Most of these methods rely on the schemas having been registered with XSDHelper.define
 */
public final class AxiomHelper {

    private AxiomHelper() {
        // utility class, never contructed
    }

    /**
     * Deserialize an OMElement into Java Objects
     * 
     * @param om
     *            the OMElement
     * @param isWrapped
     * 
     * @return the array of deserialized Java objects
     */
    public static Object[] toObjects(TypeHelper typeHelper, OMElement om, boolean isWrapped) {
        DataObject dataObject = toDataObject(typeHelper, om);
        return toObjects(dataObject, isWrapped);
    }

    /**
     * Convert a typed DataObject to Java objects
     * 
     * @param dataObject
     * @param isWrapped
     * @return the array of Objects from the DataObject
     */
    public static Object[] toObjects(DataObject dataObject, boolean isWrapped) {
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
     * Convert objects to an AXIOM OMElement
     * 
     * @param os
     * @param typeNS
     * @param typeName
     * @return an AXIOM OMElement
     */
    public static OMElement toOMElement(TypeHelper typeHelper, Object[] os, QName elementQName, boolean isWrapped) {
        DataObject dataObject = toDataObject(typeHelper, os, elementQName, isWrapped);
        return toOMElement(typeHelper, dataObject, elementQName);
    }

    /**
     * Convert a DataObject to AXIOM OMElement
     * 
     * @param dataObject
     * @param typeNS
     * @param typeName
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    public static OMElement toOMElement(TypeHelper typeHelper, DataObject dataObject, QName elementQName) {
        try {

            ByteArrayOutputStream pos = new ByteArrayOutputStream();
            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            xmlHelper.save(dataObject, elementQName.getNamespaceURI(), elementQName.getLocalPart(), pos);
            pos.close();

            XMLStreamReader parser;
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(AxiomHelper.class.getClassLoader());
                parser = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(pos.toByteArray()));
            } finally {
                Thread.currentThread().setContextClassLoader(ccl);
            }
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), parser);
            OMElement root = builder.getDocumentElement();

            return root;

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Deserialize an AXIOM OMElement into a DataObject
     * 
     * @param omElement
     * @return
     */
    public static DataObject toDataObject(TypeHelper typeHelper, OMElement omElement) {
        try {

            ByteArrayOutputStream pos = new ByteArrayOutputStream();

            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(AxiomHelper.class.getClassLoader());
                omElement.serialize(pos);
            } finally {
                Thread.currentThread().setContextClassLoader(ccl);
            }

            pos.flush();
            pos.close();

            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            XMLDocument document = xmlHelper.load(new ByteArrayInputStream(pos.toByteArray()));

            return document.getRootObject();

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
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
    public static DataObject toDataObject(TypeHelper typeHelper, Object[] os, QName elementQName, boolean isWrapped) {
        XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);

        Property property = xsdHelper.getGlobalProperty(elementQName.getNamespaceURI(), elementQName.getLocalPart(), true);
        if (null == property) {
            throw new InvocationRuntimeException("Type '" + elementQName.toString() + "' not found in registered SDO types.");
        }
        if (isWrapped) {
            DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
            DataObject dataObject = dataFactory.create(property.getType());
            List ips = dataObject.getInstanceProperties();
            for (int i = 0; i < ips.size(); i++) {
                dataObject.set(i, os[i]);
            }
            return dataObject;
        } else {
            Object value = os[0];
            Type type = property.getType();
            if (!type.isDataType()) {
                return (DataObject) value;
            } else {
                return SDOUtil.createDataTypeWrapper(type, value);
            }
        }
    }

}
