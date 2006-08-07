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
package org.apache.tuscany.binding.celtix.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
//import commonj.sdo.DataObject;
//import commonj.sdo.Property;
//import commonj.sdo.helper.XMLDocument;

//import org.apache.tuscany.databinding.sdo.SDOXMLHelper;
//import org.apache.tuscany.sdo.helper.XMLHelperImpl;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.context.ObjectMessageContext;

public class NodeDataReader implements DataReader<Node> {

    SCADataBindingCallback callback;

    public NodeDataReader(SCADataBindingCallback cb) {
        callback = cb;
    }

    public Object read(int idx, Node input) {
        return read(null, idx, input);
    }

    public Object read(QName name, int idx, Node input) {
//FIXME: This part of code should not depend on SDO direclty. Use a data mediation system service?
/*
        try {
            byte bytes[] = getNodeBytes(input);
            //FIXME: get TypeHelper
            //            Object os[] = SDOXMLHelper.toObjects(callback.getTypeHelper(), bytes, false);
            return os[0];
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
*/
        return null;
    }

    public void readWrapper(ObjectMessageContext objCtx, boolean isOutBound, Node input) {
/*
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
            //            XMLDocument document = new XMLHelperImpl(new commonj.sdo.helper.TypeHelper()).load(in);
            //            XMLDocument document = new XMLHelperImpl(callback.getTypeHelper()).load(in);
            DataObject object = null;

            List ips = object.getInstanceProperties();
            Object[] os = new Object[object.getInstanceProperties().size()];
            for (int i = 0; i < os.length; i++) {
                os[i] = object.get((Property)ips.get(i));
            }

            if (callback.hasInOut()) {
                //REVISIT - inOuts
            } else {
                if (isOutBound) {
                    objCtx.setReturn(os[0]);
                } else {
                    objCtx.setMessageObjects(os);
                }
            }
            //        } catch (IOException e) {
            //            throw new WebServiceException(e);
        } catch (ClassCastException e) {
            throw new WebServiceException(e);
        } catch (ClassNotFoundException e) {
            throw new WebServiceException(e);
        } catch (InstantiationException e) {
            throw new WebServiceException(e);
        } catch (IllegalAccessException e) {
            throw new WebServiceException(e);
        }
*/
    }
    byte[] getNodeBytes(Node node)
        throws ClassCastException, ClassNotFoundException,
               InstantiationException, IllegalAccessException {

        //This is also a hack, the JDK should already have this set, but it doesn't
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        if (registry == null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                               "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
        }
        DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        if (impl == null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                               "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
            impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
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
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                               "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
        }
        DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        if (impl == null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                               "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
            impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        }
        LSOutput output = impl.createLSOutput();
        RawByteArrayOutputStream bout = new RawByteArrayOutputStream();
        output.setByteStream(bout);
        LSSerializer writer = impl.createLSSerializer();
        writer.write(node, output);

        return new ByteArrayInputStream(bout.getBytes(), 0, bout.size());
    }

}
