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
package org.apache.tuscany.sca.databinding.saxon;

import net.sf.saxon.Configuration;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * Provides helper functionality for saxon data bindings
 * @version $Rev$ $Date$
 */
public class SaxonDataBindingHelper {
	/**
	 * This variable is meaningfull only in the context of XQoery expression
	 * execution. It is used by the DataObject2NodeInfoTransformer and
	 * Node2NodeInfoTransformer to create the correct NodeInfo objects
	 * in the Output2Output transformations. 
	 * For Input2Input transformations it is meaningless:
	 *    - if it is null - it is ignored by the transformers as they create new
	 *      configuration objects
	 *    - if it is not null - it is reused
	 * However the XQueryInvoker transforms all NodeInfo-s to NodeInfo-s with
	 * its current configuration, so there is no effect for Input2Input transformations
	 */
	public static Configuration CURR_EXECUTING_CONFIG = null;
	
	/**
	 * Converts a given document to a document with specific namespace and prefix
	 */
	public static void setNamespacesAndPrefixesReq(Node node, Node copyNode, Document copyOwnerDocument, String namespace, String prefix) {
		NodeList childNodes = node.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			Node clonning = null;
			if(childNode.getNodeType() == Node.ELEMENT_NODE || childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				if(childNode.getNodeType() == Node.ELEMENT_NODE) {
					clonning = copyOwnerDocument.createElementNS(namespace, childNode.getLocalName());
					NamedNodeMap attributes = ((Element)childNode).getAttributes();
					for(int j=0; j<attributes.getLength(); j++) {
						Attr attribute = (Attr)attributes.item(j);
						Attr cloneAttribute = copyOwnerDocument.createAttributeNS(namespace, attribute.getLocalName());
						cloneAttribute.setValue(attribute.getValue());
						((Element)clonning).setAttributeNode(cloneAttribute);
						cloneAttribute.setPrefix(prefix);
					}
				} else {
					clonning = copyOwnerDocument.createAttributeNS(namespace, childNode.getLocalName());
				}
				//clonning.setTextContent(childNode.getTextContent());
				clonning.setNodeValue(childNode.getNodeValue());
				clonning.setPrefix(prefix);
			} else if(!(copyNode instanceof Document)) {
				if(childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
					clonning = copyOwnerDocument.createCDATASection(((CharacterData)childNode).getData());
				} else if(childNode.getNodeType() == Node.COMMENT_NODE) {
					clonning = copyOwnerDocument.createComment(((CharacterData)childNode).getData());
				} else if(childNode.getNodeType() == Node.TEXT_NODE) {
					clonning = copyOwnerDocument.createTextNode(((CharacterData)childNode).getData());
				} else if(childNode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
					clonning = copyOwnerDocument.createEntityReference(childNode.getNodeName());
				} else if(childNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
					clonning = copyOwnerDocument.createProcessingInstruction(((ProcessingInstruction)childNode).getTarget(), ((ProcessingInstruction)childNode).getData());
				}
			}
			if(clonning!=null) {
				copyNode.appendChild(clonning);
			}
			setNamespacesAndPrefixesReq(childNode, clonning, copyOwnerDocument, namespace, prefix);
		}
	}
}
