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
package org.apache.tuscany.sca.binding.notification.encoding;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.binding.notification.util.IOUtils.IOUtilsException;

/**
 * @version $Rev$ $Date$
 */
public class EncodingUtils {

    private static XMLOutputFactory xof = XMLOutputFactory.newInstance();
    private static XMLInputFactory xif = XMLInputFactory.newInstance();

    public static void encodeToStream(EncodingRegistry encodingRegistry,
                                      EncodingObject eo,
                                      OutputStream os) throws IOUtilsException {
        try {
            XMLStreamWriter writer = xof.createXMLStreamWriter(os);
            encodingRegistry.encode(eo, writer);
            writer.flush();
            writer.close();
        }
        catch(Exception e) {
            throw new IOUtilsException(e);
        }
    }
    
    public static EncodingObject decodeFromStream(EncodingRegistry encodingRegistry,
                                                  InputStream istream) throws EncodingException {
        EncodingObject eo = null;
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(istream);
            reader.next();
            eo = encodingRegistry.decode(reader);
            reader.close();
        }
        catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
        
        return eo;
    }
    
    public static EndpointReference createEndpointReference(URL address, String brokerID) {
        EndpointAddress epa = new EndpointAddress();
        epa.setAddress(address);
        EndpointReference epr = new EndpointReference();
        epr.setEndpointAddress(epa);
        if (brokerID != null) {
            BrokerID bi = new BrokerID();
            bi.setID(brokerID);
            ReferenceProperties rp = new ReferenceProperties();
            rp.addProperty(bi);
            epr.setReferenceProperties(rp);
        }
        return epr;
    }
}
