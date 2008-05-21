/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.databinding.jaxb.axiom.ext;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;

/*
 * To marshal or unmarshal a JAXB object, the JAXBContext is necessary.
 * In addition, access to the MessageContext and other context objects may be necessary
 * to get classloader information, store attachments etc.
 * 
 * The JAXBDSContext bundles all of this information together.
 */
public class JAXBDSContext {

    private static final Logger log = Logger.getLogger(JAXBDSContext.class.getName());
    private static final boolean DEBUG_ENABLED = log.isLoggable(Level.FINER);

    private JAXBContext jaxbContext = null; // JAXBContext

    /**
     * "Dispatch" Constructor Use this full constructor when the JAXBContent is provided by the
     * customer.
     *
     * @param jaxbContext
     */
    public JAXBDSContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    /**
     * Unmarshal the xml into a JAXB object
     * @param reader
     * @return
     * @throws JAXBException
     */
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {

        Unmarshaller u = JAXBContextHelper.getUnmarshaller(getJAXBContext());

        Object jaxb = null;

        // Unmarshal into the business object.
        jaxb = unmarshalElement(u, reader); // preferred and always used for
        // style=document

        // Successfully unmarshalled the object
        // JAXBUtils.releaseJAXBUnmarshaller(getJAXBContext(cl), u);

        // Don't close the reader.  The reader is owned by the caller, and it
        // may contain other xml instance data (other than this JAXB object)
        // reader.close();
        return jaxb;
    }

    /**
     * Marshal the jaxb object
     * @param obj
     * @param writer
     * @param am AttachmentMarshaller, optional Attachment
     */
    public void marshal(Object obj, XMLStreamWriter writer) throws JAXBException {

        // Very easy, use the Context to get the Marshaller.
        // Use the marshaller to write the object.
        Marshaller m = JAXBContextHelper.getMarshaller(getJAXBContext());
        AttachmentMarshaller am = m.getAttachmentMarshaller();
        boolean xop = am != null ? am.isXOPPackage() : false;
        // Marshal the object
        marshalElement(obj, m, writer, !xop);
    }

    /**
     * Preferred way to marshal objects.
     * 
     * @param b Object that can be rendered as an element and the element name is known by the
     * Marshaller
     * @param m Marshaller
     * @param writer XMLStreamWriter
     */
    private static void marshalElement(final Object b,
                                         final Marshaller m,
                                         final XMLStreamWriter writer,
                                         final boolean optimize) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                // Marshalling directly to the output stream is faster than marshalling through the
                // XMLStreamWriter. 
                // Take advantage of this optimization if there is an output stream.
                try {
                    OutputStream os = (optimize) ? getOutputStream(writer) : null;
                    if (os != null) {
                        writer.flush();
                        m.marshal(b, os);
                    } else {
                        m.marshal(b, writer);
                    }
                } catch (OMException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new OMException(t);
                }
                return null;
            }
        });
    }

    /**
     * If the writer is backed by an OutputStream, then return the OutputStream
     * @param writer
     * @return OutputStream or null
     */
    private static OutputStream getOutputStream(XMLStreamWriter writer) throws XMLStreamException {
        if (writer.getClass() == MTOMXMLStreamWriter.class) {
            return ((MTOMXMLStreamWriter)writer).getOutputStream();
        }
        if (writer.getClass() == XMLStreamWriterWithOS.class) {
            return ((XMLStreamWriterWithOS)writer).getOutputStream();
        }
        return null;
    }

    /**
     * Preferred way to unmarshal objects
     * 
     * @param u Unmarshaller
     * @param reader XMLStreamReader
     * @return Object that represents an element
     */
    private static Object unmarshalElement(final Unmarshaller u, final XMLStreamReader reader) {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    try {
                        return u.unmarshal(reader);
                    } catch (OMException e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new OMException(t);
                    }
                }
            });

        } catch (OMException e) {
            throw e;
        } catch (Throwable t) {
            throw new OMException(t);
        }
    }

}
