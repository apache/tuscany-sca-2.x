package org.apache.tuscany.sca.contribution.processor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.XMLConstants;

/*
 * Custom implementaion of the XMLStreamReader to keep track of the namespace context for each element
 */
public class TuscanyXMLStreamReader extends StreamReaderDelegate implements
		XMLStreamReader {

	Stack<ArrayList<ArrayList<String>>> context = new Stack<ArrayList<ArrayList<String>>>();
	
	List contextList;
	List<String> prefixList;
	List<String> uriList;

	public TuscanyXMLStreamReader(XMLStreamReader reader) {
		super(reader);
	}

	public void pushContext() throws XMLStreamException {
		contextList = new ArrayList<ArrayList<String>>();
		prefixList = new ArrayList<String>();
		uriList = new ArrayList<String>();
		int namespaceCount = this.getNamespaceCount();
		if (namespaceCount == 0) {
			prefixList.add(null);
			uriList.add(null);
		}
		for (int i = 0; i < namespaceCount; i++) {
			prefixList.add(checkString(this.getNamespacePrefix(i)));
			uriList.add(this.getNamespaceURI(i));
		}
		contextList.add(prefixList);
		contextList.add(uriList);
		context.push((ArrayList) contextList);
	}

	private String checkString(String namespacePrefix) {
		if (namespacePrefix == null) {
			return XMLConstants.DEFAULT_NS_PREFIX;
		} else {
			return namespacePrefix;
		}
	}

	public void popContext() throws XMLStreamException {
		context.pop();
	}

	/*
	 * Overriding the next() method to perform PUSH and POP operations 
	 * for the NamespaceContext for the current element
	 */
			
	@Override
	public int next() throws XMLStreamException {
		// POP the context if the element ends
		if (this.getEventType() == END_ELEMENT) {
			popContext();
		}
		
		//get the next event 
		int nextEvent = super.next();
		//PUSH the events info onto the Stack 
		if (nextEvent == START_ELEMENT) {
			pushContext();
		}
		return nextEvent;
	}
	
	@Override
	public int nextTag() throws XMLStreamException {
		if (this.getEventType() == START_ELEMENT) {
			pushContext();
		}
		if (this.getEventType() == END_ELEMENT) {
			popContext();
		}
		return super.nextTag();
	}
	
	@Override
	public NamespaceContext getNamespaceContext(){
		return new TuscanyNamespaceContext((Stack)context.clone());
	}
}
