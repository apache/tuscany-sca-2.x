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
package org.apache.tuscany.sca.databinding.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;


public final class StAXHelper {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private StAXHelper() {
    }

    public static XMLStreamReader createXMLStreamReader(InputStream inputStream) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(inputStream);
    }

    public static XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(reader);
    }

    public static XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(source);
    }

    public static XMLStreamReader createXMLStreamReader(String string) throws XMLStreamException {
        StringReader reader = new StringReader(string);
        return createXMLStreamReader(reader);
    }

    public static String save(XMLStreamReader reader) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        save(reader, writer);
        return writer.toString();
    }

    public static void save(XMLStreamReader reader, OutputStream outputStream) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(outputStream);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public static void save(XMLStreamReader reader, Writer writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public static void save(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        serializer.serialize(reader, writer);
        writer.flush();
    }

}
