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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.CDATA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.tuscany.sca.contribution.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.contribution.processor.xml.XMLEventsStreamReader;

public class AnyElementProcessor implements StAXArtifactProcessor<Object> {
	private static final QName ANY_ELEMENT = new QName(Constants.XMLSCHEMA_NS,
			"anyElement");

	private XMLInputFactory xmlInputFactory;
	@SuppressWarnings("unused")
	private Monitor monitor;
	//Map<String, NamespaceContext> map = new HashMap<String, NamespaceContext>();

	public AnyElementProcessor(ModelFactoryExtensionPoint modelFactories,
			Monitor monitor) {
		xmlInputFactory = modelFactories.getFactory(XMLInputFactory.class);
		this.monitor = monitor;
	}

	public QName getArtifactType() {
		return ANY_ELEMENT;
	}

	public Class<Object> getModelType() {
		return Object.class;
	}

	/**
	 * Reads the contetns of the unknown elements and generates a custom
	 * implementation of XMLStreamReader i.e. XMLEventsStreamReader
	 * 
	 * @param reader
	 * @return
	 * @throws XMLStreamException
	 */
	@SuppressWarnings("unchecked")
	public Object read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

		//Custom variables
		String currentElement = null;
		List eventsList = new ArrayList();
		
		Map<String, NamespaceContext> eventContext = new HashMap<String, NamespaceContext>();
		
		try{
			//Cast the block of unknown elements into document
			XMLDocumentStreamReader docReader = new XMLDocumentStreamReader(reader);
					
			XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(docReader);
			
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();
				
				//Populate the eventContext map with the current element's name and corresponding NamesapceContext
				if (currentElement != null && !(eventContext.containsKey(currentElement))) {
					eventContext.put(currentElement, reader.getNamespaceContext());
				}
				
				//Populate the list with the XMLEvents
				eventsList.add(event);
				if (event.isStartElement()) {
					currentElement = reader.getName().getLocalPart();
				}
				if (event.isEndDocument()) {
					return new XMLEventsStreamReader(eventsList, eventContext);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes unknown portions back to the writer
	 * 
	 * @param model
	 * @param writer
	 */
	public void write(Object model, XMLStreamWriter writer)
			throws XMLStreamException {
		if (!(model instanceof XMLStreamReader)) {
			return;
		}
		XMLStreamReader reader = (XMLStreamReader) model;
		
		int event = reader.getEventType();
		while (reader.hasNext()) {
			switch (event) {
			case START_ELEMENT:
											
				writer.writeStartElement(reader.getPrefix(), reader
						.getLocalName(), reader.getNamespaceURI());
				for (int i = 1; i <= reader.getAttributeCount(); i++) {
					writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), 
							reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				}
				break;
				
			case CHARACTERS:
				writer.writeCharacters(reader.getText());	
				break;
				
			case CDATA:				
				writer.writeCData(reader.getText());
				break;

			case END_ELEMENT:
				writer.writeEndElement();
				break;
			}
			if (reader.hasNext()) {
				event = reader.next();
			}
		}
	}

	public void resolve(Object model, ModelResolver resolver)
			throws ContributionResolveException {
		// TODO Auto-generated method stub

	}

}
