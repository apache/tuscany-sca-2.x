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

package org.apache.tuscany.sca.binding.corba.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$ 
 * Helper tree for Java object hierarchy. It's closer to CORBA types structure
 * than plain Java hierarchy - it helps reading and writing complex structures.
 */
public class TypeTree {

	private Map<Class<?>, TypeTreeNode> typesUsed = new HashMap<Class<?>, TypeTreeNode>();
	private TypeTreeNode rootNode;

	/**
	 * Intent was to cache previously computed type trees. In practice it caused
	 * a bug, and it's not being used right now. TODO: enable type trees caching
	 * 
	 * @param forClass
	 * @param node
	 */
	public void addUsedType(Class<?> forClass, TypeTreeNode node) {
		typesUsed.put(forClass, node);
	}

	/**
	 * Returns node for previously created tree, it's getter for nodes added by
	 * addUsedType method. Not in use right now. TODO: enable type trees caching
	 * 
	 * @param forClass
	 * @return
	 */
	public TypeTreeNode getNodeForType(Class<?> forClass) {
		return typesUsed.get(forClass);
	}

	/**
	 * Returns root node.
	 * 
	 * @return root of type tree
	 */
	public TypeTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * Sets root of the type tree.
	 * 
	 * @param rootNode
	 *            root node.
	 */
	public void setRootNode(TypeTreeNode rootNode) {
		this.rootNode = rootNode;
	}

}
