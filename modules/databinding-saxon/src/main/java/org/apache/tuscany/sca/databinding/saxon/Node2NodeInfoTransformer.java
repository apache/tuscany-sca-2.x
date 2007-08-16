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

import javax.xml.transform.dom.DOMSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transforms DOM Node-s to NodeInfo objects needed by Saxon parser
 * @version $Rev$ $Date$
 * Any namespaces that are defined are deleted, because otherwise
 * the SaxonB parser does not work
 */
public class Node2NodeInfoTransformer extends BaseTransformer<Node, NodeInfo>
		implements PullTransformer<Node, NodeInfo> {

	public NodeInfo transform(Node source, TransformationContext context) {
		if(source instanceof Document) {
			Configuration configuration = SaxonDataBindingHelper.CURR_EXECUTING_CONFIG;
			if(configuration == null) {
				configuration = new Configuration();
			}
			Document doc = (Document)source;
			Document cloneDoc = (Document)doc.cloneNode(false);
			SaxonDataBindingHelper.setNamespacesAndPrefixesReq(doc, cloneDoc, cloneDoc, "", null);
			DocumentInfo docInfo = null;
			try {
				docInfo = configuration.buildDocument(new DOMSource(cloneDoc));
			} catch (XPathException e) {
				throw new TransformationException(e);
			}
			return docInfo;
		}
		return null;
	}

	@Override
	protected Class getSourceType() {
		return Node.class;
	}

	@Override
	protected Class getTargetType() {
		return NodeInfo.class;
	}

	@Override
	public int getWeight() {
		return 10;
	}

}
