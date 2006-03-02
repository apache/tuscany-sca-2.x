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
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.tuscany.binding.axis2.handler.WebServicePortMetaData;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class AxiomHelper {

    public static OMElement toOMElement(DataObject dataObject, String nsURI, String name) throws XMLStreamException, IOException {

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        XMLHelper.INSTANCE.save(dataObject, nsURI, name, pos);
        pos.close();

        // create the parser
        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(pis);
        // create the builder
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), parser);
        // get the root element (in this case the envelope)
        OMElement root = builder.getDocumentElement();

        /*
         * // get the writer XMLStreamWriter writer = XMLOutputFactory.newInstance() .createXMLStreamWriter(System.out); // dump the out put to
         * console with caching root.serialize(writer); writer.flush();
         */

        return root;

    }

    public static DataObject fromOMElement(OMElement root) throws IOException, XMLStreamException {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);

        root.serialize(pos);
        pos.flush();
        pos.close(); // We have to close the pos to avoid the reader being
        // blocked

        XMLDocument document = XMLHelper.INSTANCE.load(pis);

        return document.getRootObject();

    }

    /**
     * Serialize Java Objects into an AXIOM OMElement
     * 
     * @param method
     * @param args
     * @param wsPortMetaData
     * @return the AXIOM OMElement
     */
    public static OMElement toOMElement(Method method, Object[] args, WebServicePortMetaData wsPortMetaData) {

        // TODO: toDataObject() doesn't work, ask Frank how it should be done
        // DataObject dataObject = toDataObject(method, args, wsPortMetaData);
        // String operationName = method.getName();
        // String serviceNamespace = wsPortMetaData.getServiceName().getNamespaceURI();
        // String xml = XMLHelper.INSTANCE.save(dataObject,serviceNamespace, operationName);
        String xml = 
            "<getGreetings xmlns=\"http://helloworldaxis.samples.tuscany.apache.org\">" + 
               "<in0>World</in0>" + 
            "</getGreetings>";

        StringReader sr = new StringReader(xml);
        XMLStreamReader parser;
        try {
            parser = XMLInputFactory.newInstance().createXMLStreamReader(sr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), parser);
        OMElement om = builder.getDocumentElement();

        return om;
    }

    /**
     * Deserialize an OMElement into Java Objects
     * 
     * @param om
     *            the OMElement
     * @return the array of deserialized Java objects
     */
    public static Object[] toObjects(OMElement om) {
        DataObject dataObject;
        try {
            dataObject = fromOMElement(om);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        }
        Object[] os = toObjects(dataObject);
        return os;
    }

    public static Object[] toObjects(DataObject dataObject) {
        Sequence parmSeq = dataObject.getSequence("mixed");
        ArrayList parms = new ArrayList(parmSeq.size());
        for (int i = 0; i < parmSeq.size(); ++i) {
            Object parmDO = (Object) parmSeq.getValue(i);// parm element
            if (parmDO instanceof DataObject) {
                Sequence nn = ((DataObject) parmDO).getSequence("mixed");
                for (int j = 0; j < nn.size(); j++) {
                    Object valueDO = (Object) nn.getValue(j); // data array s
                    if (valueDO instanceof DataObject) {
                        Sequence seqVal = ((DataObject) valueDO).getSequence("mixed");
                        Object seqDO = seqVal.getValue(0);
                        if (seqDO instanceof String) {
                            parms.add(seqDO);
                        } else {
                            parms.add(valueDO); // no sure if this is right?
                        }
                    } else {
                        parms.add(valueDO);
                    }
                }
            }
        }
        Object[] args = parms.toArray(new Object[parms.size()]);
        return args;
    }

    private static DataObject toDataObject(Method method, Object[] args, WebServicePortMetaData wsPortMetaData) {
        // TODO: this doesn't work, ask Frank how it should be done
        Class seiClass = method.getDeclaringClass();
        DataObject dataObject = DataFactory.INSTANCE.create(seiClass);
        for (int i = 0; i < args.length; i++) {
            dataObject.set(i, args[i]);
        }
        return dataObject;
    }

}
