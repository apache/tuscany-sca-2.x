package org.apache.tuscany.sca.binding.corba.types;

import java.util.HashMap;
import java.util.Map;

/**
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
