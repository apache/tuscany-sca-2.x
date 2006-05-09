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
package org.apache.tuscany.databinding.sdo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

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
 * Utility methods to convert between XML byte arrays, SDO DataObjects, and Java objects.
 * 
 * Most of these methods rely on the schemas having been registered with XSDHelper.define
 */
public final class SDOXMLHelper {

    private SDOXMLHelper() {
        // utility class, never contructed
    }

    /**
     * Deserialize an XML byte array into Java Objects
     * 
     * @param xmlBytes
     *            the byte array containing the XML
     * @param isWrapped
     * 
     * @return the array of deserialized Java objects
     */
    public static Object[] toObjects(TypeHelper typeHelper, byte[] xmlBytes, boolean isWrapped) {
        DataObject dataObject = toDataObject(typeHelper, xmlBytes);
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
     * Serializes objects to an XML byte array
     * 
     * @param os
     * @param typeNS
     * @param typeName
     * @return a byte array containing the XML
     */
    public static byte[] toXMLBytes(TypeHelper typeHelper, Object[] os, QName elementQName, boolean isWrapped) {
        DataObject dataObject = toDataObject(typeHelper, os, elementQName, isWrapped);
        return toXMLbytes(typeHelper, dataObject, elementQName);
    }

    /**
     * Convert a DataObject to an XML byte array
     * 
     * @param dataObject
     * @param typeNS
     * @param typeName
     * @return a byte array containing the XML bytes
     */
    public static byte[] toXMLbytes(TypeHelper typeHelper, DataObject dataObject, QName elementQName) {
        try {

            ByteArrayOutputStream pos = new ByteArrayOutputStream();
            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            xmlHelper.save(dataObject, elementQName.getNamespaceURI(), elementQName.getLocalPart(), pos);
            pos.close();

            return pos.toByteArray();

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Deserialize an XML byte array into a DataObject
     * 
     * @param xmlBytes
     * @return a DataObject
     */
    public static DataObject toDataObject(TypeHelper typeHelper, byte[] xmlBytes) {
        try {

            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            XMLDocument document = xmlHelper.load(new ByteArrayInputStream(xmlBytes));

            return document.getRootObject();

        } catch (IOException e) {
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
