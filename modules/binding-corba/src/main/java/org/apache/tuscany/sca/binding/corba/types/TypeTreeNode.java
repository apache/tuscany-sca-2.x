package org.apache.tuscany.sca.binding.corba.types;

/**
 * Represents single tree node.
 * 
 */
public class TypeTreeNode {

	private NodeType nodeType;
	private TypeTreeNode[] children;
	private Class<?> javaClass;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public TypeTreeNode[] getChildren() {
		return children;
	}

	public void setChildren(TypeTreeNode[] children) {
		this.children = children;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

}
