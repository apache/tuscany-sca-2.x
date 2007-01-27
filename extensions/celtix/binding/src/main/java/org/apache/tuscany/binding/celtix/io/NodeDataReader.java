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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import static org.w3c.dom.bootstrap.DOMImplementationRegistry.PROPERTY;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.model.DataType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

import org.apache.tuscany.core.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2XMLDocument;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.context.ObjectMessageContext;

public class NodeDataReader implements DataReader<Node> {

    private SCADataBindingCallback callback;

    public NodeDataReader(SCADataBindingCallback cb) {
        callback = cb;
    }

    public Object read(int idx, Node input) {
        return read(null, idx, input);
    }

    public Object read(QName name, int idx, Node input) {
        try {
            InputStream in = getNodeStream(input);
            XMLInputFactory staxFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = staxFactory.createXMLStreamReader(in);

            XMLStreamReader2XMLDocument transformer = new XMLStreamReader2XMLDocument();
            TransformationContext context = new TransformationContextImpl();
            DataType<QName> binding = new DataType<QName>(DataObject.class, null);
            binding.setMetadata(TypeHelper.class.getName(), callback.getTypeHelper());
            context.setTargetDataType(binding);
            XMLDocument document = transformer.transform(reader, context);

            boolean isWrapped = false;
            return toObjects(document, isWrapped);
        } catch (Exception e) {
            //REVISIT: better handling of exceptions
        }
        return null;
    }

    public void readWrapper(ObjectMessageContext objCtx, boolean isOutBound, Node input) {
        try {
            QName wrapperName;
            if (isOutBound) {
                wrapperName = callback.getOperationInfo().getResponseWrapperQName();
            } else {
                wrapperName = callback.getOperationInfo().getRequestWrapperQName();
            }

            Node nd = input.getFirstChild();
            while (nd != null
                && !wrapperName.getNamespaceURI().equals(nd.getNamespaceURI())
                && !wrapperName.getLocalPart().equals(nd.getLocalName())) {
                nd = nd.getNextSibling();
            }

            //REVISIT - This is SUCH a HACK.  This needs to be done with StAX or something
            //a bit better than streaming and reparsing
            InputStream in = getNodeStream(nd);
            XMLInputFactory staxFactory = XMLInputFactory.newInstance(
                "javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
            XMLStreamReader reader = staxFactory.createXMLStreamReader(in);

            XMLStreamReader2XMLDocument transformer = new XMLStreamReader2XMLDocument();
            TransformationContext context = new TransformationContextImpl();
            DataType<QName> binding = new DataType<QName>(DataObject.class, null);
            binding.setMetadata(TypeHelper.class.getName(), callback.getTypeHelper());
            context.setTargetDataType(binding);
            XMLDocument document = transformer.transform(reader, context);

            //boolean isWrapped = true;
            Object[] objects = toObjects(document, true);

            if (callback.hasInOut()) {
                //REVISIT - inOuts
            } else {
                if (isOutBound) {
                    objCtx.setReturn(objects[0]);
                } else {
                    objCtx.setMessageObjects(objects);
                }
            }
        } catch (Exception e) {
            //REVISIT: better handling of exceptions
        }
    }

    /**
     * Convert a typed DataObject to Java objects
     *
     * @param document
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
            return new Object[]{object};
        }
    }

    byte[] getNodeBytes(Node node)
        throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        //This is also a hack, the JDK should already have this set, but it doesn't
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        if (registry == null) {
            System.setProperty(PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
        }
        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        if (impl == null) {
            System.setProperty(PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
            impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        }
        LSOutput output = impl.createLSOutput();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        output.setByteStream(bout);
        LSSerializer writer = impl.createLSSerializer();
        writer.write(node, output);

        return bout.toByteArray();
    }

    InputStream getNodeStream(Node node)
        throws ClassCastException, ClassNotFoundException,
               InstantiationException, IllegalAccessException {

        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        if (registry == null) {
            //This is also a hack, the JDK should already have this set, but it doesn't
            System.setProperty(PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
        }
        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        if (impl == null) {
            System.setProperty(PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
            impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        }
        LSOutput output = impl.createLSOutput();
        RawByteArrayOutputStream bout = new RawByteArrayOutputStream();
        output.setByteStream(bout);
        LSSerializer writer = impl.createLSSerializer();
        writer.write(node, output);

        return new ByteArrayInputStream(bout.getBytes(), 0, bout.size());
    }

}
