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
package org.apache.tuscany.sca.contribution.processor.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.XMLEvent;


public class XMLEventsStreamReader implements XMLStreamReader {

	@SuppressWarnings("unused")
	private ArrayList<XMLEvent> events = null;
	@SuppressWarnings("unchecked")
	private HashMap<String, NamespaceContext> eventContext = null;

	private int state;
	private java.util.Iterator<XMLEvent> iterator;
	private XMLEvent current;

	@SuppressWarnings("unchecked")
	public XMLEventsStreamReader(List<XMLEvent> events,Map<String, NamespaceContext> map) {
		this.events = (ArrayList<XMLEvent>) events;
		this.eventContext = (HashMap<String, NamespaceContext>) map;
		this.iterator = events.iterator();
		this.current = iterator.next();
		this.state = current.getEventType();
	}

	public void close() throws XMLStreamException {
		this.events = null;
		this.eventContext = null;
		this.iterator = null;
		this.current = null;
	}

	private void checkElementState() {
		if (getEventType() != START_ELEMENT && getEventType() != END_ELEMENT) {
			throw new IllegalStateException();
		}
	}

	@SuppressWarnings("unchecked")
	public int getAttributeCount() {
		checkElementState();
		int count = 0;
		Iterator<Attribute> iterator = current.asStartElement().getAttributes();
		while (iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}

	/*
	 * Custom method to get attribute from the specified index
	 */
	@SuppressWarnings("unchecked")
	private Attribute getAttribute(int index) {
		checkElementState();
		int count = 0;
		Attribute attribute = null;
		Iterator<Attribute> iterator = current.asStartElement().getAttributes();
		while (iterator.hasNext()) {
			count++;
			if (count == index) {
				attribute = iterator.next();
			} else {
				iterator.next();
			}
		}
		return attribute;
	}

	
	public String getAttributeLocalName(int index) {
		checkElementState();
		return getAttribute(index).getName().getLocalPart();
	}

	public QName getAttributeName(int index) {
		checkElementState();
		return getAttribute(index).getName();
	}

	public String getAttributeNamespace(int index) {
		checkElementState();
		return getAttributeName(index).getNamespaceURI();
	}

	public String getAttributePrefix(int index) {
		checkElementState();
		return getAttributeName(index).getPrefix();
	}

	public String getAttributeType(int index) {
		checkElementState();
		return getAttribute(index).getDTDType();
	}

	public String getAttributeValue(int index) {
		checkElementState();
		return getAttribute(index).getValue();
	}

	@SuppressWarnings("unchecked")
	public String getAttributeValue(String namespaceURI, String localName) {
		checkElementState();
		Iterator<Attribute> iterator = current.asStartElement().getAttributes();
		Attribute attribute;
		while (iterator.hasNext()) {
			attribute = iterator.next();
			if (attribute.getName().getNamespaceURI().equalsIgnoreCase(
					namespaceURI)
					&& attribute.getName().getLocalPart().equalsIgnoreCase(
							localName)) {
				return attribute.getValue();
			}
		}
		return null;

	}

	public String getCharacterEncodingScheme() {
		return "UTF-8";
	}

	public String getElementText() throws XMLStreamException {
		checkElementState();
		int eventType = getEventType();
		String elementText = null;

		if (eventType == START_ELEMENT) {
			elementText = current.asStartElement().getName().getLocalPart();
		} else if (eventType == END_ELEMENT) {
			elementText = current.asEndElement().getName().getLocalPart();
		}
		return elementText;
	}

	public String getEncoding() {
		return "UTF-8";
	}

	public int getEventType() {
		return state;
	}

	public String getLocalName() {
		checkElementState();
		switch (current.getEventType()) {
		case START_ELEMENT:
			return current.asStartElement().getName().getLocalPart();
		case END_ELEMENT:
			return current.asEndElement().getName().getLocalPart();
		}
		return null;
	}

	public Location getLocation() {
		return current.getLocation();
	}

	public QName getName() {
		checkElementState();
		switch (current.getEventType()) {
		case START_ELEMENT:
			return current.asStartElement().getName();
		case END_ELEMENT:
			return current.asEndElement().getName();
		}
		return null;
	}

	public NamespaceContext getNamespaceContext() {
		checkElementState();
		//return new TuscanyNamespaceContext(eventContext.get(getLocalName()));
		return eventContext.get(getLocalName());
	}

	@SuppressWarnings("unchecked")
	public int getNamespaceCount() {
		int count = 0;
		Iterator<Namespace> itr = current.asStartElement().getNamespaces();
		while (itr.hasNext()) {
			count++;
			itr.next();
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public String getNamespacePrefix(int index) {
		Iterator<Namespace> itr = current.asStartElement().getNamespaces();
		int level = 0;
		Namespace ns = null;
		while (itr.hasNext()) {
			ns = itr.next();
			if (level == index) {
				return ns.getPrefix();
			}
			level++;
		}
		return null;
	}

	public String getNamespaceURI() {
		checkElementState();
		switch (current.getEventType()) {
		case START_ELEMENT:
			return current.asStartElement().getName().getNamespaceURI();
		case END_ELEMENT:
			return current.asEndElement().getName().getNamespaceURI();
		}
		return null;
	}

	public String getNamespaceURI(String prefix) {
		return getNamespaceContext().getNamespaceURI(prefix);
	}

	@SuppressWarnings("unchecked")
	public String getNamespaceURI(int index) {
		Iterator<Namespace> itr = current.asStartElement().getNamespaces();
		int level = 0;
		Namespace ns = null;
		while (itr.hasNext()) {
			ns = itr.next();
			if (level == index) {
				return ns.getNamespaceURI();
			}
			level++;
		}
		return null;
	}

	public String getPIData() {
		if (current.isProcessingInstruction()) {
			ProcessingInstruction pi = (ProcessingInstruction) current;
			return pi.getData();
		} else {
			throw new IllegalStateException(current.toString());
		}
	}

	public String getPITarget() {
		if (current.isProcessingInstruction()) {
			ProcessingInstruction pi = (ProcessingInstruction) current;
			return pi.getTarget();
		} else {
			throw new IllegalStateException(current.toString());
		}
	}

	public String getPrefix() {
		checkElementState();
		if (current.isStartElement()) {
			return current.asStartElement().getName().getPrefix();
		}
		return null;
	}

	/*
	 * FIXME: Implementation pending... 
	 * 
	 * @see (non-Javadoc)
	 * javax.xml.stream.util.StreamReaderDelegate#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) throws IllegalArgumentException {
		// TODO Auto-generated method stub

		return null;
	}

	public String getText() {
		if (current.isCharacters()) {
			return current.asCharacters().getData();
		} else {
			throw new IllegalStateException(current.toString());
		}
	}

	public char[] getTextCharacters() {
		if (current.isCharacters()) {
			return current.asCharacters().getData().toCharArray();
		} else {
			throw new IllegalStateException(current.toString());
		}
	}

	/*
	 * FIXME: Implementation pending... (non-Javadoc)
	 * 
	 * @see javax.xml.stream.util.StreamReaderDelegate#getTextCharacters(int,
	 * char[], int, int)
	 */
	public int getTextCharacters(int sourceStart, char[] target,
			int targetStart, int length) throws XMLStreamException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * FIXME:Implementaion can be improved (non-Javadoc)
	 * 
	 * @see javax.xml.stream.util.StreamReaderDelegate#getTextLength()
	 */
	public int getTextLength() {
		if (current.isCharacters()) {
			return current.asCharacters().getData().length();
		} else {
			throw new IllegalStateException(current.toString());
		}
	}

	/*
	 * FIXME: Implementation pending... (non-Javadoc)
	 * 
	 * @see javax.xml.stream.util.StreamReaderDelegate#getTextStart()
	 */
	public int getTextStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * FIXME: Implementation pending... (non-Javadoc)
	 * 
	 * @see javax.xml.stream.util.StreamReaderDelegate#getTextStart()
	 */
	public String getVersion() {
		// TODO Auto-generated method stub

		return null;
	}

	public boolean hasName() {
		return false;
	}

	public boolean hasNext() throws XMLStreamException {
		return iterator.hasNext() || state != END_DOCUMENT;

	}

	public boolean hasText() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * FIXME: Implementation pending... (non-Javadoc)
	 * 
	 * @see javax.xml.stream.util.StreamReaderDelegate#getTextStart()
	 */
	public boolean isAttributeSpecified(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCharacters() {
		return current.isCharacters();
	}

	public boolean isEndElement() {
		return current.isEndElement();
	}

	public boolean isStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStartElement() {
		return current.isStartElement();
	}

	public boolean isWhiteSpace() {
		// TODO Auto-generated method stub
		return false;
	}

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
		state = current.getEventType();
		return state;
	}

	public int nextTag() throws XMLStreamException {
		return iterator.next().getEventType();
	}

	public void require(int type, String namespaceURI, String localName)
			throws XMLStreamException {
		boolean require = false;
		String uri = getNamespaceURI();
		String name = getLocalName();
		if (state == type && namespaceURI.equals(uri) && localName.equals(name)) {
			require = true;
		}
		if (require != true) {
			throw new XMLStreamException();
		}
	}

	public boolean standaloneSet() {
		return false;
	}

}
