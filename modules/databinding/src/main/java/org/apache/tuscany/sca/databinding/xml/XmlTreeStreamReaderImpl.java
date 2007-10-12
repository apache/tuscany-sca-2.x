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
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/**
 * 
 * @version $Rev$ $Date$
 */
public class XmlTreeStreamReaderImpl implements XMLFragmentStreamReader {

    protected int state;
    protected XmlNodeIterator iterator;
    protected XmlNode current;

    /*
     * we need to pass in a namespace context since when delegated, we've no
     * idea of the current namespace context. So it needs to be passed on here!
     */
    public XmlTreeStreamReaderImpl(XmlNode root) {
        this.iterator = new XmlNodeIterator(root);
        this.current = null;
        this.state = START_DOCUMENT;
    }

    public void close() throws XMLStreamException {
        // do nothing here - we have no resources to free
    }

    private void checkElementState() {
        if (current == null || current.getName() == null) {
            throw new IllegalStateException();
        }
    }

    public int getAttributeCount() {
        checkElementState();
        return current.attributes().size();
    }

    public String getAttributeLocalName(int i) {
        checkElementState();
        return current.attributes().get(i).getName().getLocalPart();
    }

    /**
     * @param i
     */
    public QName getAttributeName(int i) {
        checkElementState();
        return current.attributes().get(i).getName();
    }

    public String getAttributeNamespace(int i) {
        checkElementState();
        return current.attributes().get(i).getName().getNamespaceURI();
    }

    public String getAttributePrefix(int i) {
        checkElementState();
        return current.attributes().get(i).getName().getPrefix();
    }

    public String getAttributeType(int i) {
        return null; // not supported
    }

    public String getAttributeValue(int i) {
        checkElementState();
        return current.attributes().get(i).getValue();
    }

    public String getAttributeValue(String nsUri, String localName) {
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
        return null; // todo - should we return something for this ?
    }

    public String getElementText() throws XMLStreamException {
        checkElementState();
        return current.getValue();
    }

    public String getEncoding() {
        return "UTF-8";
    }

    public int getEventType() {
        return state;
    }

    public String getLocalName() {
        checkElementState();
        return current.getName().getLocalPart();
    }

    /**
     */
    public Location getLocation() {
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
        return current.getName();
    }

    public NamespaceContext getNamespaceContext() {
        return iterator.getNamespaceContext();
    }

    public int getNamespaceCount() {
        checkElementState();
        return current.namespaces().size();
    }

    /**
     * @param i
     */
    public String getNamespacePrefix(int i) {
        checkElementState();
        return new ArrayList<Map.Entry<String, String>>(current.namespaces().entrySet()).get(i).getKey();
    }

    public String getNamespaceURI() {
        return current.getName().getNamespaceURI();
    }

    public String getNamespaceURI(int i) {
        checkElementState();
        return new ArrayList<Map.Entry<String, String>>(current.namespaces().entrySet()).get(i).getValue();
    }

    public String getNamespaceURI(String prefix) {
        return getNamespaceContext().getNamespaceURI(prefix);
    }

    public String getPIData() {
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPITarget() {
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPrefix() {
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
        return null;
    }

    public String getText() {
        return current.getValue();
    }

    public char[] getTextCharacters() {
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
        return copy(i, chars, i1, i2);
    }

    public int getTextLength() {
        return getTextCharacters().length;
    }

    public int getTextStart() {
        return 0;
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean hasName() {
        return current.getName() != null;
    }

    /**
     * @throws XMLStreamException
     */
    public boolean hasNext() throws XMLStreamException {
        return iterator.hasNext() || state != END_DOCUMENT;
    }

    public boolean hasText() {
        return current.getName() == null;
    }

    /**
     * we need to split out the calling to the populate namespaces seperately
     * since this needs to be done *after* setting the parent namespace context.
     * We cannot assume it will happen at construction!
     */
    public void init() {
        // here we have an extra issue to attend to. we need to look at the
        // prefixes and uris (the combination) and populate a hashmap of
        // namespaces. The hashmap of namespaces will be used to serve the
        // namespace context

        // populateNamespaceContext();
    }

    public boolean isAttributeSpecified(int i) {
        return false; // not supported
    }

    public boolean isCharacters() {
        return current.getName() == null;
    }

    /**
     * are we done ?
     */
    public boolean isDone() {
        return !iterator.hasNext();
    }

    public boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    public boolean isStandalone() {
        return true;
    }

    public boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    public boolean isWhiteSpace() {
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
        if (!iterator.hasNext()) {
            state = END_DOCUMENT;
            current = null;
            return state;
        }
        current = iterator.next();
        int itState = iterator.getState();
        if (itState == XmlNodeIterator.END) {
            if (current.getName() != null) {
                state = END_ELEMENT;
            } else {
                state = next();
            }
        }
        if (itState == XmlNodeIterator.START) {
            if (current.getName() != null) {
                state = START_ELEMENT;
            } else {
                state = CHARACTERS;
            }
        }
        return state;
    }

    /**
     * todo implement this
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

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XMLFragmentStreamReader#setParentNamespaceContext(javax.xml.namespace.NamespaceContext)
     */
    public void setParentNamespaceContext(NamespaceContext nsContext) {
        // TODO Auto-generated method stub

    }

}
