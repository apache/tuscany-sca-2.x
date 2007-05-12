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

package org.apache.tuscany.databinding.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.xml.InputSource2Node;
import org.apache.tuscany.sca.databinding.xml.InputSource2SAX;
import org.apache.tuscany.sca.databinding.xml.InputStream2Node;
import org.apache.tuscany.sca.databinding.xml.InputStream2SAX;
import org.apache.tuscany.sca.databinding.xml.Node2OutputStream;
import org.apache.tuscany.sca.databinding.xml.Node2Writer;
import org.apache.tuscany.sca.databinding.xml.Reader2Node;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 */
public class TraxTransformerTestCase extends TestCase {
    private URL url;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        url = getClass().getResource("foo.xml");
    }

    public void testTransformDOM() throws IOException {
        InputStream is = url.openStream();
        InputStream2Node t1 = new InputStream2Node();
        Node node = t1.transform(is, null);
        is.close();
        Writer writer = new StringWriter();
        Node2Writer t2 = new Node2Writer();
        t2.transform(node, writer, null);
        String str = writer.toString();
        StringReader reader = new StringReader(str);
        Reader2Node t3 = new Reader2Node();
        node = t3.transform(reader, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Node2OutputStream t4 = new Node2OutputStream();
        t4.transform(node, os, null);
        InputSource inputSource = new InputSource(new ByteArrayInputStream(os.toByteArray()));
        InputSource2Node t5 = new InputSource2Node();
        node = t5.transform(inputSource, null);
    }

    public void testTransformSAX() throws IOException {
        MyContentHandler handler = new MyContentHandler();
        InputStream is = url.openStream();
        InputStream2SAX t1 = new InputStream2SAX();
        t1.transform(is, handler, null);
        is.close();

        String xml = "<foo xmlns=\"http://foo\">bar</foo>";

        InputSource inputSource = new InputSource(new StringReader(xml));
        InputSource2SAX t2 = new InputSource2SAX();
        MyContentHandler handler2 = new MyContentHandler();
        t2.transform(inputSource, handler2, null);

    }

    private static class MyContentHandler extends DefaultHandler {

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
            super.startElement(namespaceURI, localName, qName, atts);
        }

    }

}
