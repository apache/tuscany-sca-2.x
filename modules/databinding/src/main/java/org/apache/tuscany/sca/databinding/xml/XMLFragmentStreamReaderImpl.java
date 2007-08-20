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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This is the new implementation of the XMLFramentStreamReader. The approach
 * here is simple When the pull parser needs to generate events for a particular
 * name-value(s) pair it always handes over (delegates) the task to another pull
 * parser which knows how to deal with it The common types of name value pairs
 * we'll come across are
 * <ul>
 * <li> String name/QName name - String value
 * <li> String name/QName name - String[] value
 * <li> QName name/String name - XMLStreamReader value
 * <li> QName name/String name - XMLStreamable value
 * <li> QName name/String name - Java bean
 * <li> QName name/String name - Datahandler
 * 
 * </ul>
 * <p/> As for the attributes, these are the possible combinations in the array
 * <ul>
 * <li> String name/QName name - String value
 * </ul>
 * Note that certain array methods have been deliberately removed to avoid
 * complications. The generated code will take the trouble to lay the elements
 * of the array in the correct order <p/> <p/> Hence there will be a parser impl
 * that knows how to handle these types, and this parent parser will always
 * delegate these tasks to the child pullparasers in effect this is one huge
 * state machine that has only a few states and delegates things down to the
 * child parsers whenever possible <p/>
 * 
 * @version $Rev$ $Date$
 */
public class XMLFragmentStreamReaderImpl implements XMLFragmentStreamReader {

    private static final int DELEGATED_STATE = 2;
    private static final int END_ELEMENT_STATE = 1;
    // states for this pullparser - it can only have four states
    private static final int START_ELEMENT_STATE = 0;
    private static final int TEXT_STATE = 3;

    protected NamedProperty[] attributes;

    // reference to the child reader
    protected XMLFragmentStreamReader childReader;
    // current property index
    // initialized at zero
    protected int index;
    protected Map<String, String> declaredNamespaceMap = new HashMap<String, String>();
    protected QName elementQName;

    // we always create a new namespace context
    protected DelegatingNamespaceContext namespaceContext = new DelegatingNamespaceContext();

    protected NamedProperty[] elements;

    // integer field that keeps the state of this
    // parser.
    protected int state = START_ELEMENT_STATE;

    /*
     * we need to pass in a namespace context since when delegated, we've no
     * idea of the current namespace context. So it needs to be passed on here!
     */
    public XMLFragmentStreamReaderImpl(QName elementQName, NamedProperty[] elements, NamedProperty[] attributes) {
        // validate the lengths, since both the arrays are supposed
        // to have
        this.elements = elements == null ? new NamedProperty[0] : elements;
        this.elementQName = elementQName;
        this.attributes = attributes == null ? new NamedProperty[0] : attributes;
    }

    protected XMLFragmentStreamReaderImpl(QName elementQName) {
        this.elementQName = elementQName;
    }

    /**
     * add the namespace context
     */

    public void setParentNamespaceContext(NamespaceContext nsContext) {
        // register the namespace context passed in to this
        this.namespaceContext.setParentNsContext(nsContext);

    }

    protected NamedProperty[] getElements() {
        return elements;
    }

    protected NamedProperty[] getAttributes() {
        return attributes;
    }
    
    protected QName[] getNamespaces() {
        return new QName[0];
    }

    /**
     * @param prefix
     * @param uri
     */
    protected void addToNsMap(String prefix, String uri) {
        if (!uri.equals(namespaceContext.getNamespaceURI(prefix))) {
            namespaceContext.pushNamespace(prefix, uri);
            declaredNamespaceMap.put(prefix, uri);
        }
    }

    public void close() throws XMLStreamException {
        // do nothing here - we have no resources to free
    }

    public int getAttributeCount() {
        return (state == DELEGATED_STATE) ? childReader.getAttributeCount() : (state == START_ELEMENT_STATE
            ? getAttributes().length : 0);
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
     */
    public QName getAttributeName(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getAttributeName(i);
        } else if (state == START_ELEMENT_STATE) {
            if ((i >= (getAttributes().length)) || i < 0) { // out of range
                return null;
            } else {
                // get the attribute pointer
                QName attribPointer = getAttributes()[i].getKey();
                // case one - attrib name is null
                // this should be the pointer to the OMAttribute then
                if (attribPointer == null) {
                    throw new UnsupportedOperationException();
                } else if (attribPointer instanceof QName) {
                    return attribPointer;
                } else {
                    return null;
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
            if ((i >= (getAttributes().length)) || i < 0) { // out of range
                return null;
            } else {
                // get the attribute pointer
                QName attribPointer = getAttributes()[i].getKey();
                Object omAttribObj = getAttributes()[i].getValue();
                // case one - attrib name is null
                // this should be the pointer to the OMAttribute then
                if (attribPointer == null) {
                    throw new UnsupportedOperationException();
                } else if (attribPointer instanceof QName) {
                    return (String)omAttribObj;
                } else {
                    return null;
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
     * @throws XMLStreamException
     */
    public String getElementText() throws XMLStreamException {
        if (state == DELEGATED_STATE) {
            return childReader.getElementText();
        } else {
            return null;
        }

    }

    // /////////////////////////////////////////////////////////////////////////
    // / attribute handling
    // /////////////////////////////////////////////////////////////////////////

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

    public int getEventType() {
        if (state == START_ELEMENT_STATE) {
            return START_ELEMENT;
        } else if (state == END_ELEMENT_STATE) {
            return END_ELEMENT;
        } else if (state == TEXT_STATE) {
            return CHARACTERS;
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

    // /////////////////////////////////////////////////////////////////////////
    // //////////// end of attribute handling
    // /////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////
    // //////////// namespace handling
    // //////////////////////////////////////////////////////////////////////////

    public String getNamespaceURI(int i) {
        if (state == DELEGATED_STATE) {
            return childReader.getNamespaceURI(i);
        } else if (state != TEXT_STATE) {
            String namespacePrefix = getNamespacePrefix(i);
            return namespacePrefix == null ? null : (String)declaredNamespaceMap.get(namespacePrefix);
        } else {
            throw new IllegalStateException();
        }

    }

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
            String prefix = elementQName.getPrefix();
            return "".equals(prefix) ? null : prefix;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // /////// end of namespace handling
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param key
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

    public String getText() {
        if (state == DELEGATED_STATE) {
            return childReader.getText();
        } else if (state == TEXT_STATE) {
            return (String)getElements()[index - 1].getValue();
        } else {
            throw new IllegalStateException();
        }
    }

    public char[] getTextCharacters() {
        if (state == DELEGATED_STATE) {
            return childReader.getTextCharacters();
        } else if (state == TEXT_STATE) {
            return getElements()[index - 1].getValue() == null ? new char[0] : ((String)getElements()[index - 1]
                .getValue()).toCharArray();
        } else {
            throw new IllegalStateException();
        }
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
        if (state == DELEGATED_STATE) {
            return childReader.getTextCharacters(i, chars, i1, i2);
        } else if (state == TEXT_STATE) {
            return copy(i, chars, i1, i2);
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextLength() {
        if (state == DELEGATED_STATE) {
            return childReader.getTextLength();
        } else if (state == TEXT_STATE) {
            return getTextCharacters().length; 
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
     * @throws XMLStreamException
     */
    public boolean hasNext() throws XMLStreamException {
        if (state == DELEGATED_STATE) {
            if (childReader.isDone()) {
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
     */
    public boolean hasText() {
        if (state == DELEGATED_STATE) {
            return childReader.hasText();
        } else {
            return state == TEXT_STATE;
        }

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

    /**
     * are we done ?
     */
    public boolean isDone() {
        return state == END_ELEMENT_STATE;
    }

    public boolean isEndElement() {
        if (state == START_ELEMENT_STATE) {
            return false;
        } else if (state == END_ELEMENT_STATE) {
            return true;
        }
        return childReader.isEndElement();
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
     */
    private String[] makePrefixArray() {
        String[] prefixes = declaredNamespaceMap.keySet().toArray(new String[declaredNamespaceMap.size()]);
        Arrays.sort(prefixes);
        return prefixes;
    }

    /**
     * By far this should be the most important method in this class this method
     * changes the state of the parser
     */
    public int next() throws XMLStreamException {
        int returnEvent = -1; // invalid state is the default state
        switch (state) {
            case START_ELEMENT_STATE:
                // current element is start element. We should be looking at the
                // property list and making a pullparser for the property value
                if (getElements() == null || getElements().length == 0) {
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
                if (childReader.isDone()) {
                    // we've reached the end!
                    if (index > (getElements().length - 1)) {
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
                if (index > (getElements().length - 1)) {
                    state = END_ELEMENT_STATE;
                    returnEvent = END_ELEMENT;
                } else {
                    returnEvent = processProperties();
                }
                break;
        }
        return returnEvent;
    }

    // /////////////////////////////////////////////////////////////////////////
    // / Other utility methods
    // ////////////////////////////////////////////////////////////////////////

    /**
     * todo implement this
     * 
     * @throws XMLStreamException
     */
    public int nextTag() throws XMLStreamException {
        return 0;
    }

    /**
     * Populates a namespace context
     */
    private void populateNamespaceContext() {

        // first add the current element namespace to the namespace context
        // declare it if not found
        addToNsMap(elementQName.getPrefix(), elementQName.getNamespaceURI());
        
        for (QName n : getNamespaces()) {
            addToNsMap(n.getPrefix(), n.getNamespaceURI());
        }

        // traverse through the attributes and populate the namespace context
        // the attrib list can be of many combinations
        // the valid combinations are
        // String - String
        // QName - QName
        // null - OMAttribute

        for (int i = 0; i < getAttributes().length; i++) { // jump in two
            QName attrQName = getAttributes()[i].getKey();
            if (!"".equals(attrQName.getNamespaceURI())) {
                addToNsMap(attrQName.getPrefix(), attrQName.getNamespaceURI());
            }
        }
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
        QName propertyQName = getElements()[index].getKey();
        boolean textFound = false;
        if (propertyQName == null) {
            throw new XMLStreamException("property key cannot be null!");
        } else if (ELEMENT_TEXT.equals(propertyQName.getLocalPart())) {
            // propPointer being a String has a special case
            // that is it can be a the special constant ELEMENT_TEXT that
            // says this text event
            textFound = true;
        }

        // ok! we got the key. Now look at the value
        Object propertyValue = getElements()[index].getValue();
        // cater for the special case now
        if (textFound) {
            // no delegation here - make the parser null and immediately
            // return with the event characters
            childReader = null;
            state = TEXT_STATE;
            ++index;
            return CHARACTERS;
        } else if (propertyValue == null) {
            // if the value is null we delegate the work to a nullable
            // parser
            childReader = new NilElementStreamReader(propertyQName);
            childReader.setParentNamespaceContext(this.namespaceContext);
            childReader.init();
        } else if (propertyValue instanceof String) {
            // strings are handled by the NameValuePairStreamReader
            childReader = new NameValuePairStreamReader(propertyQName, (String)propertyValue);
            childReader.setParentNamespaceContext(this.namespaceContext);
            childReader.init();
        } else if (propertyValue instanceof String[]) {
            // string[] are handled by the NameValueArrayStreamReader
            // if the array is empty - skip it
            if (((String[])propertyValue).length == 0) {
                // advance the index
                ++index;
                return processProperties();
            } else {
                childReader = new NameValueArrayStreamReader(propertyQName, (String[])propertyValue);
                childReader.setParentNamespaceContext(this.namespaceContext);
                childReader.init();
            }

        } else if (propertyValue instanceof XMLStreamable) {
            // ADBbean has it's own method to get a reader
            XMLStreamReader reader = ((XMLStreamable)propertyValue).getXMLStreamReader(propertyQName);
            // we know for sure that this is an ADB XMLStreamreader.
            // However we need to make sure that it is compatible
            if (reader instanceof XMLFragmentStreamReader) {
                childReader = (XMLFragmentStreamReader)reader;
                childReader.setParentNamespaceContext(this.namespaceContext);
                childReader.init();
            } else {
                // wrap it to make compatible
                childReader = new WrappingXMLStreamReader(reader);
            }
        } else if (propertyValue instanceof XMLStreamReader) {
            XMLStreamReader reader = (XMLStreamReader)propertyValue;
            if (reader instanceof XMLFragmentStreamReader) {
                childReader = (XMLFragmentStreamReader)reader;
                childReader.setParentNamespaceContext(this.namespaceContext);
                childReader.init();
            } else {
                // wrap it to make compatible
                childReader = new WrappingXMLStreamReader(reader);
            }

        } else {
            // all special possiblilities has been tried! Let's treat
            // the thing as a bean and try generating events from it
            childReader = new WrappingXMLStreamReader(BeanUtil.getXMLStreamReader(propertyValue, propertyQName));
            // we cannot register the namespace context here
        }

        // set the state here
        state = DELEGATED_STATE;
        // we are done with the delegation
        // increment the property index
        ++index;
        return childReader.getEventType();
    }

    public void require(int i, String string, String string1) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public boolean standaloneSet() {
        return true;
    }

}
