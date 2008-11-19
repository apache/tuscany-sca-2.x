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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * 
 * @version $Rev$ $Date$
 */
public class XmlTreeStreamReaderImpl implements XMLStreamReader {

    protected int state;
    protected XmlNodeIterator iterator;
    protected XmlNode current;

    protected XMLStreamReader reader;

    /*
     * we need to pass in a namespace context since when delegated, we've no
     * idea of the current namespace context. So it needs to be passed on here!
     */
    public XmlTreeStreamReaderImpl(XmlNode root) {
        this.iterator = new XmlNodeIterator(root);
        this.current = null;
        this.state = START_DOCUMENT;
        this.reader = null;
    }

    public void close() throws XMLStreamException {
        if (reader != null) {
            reader.close();
        }
    }

    private void checkElementState() {
        if (getEventType() != START_ELEMENT && getEventType() != END_ELEMENT) {
            throw new IllegalStateException();
        }
    }

    private List<XmlNode> getAttributes() {
        if (current != null && current.attributes() != null) {
            return current.attributes();
        } else {
            return Collections.emptyList();
        }
    }

    public int getAttributeCount() {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeCount();
        }
        return getAttributes().size();
    }

    public String getAttributeLocalName(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeLocalName(i);
        }
        return getAttributes().get(i).getName().getLocalPart();
    }

    /**
     * @param i
     */
    public QName getAttributeName(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeName(i);
        }
        return getAttributes().get(i).getName();
    }

    public String getAttributeNamespace(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeNamespace(i);
        }
        return getAttributes().get(i).getName().getNamespaceURI();
    }

    public String getAttributePrefix(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributePrefix(i);
        }
        return getAttributes().get(i).getName().getPrefix();
    }

    public String getAttributeType(int i) {
        if (reader != null) {
            return reader.getAttributeType(i);
        }
        return null; // not supported
    }

    public String getAttributeValue(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeValue(i);
        }
        return getAttributes().get(i).getValue();
    }

    public String getAttributeValue(String nsUri, String localName) {
        checkElementState();
        if (reader != null) {
            return reader.getAttributeValue(nsUri, localName);
        }
        int count = getAttributeCount();
        String value = null;
        QName attrQName;
        for (int i = 0; i < count; i++) {
            attrQName = getAttributeName(i);
            if (nsUri == null) {
                if (localName.equals(attrQName.getLocalPart())) {
                    value = getAttributeValue(i);
                    break;
                }
            } else {
                if (localName.equals(attrQName.getLocalPart()) && nsUri.equals(attrQName.getNamespaceURI())) {
                    value = getAttributeValue(i);
                    break;
                }
            }

        }

        return value;
    }

    public String getCharacterEncodingScheme() {
        if (reader != null) {
            return reader.getCharacterEncodingScheme();
        }
        return "UTF-8";
    }

    public String getElementText() throws XMLStreamException {
        checkElementState();
        if (reader != null) {
            return reader.getElementText();
        }
        return current.getValue();
    }

    public String getEncoding() {
        if (reader != null) {
            return reader.getEncoding();
        }
        return "UTF-8";
    }

    public int getEventType() {
        return state;
    }

    public String getLocalName() {
        checkElementState();
        if (reader != null) {
            return reader.getLocalName();
        }
        return current.getName().getLocalPart();
    }

    /**
     */
    public Location getLocation() {
        if (reader != null) {
            return reader.getLocation();
        }
        // return a default location
        return new Location() {
            public int getCharacterOffset() {
                return 0;
            }

            public int getColumnNumber() {
                return 0;
            }

            public int getLineNumber() {
                return 0;
            }

            public String getPublicId() {
                return null;
            }

            public String getSystemId() {
                return null;
            }
        };
    }

    public QName getName() {
        checkElementState();
        if (reader != null) {
            return reader.getName();
        }
        return current.getName();
    }

    public NamespaceContext getNamespaceContext() {
        if (reader != null) {
            return reader.getNamespaceContext();
        }
        return iterator.getNamespaceContext();
    }

    private Map<String, String> getNamespaces() {
        if (current != null && current.namespaces() != null) {
            return current.namespaces();
        } else {
            return Collections.emptyMap();
        }
    }

    public int getNamespaceCount() {
        checkElementState();
        if (reader != null) {
            return reader.getNamespaceCount();
        }
        return getNamespaces().size();
    }

    /**
     * @param i
     */
    public String getNamespacePrefix(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getNamespacePrefix(i);
        }
        return new ArrayList<Map.Entry<String, String>>(getNamespaces().entrySet()).get(i).getKey();
    }

    public String getNamespaceURI() {
        checkElementState();
        if (reader != null) {
            return reader.getNamespaceURI();
        }
        return current.getName().getNamespaceURI();
    }

    public String getNamespaceURI(int i) {
        checkElementState();
        if (reader != null) {
            return reader.getNamespaceURI(i);
        }
        return new ArrayList<Map.Entry<String, String>>(getNamespaces().entrySet()).get(i).getValue();
    }

    public String getNamespaceURI(String prefix) {
        if (reader != null) {
            return reader.getNamespaceURI(prefix);
        }
        return getNamespaceContext().getNamespaceURI(prefix);
    }

    public String getPIData() {
        if (reader != null) {
            return reader.getPIData();
        }
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPITarget() {
        if (reader != null) {
            return reader.getPITarget();
        }
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPrefix() {
        if (reader != null) {
            return reader.getPrefix();
        }
        if (state == START_ELEMENT || state == END_ELEMENT) {
            String prefix = current.getName().getPrefix();
            return "".equals(prefix) ? null : prefix;
        } else if (state == START_DOCUMENT) {
            return null;
        } else {
            throw new IllegalStateException("State==" + state);
        }
    }

    /**
     * @param key
     * @throws IllegalArgumentException
     */
    public Object getProperty(String key) throws IllegalArgumentException {
        if (reader != null) {
            return reader.getProperty(key);
        }
        return null;
    }

    public String getText() {
        if (reader != null) {
            return reader.getText();
        }
        return current.getValue();
    }

    public char[] getTextCharacters() {
        if (reader != null) {
            return reader.getTextCharacters();
        }
        String value = current.getValue();
        return value == null ? new char[0] : value.toCharArray();
    }

    private int copy(int sourceStart, char[] target, int targetStart, int length) {
        char[] source = getTextCharacters();
        if (sourceStart > source.length) {
            throw new IndexOutOfBoundsException("source start > source length");
        }
        int sourceLen = source.length - sourceStart;
        if (length > sourceLen) {
            length = sourceLen;
        }
        System.arraycopy(source, sourceStart, target, targetStart, length);
        return sourceLen;
    }

    public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
        if (reader != null) {
            return reader.getTextCharacters(i, chars, i1, i2);
        }
        return copy(i, chars, i1, i2);
    }

    public int getTextLength() {
        if (reader != null) {
            return reader.getTextLength();
        }
        return getTextCharacters().length;
    }

    public int getTextStart() {
        if (reader != null) {
            return reader.getTextStart();
        }
        return 0;
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean hasName() {
        if (reader != null) {
            return reader.hasName();
        }
        return current.getName() != null;
    }

    /**
     * @throws XMLStreamException
     */
    public boolean hasNext() throws XMLStreamException {
        return iterator.hasNext() || state != END_DOCUMENT || (reader != null && reader.hasNext());
    }

    public boolean hasText() {
        if (reader != null) {
            return reader.hasText();
        }
        return current.getType() == XmlNode.Type.CHARACTERS;
    }

    public boolean isAttributeSpecified(int i) {
        if (reader != null) {
            return reader.isAttributeSpecified(i);
        }
        return false; // not supported
    }

    public boolean isCharacters() {
        if (reader != null) {
            return reader.isCharacters();
        }
        return current.getType() == XmlNode.Type.CHARACTERS;
    }

    public boolean isEndElement() {
        if (reader != null) {
            return reader.isEndElement();
        }
        return getEventType() == END_ELEMENT;
    }

    public boolean isStandalone() {
        return true;
    }

    public boolean isStartElement() {
        if (reader != null) {
            return reader.isStartElement();
        }
        return getEventType() == START_ELEMENT;
    }

    public boolean isWhiteSpace() {
        if (reader != null) {
            return reader.isWhiteSpace();
        }
        return false;
    }

    /**
     * By far this should be the most important method in this class this method
     * changes the state of the parser
     */
    public int next() throws XMLStreamException {
        if (!hasNext()) {
            throw new IllegalStateException("No more events");
        }
        if (reader != null) {
            if (!reader.hasNext()) {
                this.reader = null;
            } else {
                // Go to the delegation mode
                state = reader.next();
                return state;
            }
        }
        if (!iterator.hasNext()) {
            state = END_DOCUMENT;
            current = null;
            return state;
        }
        current = iterator.next();
        XmlNode.Type type = current.getType();

        int itState = iterator.getState();
        if (itState == XmlNodeIterator.END) {
            if (type == XmlNode.Type.ELEMENT) {
                state = END_ELEMENT;
            } else {
                // Ignore the pop
                state = next();
            }
        }
        if (itState == XmlNodeIterator.START) {
            if (type == XmlNode.Type.ELEMENT) {
                state = START_ELEMENT;
            } else if (type == XmlNode.Type.CHARACTERS) {
                state = CHARACTERS;
            } else if (type == XmlNode.Type.READER) {
                XMLStreamReader value = current.getValue();
                this.reader = new WrappingXMLStreamReader(value);
                state = reader.getEventType();
                return state;
            }
        }
        return state;
    }

    /**
     * TODO implement this
     * 
     * @throws XMLStreamException
     */
    public int nextTag() throws XMLStreamException {
        while (true) {
            int event = next();
            if (event == START_ELEMENT || event == END_ELEMENT) {
                return event;
            }
        }
    }

    public void require(int i, String ns, String localPart) throws XMLStreamException {
        if (reader != null) {
            reader.require(i, ns, localPart);
            return;
        }
        int event = getEventType();
        if (event != i) {
            throw new IllegalStateException("Event type is " + event + " (!=" + i + ")");
        }
        QName name = getName();
        String ns1 = name.getNamespaceURI();
        String localName1 = name.getLocalPart();

        if (ns != null && !ns.equals(ns1)) {
            throw new IllegalStateException("Namespace URI is " + ns1 + " (!=" + ns + ")");
        }

        if (localPart != null && !localPart.equals(localName1)) {
            throw new IllegalStateException("Local name is " + localName1 + " (!=" + localPart + ")");
        }

    }

    public boolean standaloneSet() {
        return true;
    }

}
