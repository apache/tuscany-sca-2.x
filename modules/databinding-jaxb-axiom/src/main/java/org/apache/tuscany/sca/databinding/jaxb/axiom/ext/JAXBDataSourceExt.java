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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.util.StAXUtils;

/**
 * OMDataSource backed by a jaxb object
 */
public class JAXBDataSourceExt extends OMDataSourceExtBase {

    private static final Logger log = Logger.getLogger(JAXBDataSourceExt.class.getName());

    private Object jaxb;
    private JAXBDSContext context;

    public JAXBDataSourceExt(Object jaxb, JAXBDSContext context) {
        super();
        this.jaxb = jaxb;
        this.context = context;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        return new JAXBDataSourceExt(jaxb, context);
    }

    public Object getObject() {
        return jaxb;
    }

    public JAXBDSContext getContext() {
        return context;
    }

    public XMLStreamReader getReader() throws XMLStreamException {

        try {
            String encoding = "utf-8";
            InputStream is = new ByteArrayInputStream(getXMLBytes(encoding));
            return StAXUtils.createXMLStreamReader(is, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        serialize(writer);
        writer.flush();
        try {
            writer.close();
        } catch (XMLStreamException e) {
            // An exception can occur if nothing is written to the 
            // writer.  This is possible if the underlying data source
            // writers to the output stream directly.
            if (log.isLoggable(Level.FINER)) {
                log.finer("Catching and swallowing exception " + e);
            }
        }
    }

    public void serialize(Writer writerTarget, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writerTarget));
        writer.setOutputFormat(format);
        serialize(writer);
        writer.flush();
        writer.close();
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        try {
            context.marshal(jaxb, xmlWriter);
        } catch (JAXBException je) {
            throw new XMLStreamException(je);
        }
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Exposes getOutputStream, which allows faster writes.
            XMLStreamWriterWithOS writer = new XMLStreamWriterWithOS(baos, encoding);

            // Write the business object to the writer
            serialize(writer);

            // Flush the writer
            writer.flush();
            writer.close();
            return baos.toByteArray();
        } catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

}
