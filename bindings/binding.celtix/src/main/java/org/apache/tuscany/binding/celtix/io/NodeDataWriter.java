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


import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
//import commonj.sdo.DataObject;
//import commonj.sdo.Property;
//import commonj.sdo.helper.TypeHelper;
//import commonj.sdo.helper.XSDHelper;

//import org.apache.tuscany.databinding.sdo.SDOXMLHelper;
//import org.apache.tuscany.sdo.helper.DataFactoryImpl;
//import org.apache.tuscany.sdo.helper.XMLHelperImpl;
//import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.objectweb.celtix.bindings.DataWriter;
import org.objectweb.celtix.context.ObjectMessageContext;

public class NodeDataWriter implements DataWriter<Node> {
    SCADataBindingCallback callback;

    public NodeDataWriter(SCADataBindingCallback cb) {
        callback = cb;
    }

    public void write(Object obj, Node output) {
        write(obj, null, output);
    }

    public void write(Object obj, QName elName, Node output) {
        /*
        byte bytes[] = SDOXMLHelper.toXMLBytes(
            callback.getTypeHelper(),
            new Object[] {obj},
            elName,
            false);
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(bin, new NodeContentHandler(output));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
        */

    }

    public void writeWrapper(ObjectMessageContext objCtx, boolean isOutbound, Node nd) {
/*
        QName wrapperName;
        if (isOutbound) {
            wrapperName = callback.getOperationInfo().getResponseWrapperQName();
        } else {
            wrapperName = callback.getOperationInfo().getRequestWrapperQName();
        }

        DataObject obj = toWrappedDataObject(callback.getTypeHelper(),
            isOutbound ? objCtx.getReturn() : null,
            objCtx.getMessageObjects(),
            wrapperName);

        try {
            //REVISIT - this is SUCH a hack.   SDO needs to be able to
            //go directly to some formats other than streams.  They are working
            //on stax, but not there yet.
            RawByteArrayOutputStream bout = new RawByteArrayOutputStream();
            new XMLHelperImpl(callback.getTypeHelper()).save(obj,
                wrapperName.getNamespaceURI(),
                wrapperName.getLocalPart(),
                bout);
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.getBytes(),
                                                                0,
                                                                bout.size());
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(bin, new NodeContentHandler(nd));
        } catch (IOException e) {
            throw new WebServiceException(e);
        } catch (ParserConfigurationException e) {
            throw new WebServiceException(e);
        } catch (SAXException e) {
            throw new WebServiceException(e);
        }
*/
    }

/*
    public static DataObject toWrappedDataObject(TypeHelper typeHelper,
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
    class NodeContentHandler extends DefaultHandler {
        Node current;
        Document doc;

        public NodeContentHandler(Node nd) {
            doc = nd.getOwnerDocument();
            if (doc == null && nd instanceof Document) {
                doc = (Document)nd;
            }
            current = nd;
        }

        public void characters(char[] ch, int start, int length) {
            current.appendChild(doc.createTextNode(new String(ch, start, length)));
        }

        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) {
            Element newEl = doc.createElementNS(uri, qName);
            current.appendChild(newEl);
            current = newEl;
            for (int x = 0; x < attributes.getLength(); x++) {
                newEl.setAttributeNS(attributes.getURI(x),
                                     attributes.getQName(x),
                                     attributes.getValue(x));
            }
        }

        public void endElement(String uri, String localName, String qName) {
            current = current.getParentNode();
        }
    }


}
