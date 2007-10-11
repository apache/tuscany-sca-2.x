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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class XmlTreeStreamReaderTestCase {
    private XmlElementImpl root;

    @Before
    public void setUp() {
        root = new XmlElementImpl();
        root.name = new QName("http://ns", "e1");

        XmlElementImpl e11 = new XmlElementImpl();
        e11.name = new QName("http://ns", "e11");

        XmlElementImpl e12 = new XmlElementImpl();
        e12.name = new QName("http://ns", "e12");

        root.children.add(e11);
        root.children.add(e12);

        XmlElementImpl e121 = new XmlElementImpl();
        e121.name = new QName("http://ns", "e121");
        e12.children.add(e121);

        XmlElementImpl e111 = new XmlElementImpl();
        e111.value = "MyText";
        e11.children.add(e111);

    }

    @Test
    public void testIterator() {
        List<QName> elements = new ArrayList<QName>();
        XmlElementIterator i = new XmlElementIterator(root);
        for (; i.hasNext();) {
            XmlElement e = i.next();
            elements.add(e.getName());
            // System.out.println(e + " " + i.getState());
        }
        QName[] names =
            {new QName("http://ns", "e1"), new QName("http://ns", "e11"), null, null, new QName("http://ns", "e11"),
             new QName("http://ns", "e12"), new QName("http://ns", "e121"), new QName("http://ns", "e121"),
             new QName("http://ns", "e12"), new QName("http://ns", "e1")};
        Assert.assertEquals(Arrays.asList(names), elements);
    }

    @Test
    public void testReader() throws XMLStreamException {
        XmlTreeStreamReaderImpl reader = new XmlTreeStreamReaderImpl(root);
        List<String> seq = new ArrayList<String>();
        while (true) {
            int e = reader.getEventType();
            if (e == XMLStreamConstants.START_DOCUMENT) {
                seq.add("START_DOCUMENT");
            } else if (e == XMLStreamConstants.END_DOCUMENT) {
                seq.add("END_DOCUMENT");
            } else if (e == XMLStreamConstants.CHARACTERS) {
                seq.add(reader.getText());
            } else {
                seq.add(e + ": " + reader.getName());
            }
            if (!reader.hasNext()) {
                break;
            } else {
                reader.next();
            }
        }

        String[] events =
            {"START_DOCUMENT", "1: {http://ns}e1", "1: {http://ns}e11", "MyText", "2: {http://ns}e11",
             "1: {http://ns}e12", "1: {http://ns}e121", "2: {http://ns}e121", "2: {http://ns}e12", "2: {http://ns}e1",
             "END_DOCUMENT"
            };
        
        Assert.assertEquals(Arrays.asList(events), seq);
    }

    private static class XmlElementImpl implements XmlElement {
        private List<XmlElement> children = new ArrayList<XmlElement>();
        private List<XmlAttribute> attrs = new ArrayList<XmlAttribute>();
        private List<QName> namespaces = new ArrayList<QName>();
        private QName name;
        private boolean isLeaf;
        private String value = "123";

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#attributes()
         */
        public List<XmlAttribute> attributes() {
            return attrs;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#children()
         */
        public Iterator<XmlElement> children() {
            return children.iterator();
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#getName()
         */
        public QName getName() {
            return name;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#getValue()
         */
        public String getValue() {
            return value;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#isLeaf()
         */
        public boolean isLeaf() {
            return isLeaf;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlElement#namespaces()
         */
        public List<QName> namespaces() {
            return namespaces;
        }

        public String toString() {
            return String.valueOf(name);
        }

    }
}
