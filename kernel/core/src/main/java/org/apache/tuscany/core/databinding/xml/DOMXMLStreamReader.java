/**
 *
 *  Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.databinding.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.tuscany.core.databinding.xml.StAXHelper.XMLFragmentStreamReader;

public class DOMXMLStreamReader implements XMLFragmentStreamReader {
    protected static class DelegatingNamespaceContext implements NamespaceContext {
        private int counter;

        private NamespaceContext parent;

        private Map<String, String> prefixToNamespaceMapping = new HashMap<String, String>();

        public DelegatingNamespaceContext(NamespaceContext parent) {
            super();
            this.parent = parent;

            prefixToNamespaceMapping.put("xml", "http://www.w3.org/XML/1998/namespace");
            prefixToNamespaceMapping.put("xmlns", "http://www.w3.org/2000/xmlns/");
            prefixToNamespaceMapping.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        }

        public synchronized QName createQName(String nsURI, String name) {
            String prefix = nsURI != null ? (String) getPrefix(nsURI) : null;
            if (prefix == null && nsURI != null && !nsURI.equals("")) {
                prefix = "p" + (counter++);
            }
            if (prefix == null) {
                prefix = "";
            }
            if (nsURI != null) {
                prefixToNamespaceMapping.put(prefix, nsURI);
            }
            return new QName(nsURI, name, prefix);
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            }

            String ns = (String) prefixToNamespaceMapping.get(prefix);
            if (ns != null) {
                return ns;
            } else if (parent != null) {
                return parent.getNamespaceURI(prefix);
            } else {
                return null;
            }
        }

        public String getPrefix(String nsURI) {
            if (nsURI == null) {
                throw new IllegalArgumentException("Namespace is null");
            }
            for (Map.Entry<String, String> entry1 : prefixToNamespaceMapping.entrySet()) {
                Map.Entry entry = entry1;
                if (entry.getValue().equals(nsURI)) {
                    return (String) entry.getKey();
                }
            }
            if (parent != null) {
                return parent.getPrefix(nsURI);
            } else {
                return null;
            }
        }

        public Iterator getPrefixes(String nsURI) {
            List<String> prefixList = new ArrayList<String>();
            for (Map.Entry<String, String> entry : prefixToNamespaceMapping.entrySet()) {
                if (entry.getValue().equals(nsURI)) {
                    prefixList.add(entry.getKey());
                }
            }
            if (parent != null) {
                for (Iterator i = parent.getPrefixes(nsURI); i.hasNext();) {
                    prefixList.add((String) i.next());
                }
            }
            return prefixList.iterator();
        }

        public void registerMapping(String prefix, String nsURI) {
            prefixToNamespaceMapping.put(prefix, nsURI);
        }

        public void removeMapping(String prefix) {
            prefixToNamespaceMapping.remove(prefix);
        }

        public void setParent(NamespaceContext parent) {
            this.parent = parent;
        }
    }

    protected static class NameValuePair implements Map.Entry {
        private Object key;

        private Object value;

        public NameValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            Object v = this.value;
            this.value = value;
            return v;
        }

    }

    protected static class SimpleElementStreamReader implements XMLFragmentStreamReader {

        private static final int END_ELEMENT_STATE = 2;

        private static final int START_ELEMENT_STATE = 0;

        private static final int START_ELEMENT_STATE_WITH_NULL = 3;

        private static final int TEXT_STATE = 1;

        private static final QName XSI_NIL_QNAME =
            new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");

        private QName name;

        private DelegatingNamespaceContext namespaceContext = new DelegatingNamespaceContext(null);

        private int state = START_ELEMENT_STATE;

        private String value;

        public SimpleElementStreamReader(QName name, String value) {
            this.name = name;
            this.value = value;
            if (value == null) {
                state = START_ELEMENT_STATE_WITH_NULL;
            }
        }

        public void close() throws XMLStreamException {
            // Do nothing - we've nothing to free here
        }

        public int getAttributeCount() {
            if (state == START_ELEMENT_STATE_WITH_NULL) {
                return 1;
            }
            if (state == START_ELEMENT_STATE) {
                return 0;
            } else {
                throw new IllegalStateException();
            }

        }

        public String getAttributeLocalName(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && i == 0) {
                return XSI_NIL_QNAME.getLocalPart();
            }
            if (state == START_ELEMENT_STATE) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public QName getAttributeName(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && i == 0) {
                return XSI_NIL_QNAME;
            }
            if (state == START_ELEMENT_STATE) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeNamespace(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && i == 0) {
                return XSI_NIL_QNAME.getNamespaceURI();
            }
            if (state == START_ELEMENT_STATE) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributePrefix(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && i == 0) {
                return XSI_NIL_QNAME.getPrefix();
            }
            if (state == START_ELEMENT_STATE) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeType(int i) {
            return null; // not implemented
        }

        public String getAttributeValue(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && i == 0) {
                return "true";
            }
            if (state == START_ELEMENT_STATE) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeValue(String string, String string1) {
            if (state == TEXT_STATE) {
                // todo something
                return null;
            } else {
                return null;
            }

        }

        public String getCharacterEncodingScheme() {
            return null;
        }

        public String getElementText() throws XMLStreamException {
            if (state == START_ELEMENT) {
                // move to the end state and return the value
                state = END_ELEMENT_STATE;
                return value;
            } else {
                throw new XMLStreamException();
            }

        }

        public String getEncoding() {
            return "UTF-8";
        }

        public int getEventType() {
            switch (state) {
                case START_ELEMENT_STATE:
                case START_ELEMENT_STATE_WITH_NULL:
                    return START_ELEMENT;
                case END_ELEMENT_STATE:
                    return END_ELEMENT;
                case TEXT_STATE:
                    return CHARACTERS;
                default:
                    throw new UnsupportedOperationException();
                    // we've no idea what this is!!!!!
            }

        }

        public String getLocalName() {
            if (state != TEXT_STATE) {
                return name.getLocalPart();
            } else {
                return null;
            }
        }

        public Location getLocation() {
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
            if (state != TEXT_STATE) {
                return name;
            } else {
                return null;
            }
        }

        public NamespaceContext getNamespaceContext() {
            return this.namespaceContext;
        }

        public int getNamespaceCount() {
            if (state == START_ELEMENT_STATE_WITH_NULL && isXsiNamespacePresent()) {
                return 1;
            } else {
                return 0;
            }

        }

        public String getNamespacePrefix(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && isXsiNamespacePresent() && i == 0) {
                return XSI_NIL_QNAME.getPrefix();
            } else {
                return null;
            }
        }

        public String getNamespaceURI() {
            if (state != TEXT_STATE) {
                return name.getNamespaceURI();
            } else {
                return null;
            }

        }

        public String getNamespaceURI(int i) {
            if (state == START_ELEMENT_STATE_WITH_NULL && isXsiNamespacePresent() && i == 0) {
                return XSI_NIL_QNAME.getNamespaceURI();
            } else {
                return null;
            }
        }

        public String getNamespaceURI(String prefix) {
            return namespaceContext.getNamespaceURI(prefix);
        }

        public String getPIData() {
            return null;
        }

        public String getPITarget() {
            return null;
        }

        public String getPrefix() {
            if (state != TEXT_STATE) {
                return name.getPrefix();
            } else {
                return null;
            }
        }

        public Object getProperty(String key) throws IllegalArgumentException {
            return null;
        }

        public String getText() {
            if (state == TEXT_STATE) {
                return value;
            } else {
                throw new IllegalStateException();
            }
        }

        public char[] getTextCharacters() {
            if (state == TEXT_STATE) {
                return value.toCharArray();
            } else {
                throw new IllegalStateException();
            }
        }

        public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
            // not implemented
            throw new UnsupportedOperationException();
        }

        public int getTextLength() {
            if (state == TEXT_STATE) {
                return value.length();
            } else {
                throw new IllegalStateException();
            }

        }

        public int getTextStart() {
            if (state == TEXT_STATE) {
                return 0;
            } else {
                throw new IllegalStateException();
            }
        }

        public String getVersion() {
            return null; // todo 1.0 ?
        }

        public boolean hasName() {
            return state != TEXT_STATE;

        }

        public boolean hasNext() throws XMLStreamException {
            return state != END_ELEMENT_STATE;
        }

        public boolean hasText() {
            return state == TEXT_STATE;
        }

        public void init() {
            // just add the current elements namespace and prefix to the this
            // elements nscontext
            registerNamespace(name.getPrefix(), name.getNamespaceURI());

        }

        public boolean isAttributeSpecified(int i) {
            return false; // no attribs here
        }

        public boolean isCharacters() {
            return state == TEXT_STATE;
        }

        public boolean isEndElement() {
            return state == END_ELEMENT_STATE;
        }

        public boolean isEndOfFragment() {
            return state == END_ELEMENT_STATE;
        }

        public boolean isStandalone() {
            return false;
        }

        public boolean isStartElement() {
            return state == START_ELEMENT_STATE || state == START_ELEMENT_STATE_WITH_NULL;
        }

        public boolean isWhiteSpace() {
            return false; // no whitespaces here
        }

        /**
         * Test whether the xsi namespace is present
         *
         * @return
         */
        private boolean isXsiNamespacePresent() {
            return namespaceContext.getNamespaceURI(XSI_NIL_QNAME.getPrefix()) != null;
        }

        public int next() throws XMLStreamException {
            switch (state) {
                case START_ELEMENT_STATE:
                    state = TEXT_STATE;
                    return CHARACTERS;
                case START_ELEMENT_STATE_WITH_NULL:
                    state = END_ELEMENT_STATE;
                    return END_ELEMENT;
                case END_ELEMENT_STATE:
                    // oops, not supposed to happen!
                    throw new XMLStreamException("end already reached!");
                case TEXT_STATE:
                    state = END_ELEMENT_STATE;
                    return END_ELEMENT;
                default:
                    throw new XMLStreamException("unknown event type!");
            }
        }

        public int nextTag() throws XMLStreamException {
            return 0; // todo
        }

        /**
         * @param prefix
         * @param uri
         */
        private void registerNamespace(String prefix, String uri) {
            // todo - need to fix this up to cater for cases where
            // namespaces are having no prefixes
            if (!uri.equals(namespaceContext.getNamespaceURI(prefix))) {
                // this namespace is not there. Need to declare it
                namespaceContext.registerMapping(prefix, uri);
            }
        }

        public void require(int i, String string, String string1) throws XMLStreamException {
            // not implemented
        }

        public void setParentNamespaceContext(NamespaceContext nsContext) {
            this.namespaceContext.setParent(nsContext);
        }

        public boolean standaloneSet() {
            return false;
        }

    }

    private static final int DELEGATED_STATE = 2;

    private static final int END_ELEMENT_STATE = 1;

    // states for this pullparser - it can only have three states
    private static final int START_ELEMENT_STATE = 0;

    private static final int TEXT_STATE = 3;

    private Map.Entry[] attributes;

    // reference to the child reader
    private XMLFragmentStreamReader childReader;

    // current property index
    private int currentPropertyIndex;

    private Map<String, String> declaredNamespaceMap = new HashMap<String, String>();

    private QName elementQName;

    // we always create a new namespace context
    private DelegatingNamespaceContext namespaceContext = new DelegatingNamespaceContext(null);

    private Map.Entry[] properties;

    private Element rootElement;

    private String rootElementName;

    private String rootElementURI;

    // integer field that keeps the state of this
    // parser.
    private int state = START_ELEMENT_STATE;

    public DOMXMLStreamReader(Node node) {
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                this.rootElement = ((Document) node).getDocumentElement();
                break;
            case Node.ELEMENT_NODE:
                this.rootElement = (Element) node;
                break;
            default:
                throw new IllegalArgumentException("Illegal Node");
        }
        this.rootElementName = rootElement.getLocalName();
        this.rootElementURI = rootElement.getNamespaceURI();

        declaredNamespaceMap.put("xml", "http://www.w3.org/XML/1998/namespace");
        declaredNamespaceMap.put("xmlns", "http://www.w3.org/2000/xmlns/");
        declaredNamespaceMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        populateProperties();
    }

    /*
     * we need to pass in a namespace context since when delegated, we've no
     * idea of the current namespace context. So it needs to be passed on here!
     */
    protected DOMXMLStreamReader(QName elementQName, Map.Entry[] properties, Map.Entry[] attributes) {
        // validate the lengths, since both the arrays are supposed
        // to have
        this.properties = properties;
        this.elementQName = elementQName;
        this.attributes = attributes;

    }

    public void close() throws XMLStreamException {
        // do nothing here - we have no resources to free
    }

    public int getAttributeCount() {
        return (state == DELEGATED_STATE) ? childReader.getAttributeCount()
            : ((attributes != null) && (state == START_ELEMENT_STATE) ? attributes.length : 0);
    }

    public String getAttributeLocalName(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributeLocalName(i);
        } else if (state == START_ELEMENT_STATE) {
            QName name = getAttributeName(i);
            if (name == null) {
                return null;
            } else {
                return name.getLocalPart();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * @param i
     * @return
     */
    public QName getAttributeName(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributeName(i);
        } else if (state == START_ELEMENT_STATE) {
            if (attributes == null) {
                return null;
            } else {
                if ((i >= (attributes.length)) || i < 0) { // out of range
                    return null;
                } else {
                    // get the attribute pointer
                    Object attribPointer = attributes[i].getKey();
                    // case one - attrib name is null
                    // this should be the pointer to the OMAttribute then
                    if (attribPointer instanceof String) {
                        return new QName((String) attribPointer);
                    } else if (attribPointer instanceof QName) {
                        return (QName) attribPointer;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            throw new IllegalStateException(); // as per the api contract
        }

    }

    public String getAttributeNamespace(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributeNamespace(i);
        } else if (state == START_ELEMENT_STATE) {
            QName name = getAttributeName(i);
            if (name == null) {
                return null;
            } else {
                return name.getNamespaceURI();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public String getAttributePrefix(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributePrefix(i);
        } else if (state == START_ELEMENT_STATE) {
            QName name = getAttributeName(i);
            if (name == null) {
                return null;
            } else {
                return name.getPrefix();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public String getAttributeType(int i) {
        return null; // not supported
    }

    public String getAttributeValue(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributeValue(i);
        } else if (state == START_ELEMENT_STATE) {
            if (attributes == null) {
                return null;
            } else {
                if ((i >= (attributes.length)) || i < 0) { // out of range
                    return null;
                } else {
                    // get the attribute pointer
                    Object attribPointer = attributes[i].getKey();
                    Object omAttribObj = attributes[i].getValue();
                    // case one - attrib name is null
                    // this should be the pointer to the OMAttribute then
                    if (attribPointer instanceof String) {
                        return (String) omAttribObj;
                    } else if (attribPointer instanceof QName) {
                        return (String) omAttribObj;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            throw new IllegalStateException();
        }

    }

    public String getAttributeValue(String nsUri, String localName) {

        int attribCount = getAttributeCount();
        String returnValue = null;
        QName attribQualifiedName;
        for (int i = 0; i < attribCount; i++) {
            attribQualifiedName = getAttributeName(i);
            if (nsUri == null) {
                if (localName.equals(attribQualifiedName.getLocalPart())) {
                    returnValue = getAttributeValue(i);
                    break;
                }
            } else {
                if (localName.equals(attribQualifiedName.getLocalPart()) && nsUri.equals(attribQualifiedName
                    .getNamespaceURI())) {
                    returnValue = getAttributeValue(i);
                    break;
                }
            }

        }

        return returnValue;
    }

    public String getCharacterEncodingScheme() {
        return null; // todo - should we return something for this ?
    }

    /**
     * todo implement the right contract for this
     *
     * @return
     * @throws XMLStreamException
     */
    public String getElementText() throws XMLStreamException {
        if (state == DELEGATED_STATE) {
            return childReader.getElementText();
        } else {
            return null;
        }

    }

    public String getEncoding() {
        if (state == DELEGATED_STATE) {
            return childReader.getEncoding();
        } else {
            // we've no idea what the encoding is going to be in this case
            // perhaps we ought to return some constant here, which the user
            // might
            // have access to change!
            return null;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // / attribute handling
    // /////////////////////////////////////////////////////////////////////////

    public int getEventType() {
        if (state == START_ELEMENT_STATE) {
            return START_ELEMENT;
        } else if (state == END_ELEMENT_STATE) {
            return END_ELEMENT;
        } else { // this is the delegated state
            return childReader.getEventType();
        }

    }

    public String getLocalName() {
        if (state == DELEGATED_STATE) {
            return childReader.getLocalName();
        } else if (state != TEXT_STATE) {
            return elementQName.getLocalPart();
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * @return
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
        if (state == DELEGATED_STATE) {
            return childReader.getName();
        } else if (state != TEXT_STATE) {
            return elementQName;
        } else {
            throw new IllegalStateException();
        }

    }

    public NamespaceContext getNamespaceContext() {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespaceContext();
        } else {
            return namespaceContext;
        }

    }

    public int getNamespaceCount() {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespaceCount();
        } else {
            return declaredNamespaceMap.size();
        }
    }

    /**
     * @param i
     * @return
     */
    public String getNamespacePrefix(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespacePrefix(i);
        } else if (state != TEXT_STATE) {
            // order the prefixes
            String[] prefixes = makePrefixArray();
            if ((i >= prefixes.length) || (i < 0)) {
                return null;
            } else {
                return prefixes[i];
            }

        } else {
            throw new IllegalStateException();
        }

    }

    public String getNamespaceURI() {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespaceURI();
        } else if (state == TEXT_STATE) {
            return null;
        } else {
            return elementQName.getNamespaceURI();
        }
    }

    public String getNamespaceURI(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespaceURI(i);
        } else if (state != TEXT_STATE) {
            String namespacePrefix = getNamespacePrefix(i);
            return namespacePrefix == null ? null : (String) declaredNamespaceMap.get(namespacePrefix);
        } else {
            throw new IllegalStateException();
        }

    }

    // /////////////////////////////////////////////////////////////////////////
    // //////////// end of attribute handling
    // /////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////
    // //////////// namespace handling
    // //////////////////////////////////////////////////////////////////////////

    public String getNamespaceURI(String prefix) {
        return namespaceContext.getNamespaceURI(prefix);
    }

    public String getPIData() {
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPITarget() {
        throw new UnsupportedOperationException("Yet to be implemented !!");
    }

    public String getPrefix() {
        if (state == DELEGATED_STATE) {
            return childReader.getPrefix();
        } else if (state == TEXT_STATE) {
            return null;
        } else {
            return elementQName.getPrefix();
        }
    }

    /**
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public Object getProperty(String key) throws IllegalArgumentException {
        if (state == START_ELEMENT_STATE || state == END_ELEMENT_STATE) {
            return null;
        } else if (state == TEXT_STATE) {
            return null;
        } else if (state == DELEGATED_STATE) {
            return childReader.getProperty(key);
        } else {
            return null;
        }

    }

    // /////////////////////////////////////////////////////////////////////////
    // /////// end of namespace handling
    // /////////////////////////////////////////////////////////////////////////

    public String getText() {
        if (state == DELEGATED_STATE) {
            return childReader.getText();
        } else if (state == TEXT_STATE) {
            return (String) properties[currentPropertyIndex - 1].getValue();
        } else {
            throw new IllegalStateException();
        }
    }

    public char[] getTextCharacters() {
        if (state == DELEGATED_STATE) {
            return childReader.getTextCharacters();
        } else if (state == TEXT_STATE) {
            return properties[currentPropertyIndex - 1].getValue() == null ? new char[0]
                : ((String) properties[currentPropertyIndex - 1].getValue()).toCharArray();
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
        if (state == DELEGATED_STATE) {
            return childReader.getTextCharacters(i, chars, i1, i2);
        } else if (state == TEXT_STATE) {
            // todo - implement this
            return 0;
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextLength() {
        if (state == DELEGATED_STATE) {
            return childReader.getTextLength();
        } else if (state == TEXT_STATE) {
            return 0; // assume text always starts at 0
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextStart() {
        if (state == DELEGATED_STATE) {
            return childReader.getTextStart();
        } else if (state == TEXT_STATE) {
            return 0; // assume text always starts at 0
        } else {
            throw new IllegalStateException();
        }
    }

    public String getVersion() {
        return null;
    }

    public boolean hasName() {
        // since this parser always has a name, the hasname
        // has to return true if we are still navigating this element
        // if not we should ask the child reader for it.
        if (state == DELEGATED_STATE) {
            return childReader.hasName();
        } else {
            return state != TEXT_STATE;
        }
    }

    /**
     * @return
     * @throws XMLStreamException
     */
    public boolean hasNext() throws XMLStreamException {
        if (state == DELEGATED_STATE) {
            if (childReader.isEndOfFragment()) {
                // the child reader is done. We shouldn't be getting the
                // hasnext result from the child pullparser then
                return true;
            } else {
                return childReader.hasNext();
            }
        } else {
            return state == START_ELEMENT_STATE || state == TEXT_STATE;

        }
    }

    /**
     * check the validity of this implementation
     *
     * @return
     */
    public boolean hasText() {
        if (state == DELEGATED_STATE) {
            return childReader.hasText();
        } else {
            return state == TEXT_STATE;
        }
    }

    /**
     * we need to split out the calling to the populate namespaces seperately since this needs to be done *after*
     * setting the parent namespace context. We cannot assume it will happen at construction!
     */
    public void init() {
        // here we have an extra issue to attend to. we need to look at the
        // prefixes and uris (the combination) and populate a hashmap of
        // namespaces. The hashmap of namespaces will be used to serve the
        // namespace context

        populateNamespaceContext();
    }

    public boolean isAttributeSpecified(int i) {
        return false; // not supported
    }

    public boolean isCharacters() {
        if (state == START_ELEMENT_STATE || state == END_ELEMENT_STATE) {
            return false;
        }
        return childReader.isCharacters();
    }

    public boolean isEndElement() {
        if (state == START_ELEMENT_STATE) {
            return false;
        } else if (state == END_ELEMENT_STATE) {
            return true;
        }
        return childReader.isEndElement();
    }

    /**
     * are we done ?
     *
     * @return
     */
    public boolean isEndOfFragment() {
        return state == END_ELEMENT_STATE;
    }

    public boolean isStandalone() {
        return true;
    }

    public boolean isStartElement() {
        if (state == START_ELEMENT_STATE) {
            return true;
        } else if (state == END_ELEMENT_STATE) {
            return false;
        }
        return childReader.isStartElement();
    }

    public boolean isWhiteSpace() {
        if (state == START_ELEMENT_STATE || state == END_ELEMENT_STATE) {
            return false;
        }
        return childReader.isWhiteSpace();
    }

    /**
     * Get the prefix list from the hastable and take that into an array
     *
     * @return
     */
    private String[] makePrefixArray() {
        String[] prefixes =
            (String[]) declaredNamespaceMap.keySet().toArray(new String[declaredNamespaceMap.size()]);
        Arrays.sort(prefixes);
        return prefixes;
    }

    public int next() throws XMLStreamException {
        return updateStatus();
    }

    /**
     * todo implement this
     *
     * @return
     * @throws XMLStreamException
     */
    public int nextTag() throws XMLStreamException {
        return 0;
    }

    // /////////////////////////////////////////////////////////////////////////
    // / Other utility methods
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Populates a namespace context
     */
    private void populateNamespaceContext() {

        // first add the current element namespace to the namespace context
        // declare it if not found
        registerNamespace(elementQName.getPrefix(), elementQName.getNamespaceURI());

        // traverse through the attributes and populate the namespace context
        // the attrib list can be of many combinations
        // the valid combinations are
        // String - String
        // QName - QName
        // null - OMAttribute

        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) { // jump in two
                Object attribName = attributes[i].getKey();
                if (attribName instanceof String) {
                    // ignore this case - Nothing to do
                } else if (attribName instanceof QName) {
                    QName attribQName = (QName) attribName;
                    registerNamespace(attribQName.getPrefix(), attribQName.getNamespaceURI());

                }
            }
        }

    }

    public final void populateProperties() {
        if (properties != null) {
            return;
        }
        if (elementQName == null) {
            elementQName = namespaceContext.createQName(this.rootElementURI, this.rootElementName);
        } else {
            elementQName =
                namespaceContext.createQName(elementQName.getNamespaceURI(), elementQName.getLocalPart());
        }

        List<Object> elementList = new ArrayList<Object>();
        List<Object> attributeList = new ArrayList<Object>();
        NamedNodeMap nodeMap = rootElement.getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Attr attr = (Attr) nodeMap.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                // Skip xmlns:xxx
                registerNamespace(attr.getLocalName(), attr.getValue());
                continue;
            }
            QName attrName = new QName(attr.getNamespaceURI(), attr.getLocalName());
            NameValuePair pair = new NameValuePair(attrName, attr.getValue());
            attributeList.add(pair);
        }
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                case Node.CDATA_SECTION_NODE:
                    NameValuePair pair = new NameValuePair(ELEMENT_TEXT, ((CharacterData) node).getData());
                    elementList.add(pair);
                    break;

                case Node.ELEMENT_NODE:
                    Element element = (Element) node;
                    QName elementName = new QName(element.getNamespaceURI(), element.getLocalName());
                    pair = new NameValuePair(elementName, new DOMXMLStreamReader(element));
                    elementList.add(pair);
                    break;
            }
        }
        properties = elementList.toArray(new Map.Entry[elementList.size()]);
        attributes = attributeList.toArray(new Map.Entry[attributeList.size()]);
    }

    /**
     * A convenient method to reuse the properties
     *
     * @return event to be thrown
     * @throws XMLStreamException
     */
    private int processProperties() throws XMLStreamException {
        // move to the next property depending on the current property
        // index
        Object propPointer = properties[currentPropertyIndex].getKey();
        QName propertyQName = null;
        boolean textFound = false;
        if (propPointer == null) {
            throw new XMLStreamException("property key cannot be null!");
        } else if (propPointer instanceof String) {
            // propPointer being a String has a special case
            // that is it can be a the special constant ELEMENT_TEXT that
            // says this text event
            if (ELEMENT_TEXT.equals(propPointer)) {
                textFound = true;
            } else {
                propertyQName = new QName((String) propPointer);
            }
        } else if (propPointer instanceof QName) {
            propertyQName = (QName) propPointer;
        } else {
            // oops - we've no idea what kind of key this is
            throw new XMLStreamException("unidentified property key!!!" + propPointer);
        }

        // ok! we got the key. Now look at the value
        Object propertyValue = properties[currentPropertyIndex].getValue();
        // cater for the special case now
        if (textFound) {
            // no delegation here - make the parser null and immediately
            // return with the event characters
            childReader = null;
            state = TEXT_STATE;
            currentPropertyIndex++;
            return CHARACTERS;
        } else if (propertyValue == null || propertyValue instanceof String) {
            // strings are handled by the NameValuePairStreamReader
            childReader = new SimpleElementStreamReader(propertyQName, (String) propertyValue);
            childReader.setParentNamespaceContext(this.namespaceContext);
            childReader.init();
        } else if (propertyValue instanceof DOMXMLStreamReader) {
            // ADBbean has it's own method to get a reader
            XMLFragmentStreamReader reader = (DOMXMLStreamReader) propertyValue;
            // we know for sure that this is an ADB XMLStreamreader.
            // However we need to make sure that it is compatible
            childReader = reader;
            childReader.setParentNamespaceContext(this.namespaceContext);
            childReader.init();
        } else {
            // all special possiblilities has been tried! Let's treat
            // the thing as a bean and try generating events from it
            throw new UnsupportedOperationException("Not supported");
            // childReader = new
            // WrappingXMLStreamReader(BeanUtil.getPullParser(propertyValue,
            // propertyQName));
            // we cannot register the namespace context here
        }

        // set the state here
        state = DELEGATED_STATE;
        // we are done with the delegation
        // increment the property index
        currentPropertyIndex++;
        return childReader.getEventType();
    }

    /**
     * @param prefix
     * @param uri
     */
    private void registerNamespace(String prefix, String uri) {
        if (!uri.equals(namespaceContext.getNamespaceURI(prefix))) {
            namespaceContext.registerMapping(prefix, uri);
            declaredNamespaceMap.put(prefix, uri);
        }
    }

    public void require(int i, String string, String string1) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    /**
     * add the namespace context
     */

    public void setParentNamespaceContext(NamespaceContext nsContext) {
        // register the namespace context passed in to this
        this.namespaceContext.setParent(nsContext);

    }

    public boolean standaloneSet() {
        return true;
    }

    /**
     * By far this should be the most important method in this class this method changes the state of the parser
     * according to the change in the
     */
    private int updateStatus() throws XMLStreamException {
        int returnEvent = -1; // invalid state is the default state
        switch (state) {
            case START_ELEMENT_STATE:
                // current element is start element. We should be looking at the
                // property list and making a pullparser for the property value
                if (properties == null || properties.length == 0) {
                    // no properties - move to the end element state
                    // straightaway
                    state = END_ELEMENT_STATE;
                    returnEvent = END_ELEMENT;
                } else {
                    // there are properties. now we should delegate this task to
                    // a
                    // child reader depending on the property type
                    returnEvent = processProperties();

                }
                break;
            case END_ELEMENT_STATE:
                // we've reached the end element already. If the user tries to
                // push
                // further ahead then it is an exception
                throw new XMLStreamException("Trying to go beyond the end of the pullparser");

            case DELEGATED_STATE:
                if (childReader.isEndOfFragment()) {
                    // we've reached the end!
                    if (currentPropertyIndex > (properties.length - 1)) {
                        state = END_ELEMENT_STATE;
                        returnEvent = END_ELEMENT;
                    } else {
                        returnEvent = processProperties();
                    }
                } else {
                    returnEvent = childReader.next();
                }
                break;

            case TEXT_STATE:
                // if there are any more event we should be delegating to
                // processProperties. if not we just return an end element
                if (currentPropertyIndex > (properties.length - 1)) {
                    state = END_ELEMENT_STATE;
                    returnEvent = END_ELEMENT;
                } else {
                    returnEvent = processProperties();
                }
                break;
        }
        return returnEvent;
    }

}
