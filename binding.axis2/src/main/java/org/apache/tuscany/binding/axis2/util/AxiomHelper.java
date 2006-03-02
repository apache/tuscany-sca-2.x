/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.axis2.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.om.impl.llom.factory.OMXMLBuilderFactory;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * Utility methods to convert between Axis2 AXIOM, SDO DataObjects and Java objects.
 * 
 * Most of these methods rely on the schemas having been registered with XSDHelper.define
 */
public class AxiomHelper {

    /**
     * Deserialize an OMElement into Java Objects
     * 
     * @param om
     *            the OMElement
     * @return the array of deserialized Java objects
     */
    public static Object[] toObjects(OMElement om) {
        DataObject dataObject = toDataObject(om);
        Object[] os = toObjects(dataObject);
        return os;
    }

    /**
     * Convert a typed DataObject to Java objects
     * 
     * @param dataObject
     * @return the array of Objects from the DataObject
     */
    public static Object[] toObjects(DataObject dataObject) {
        List ips = dataObject.getInstanceProperties();
        Object[] os = new Object[ips.size()];
        for (int i = 0; i < ips.size(); i++) {
            os[i] = dataObject.get((Property) ips.get(i));
        }
        return os;
    }

    /**
     * Convert objects to an AXIOM OMElement
     * 
     * @param os
     * @param typeNS
     * @param typeName
     * @return an AXIOM OMElement
     */
    public static OMElement toOMElement(Object[] os, QName typeQN) {
        DataObject dataObject = toDataObject(os, typeQN);
        OMElement omElement = toOMElement(dataObject, typeQN);
        return omElement;
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
    public static OMElement toOMElement(DataObject dataObject, QName typeQN) {
        try {

            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream(pos);
            XMLHelper.INSTANCE.save(dataObject, typeQN.getNamespaceURI(), typeQN.getLocalPart(), pos);
            pos.close();

            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(pis);
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
    public static DataObject toDataObject(OMElement omElement) {
        try {

            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream(pos);

            omElement.serialize(pos);
            pos.flush();
            pos.close();

            XMLDocument document = XMLHelper.INSTANCE.load(pis);

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
    public static DataObject toDataObject(Object[] os, QName typeQN) {
        String typeName = mangleName(typeQN.getLocalPart());
        DataObject dataObject = DataFactory.INSTANCE.create(typeQN.getNamespaceURI(), typeName);
        List ips = dataObject.getInstanceProperties();
        for (int i = 0; i < ips.size(); i++) {
            Property p = (Property) ips.get(i);
            dataObject.set(i, os[i]);
        }
        return dataObject;
    }

    private static String mangleName(String typeName) {
        // TODO: this will be unnecessary when SDO is fixed to do it automatically (JIRA?)
        StringBuffer sb = new StringBuffer(typeName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.append("Type");
        String s = sb.toString();
        return s;
    }

}
