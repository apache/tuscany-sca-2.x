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
package org.apache.tuscany.binding.celtix.io;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import org.apache.tuscany.databinding.sdo.XMLDocument2XMLStreamReader;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.objectweb.celtix.bindings.DataWriter;
import org.objectweb.celtix.context.ObjectMessageContext;

public class NodeDataWriter implements DataWriter<Node> {
    private static final String XML_NS = "http://www.w3.org/2000/xmlns/";
    private SCADataBindingCallback callback;

    public NodeDataWriter(SCADataBindingCallback cb) {
        callback = cb;
    }

    public void write(Object obj, Node output) {
        write(obj, null, output);
    }

    public void write(Object obj, QName elName, Node output) {
        boolean isWrapped = false;

        XMLDocument document = toXMLDocument(callback.getTypeHelper(), new Object[]{obj}, elName, isWrapped);
        // HACK: [rfeng] We should use the transformer in an interceptor
        XMLDocument2XMLStreamReader transformer = new XMLDocument2XMLStreamReader();
        XMLStreamReader reader = transformer.transform(document, null);

        try {
            //CeltixFire supports Stax, we should not need to do following anymore.
            readDocElements(output, reader, true, null);
        } catch (XMLStreamException e) {
            throw new InvocationRuntimeException(e.getMessage());
        }
    }

    public void writeWrapper(ObjectMessageContext objCtx, boolean isOutbound, Node output) {
        boolean isWrapped = true;
        QName wrapperName;
        if (isOutbound) {
            wrapperName = callback.getOperationInfo().getResponseWrapperQName();
        } else {
            wrapperName = callback.getOperationInfo().getRequestWrapperQName();
        }

        XMLDocument document = toXMLDocument(
            callback.getTypeHelper(), objCtx.getMessageObjects(), wrapperName, isWrapped);
        // HACK: [rfeng] We should use the transformer in an interceptor
        XMLDocument2XMLStreamReader transformer = new XMLDocument2XMLStreamReader();
        XMLStreamReader reader = transformer.transform(document, null);

        try {
            readDocElements(output, reader, true, null);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new InvocationRuntimeException(e.getMessage());
        }
    }
/*
    private DataObject toWrappedDataObject(TypeHelper typeHelper,
                                                 Object ret,
                                                 Object[] os,
                                                 QName typeQN) {
        XSDHelper xsdHelper = new XSDHelperImpl(typeHelper);
        Property property = xsdHelper.getGlobalProperty(typeQN.getNamespaceURI(),
                                                        typeQN.getLocalPart(), true);
        DataObject dataObject = new DataFactoryImpl(typeHelper).create(property.getType());
        List ips = dataObject.getInstanceProperties();
        int offset = 0;
        if (ret != null) {
            dataObject.set(0, ret);
            offset = 1;
        }
        for (int i = offset; i < ips.size(); i++) {
            if (os[i - offset] instanceof Holder) {
                Holder<?> holder = (Holder<?>)os[i - offset];
                dataObject.set(i, holder.value);
            } else {
                dataObject.set(i, os[i - offset]);
            }
        }
        return dataObject;
    }
*/

    /**
     * Convert objects to typed DataObject
     *
     * @param typeHelper
     * @param os
     * @param elementQName
     * @param isWrapped
     * @return the DataObject
     */
    private static XMLDocument toXMLDocument(TypeHelper typeHelper,
                                             Object[] os,
                                             QName elementQName,
                                             boolean isWrapped) {
        XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);

        Property property = xsdHelper.getGlobalProperty(
            elementQName.getNamespaceURI(), elementQName.getLocalPart(), true);
        if (null == property) {
            throw new InvocationRuntimeException(
                "Type '" + elementQName.toString() + "' not found in registered SDO types.");
        }
        DataObject dataObject;
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

    //REVISIT: We should not need to do following anymore with CeltixFire.
    //As CeltixFire supports stax directly.

    /**
     * @param parent
     * @param reader
     * @param repairing
     * @param stopAt:   stop at the specified element
     * @throws XMLStreamException
     */
    public static void readDocElements(Node parent, XMLStreamReader reader, boolean repairing, QName stopAt)
        throws XMLStreamException {
        Document doc = getDocument(parent);

        int event = reader.getEventType();

        while (reader.hasNext()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (startElement(parent, reader, repairing, stopAt) == null) {
                        return;
                    }
                    if (parent instanceof Document && stopAt != null) {
                        if (reader.hasNext()) {
                            reader.next();
                        }
                        return;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    return;
                case XMLStreamConstants.NAMESPACE:
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (parent != null) {
                        parent.appendChild(doc.createTextNode(reader.getText()));
                    }

                    break;
                case XMLStreamConstants.COMMENT:
                    if (parent != null) {
                        parent.appendChild(doc.createComment(reader.getText()));
                    }

                    break;
                case XMLStreamConstants.CDATA:
                    parent.appendChild(doc.createCDATASection(reader.getText()));

                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));

                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));

                    break;
                default:
                    break;
            }

            if (reader.hasNext()) {
                event = reader.next();
            }
        }
    }

    private static Document getDocument(Node parent) {
        return (parent instanceof Document) ? (Document) parent : parent.getOwnerDocument();
    }

    /**
     * @param parent
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static Element startElement(Node parent, XMLStreamReader reader, boolean repairing, QName stopAt)
        throws XMLStreamException {
        Document doc = getDocument(parent);

        if (stopAt != null && stopAt.getNamespaceURI().equals(reader.getNamespaceURI())
            && stopAt.getLocalPart().equals(reader.getLocalName())) {
            return null;
        }

        Element e = doc.createElementNS(reader.getNamespaceURI(), reader.getLocalName());

        if (reader.getPrefix() != null) {
            e.setPrefix(reader.getPrefix());
        }

        parent.appendChild(e);

        for (int ns = 0; ns < reader.getNamespaceCount(); ns++) {
            String uri = reader.getNamespaceURI(ns);
            String prefix = reader.getNamespacePrefix(ns);

            declare(e, uri, prefix);
        }

        for (int att = 0; att < reader.getAttributeCount(); att++) {
            String name = reader.getAttributeLocalName(att);
            String prefix = reader.getAttributePrefix(att);
            if (prefix != null && prefix.length() > 0) {
                name = prefix + ":" + name;
            }

            Attr attr = doc.createAttributeNS(reader.getAttributeNamespace(att), name);
            attr.setValue(reader.getAttributeValue(att));
            e.setAttributeNode(attr);
        }

        reader.next();

        readDocElements(e, reader, repairing, stopAt);

        if (repairing && !isDeclared(e, reader.getNamespaceURI(), reader.getPrefix())) {
            declare(e, reader.getNamespaceURI(), reader.getPrefix());
        }

        return e;
    }

    private static void declare(Element node, String uri, String prefix) {
        if (prefix != null && prefix.length() > 0) {
            node.setAttributeNS(XML_NS, "xmlns:" + prefix, uri);
        } else {
            if (uri != null /* && uri.length() > 0 */) {
                node.setAttributeNS(XML_NS, "xmlns", uri);
            }
        }
    }

    private static boolean isDeclared(Element e, String namespaceURI, String prefix) {
        Attr att;
        if (prefix != null && prefix.length() > 0) {
            att = e.getAttributeNodeNS(XML_NS, "xmlns:" + prefix);
        } else {
            att = e.getAttributeNode("xmlns");
        }

        if (att != null && att.getNodeValue().equals(namespaceURI)) {
            return true;
        }

        if (e.getParentNode() instanceof Element) {
            return isDeclared((Element) e.getParentNode(), namespaceURI, prefix);
        }

        return false;
    }
}
