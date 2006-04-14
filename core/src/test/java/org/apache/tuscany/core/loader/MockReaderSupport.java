/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.loader;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

/**
 * Base class for a mock XMLStreamReader.
 *  
 * @version $Rev$ $Date$
 */
public class MockReaderSupport implements XMLStreamReader {
    public QName getName() {
        throw new UnsupportedOperationException();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public int next() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public void require(int i, String name, String name1) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public String getElementText() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public int nextTag() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI(String name) {
        throw new UnsupportedOperationException();
    }

    public boolean isStartElement() {
        throw new UnsupportedOperationException();
    }

    public boolean isEndElement() {
        throw new UnsupportedOperationException();
    }

    public boolean isCharacters() {
        throw new UnsupportedOperationException();
    }

    public boolean isWhiteSpace() {
        throw new UnsupportedOperationException();
    }

    public String getAttributeValue(String name, String name1) {
        throw new UnsupportedOperationException();
    }

    public int getAttributeCount() {
        throw new UnsupportedOperationException();
    }

    public QName getAttributeName(int i) {
        throw new UnsupportedOperationException();
    }

    public String getAttributeNamespace(int i) {
        throw new UnsupportedOperationException();
    }

    public String getAttributeLocalName(int i) {
        throw new UnsupportedOperationException();
    }

    public String getAttributePrefix(int i) {
        throw new UnsupportedOperationException();
    }

    public String getAttributeType(int i) {
        throw new UnsupportedOperationException();
    }

    public String getAttributeValue(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean isAttributeSpecified(int i) {
        throw new UnsupportedOperationException();
    }

    public int getNamespaceCount() {
        throw new UnsupportedOperationException();
    }

    public String getNamespacePrefix(int i) {
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI(int i) {
        throw new UnsupportedOperationException();
    }

    public NamespaceContext getNamespaceContext() {
        throw new UnsupportedOperationException();
    }

    public int getEventType() {
        throw new UnsupportedOperationException();
    }

    public String getText() {
        throw new UnsupportedOperationException();
    }

    public char[] getTextCharacters() {
        throw new UnsupportedOperationException();
    }

    public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public int getTextStart() {
        throw new UnsupportedOperationException();
    }

    public int getTextLength() {
        throw new UnsupportedOperationException();
    }

    public String getEncoding() {
        throw new UnsupportedOperationException();
    }

    public boolean hasText() {
        throw new UnsupportedOperationException();
    }

    public Location getLocation() {
        throw new UnsupportedOperationException();
    }

    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    public boolean hasName() {
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }

    public String getPrefix() {
        throw new UnsupportedOperationException();
    }

    public String getVersion() {
        throw new UnsupportedOperationException();
    }

    public boolean isStandalone() {
        throw new UnsupportedOperationException();
    }

    public boolean standaloneSet() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncodingScheme() {
        throw new UnsupportedOperationException();
    }

    public String getPITarget() {
        throw new UnsupportedOperationException();
    }

    public String getPIData() {
        throw new UnsupportedOperationException();
    }
}
