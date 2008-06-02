package org.apache.tuscany.sca.binding.corba.types.util;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface TypeHelper {
	
	/**
	 * Gets type definition for CORBA API.
	 * @param node 
	 * @return
	 */
	TypeCode getType(TypeTreeNode node);
	
	/**
	 * Reads CORBA object
	 * @param node
	 * @param is
	 * @return
	 */
	Object read(TypeTreeNode node, InputStream is);
	
	/**
	 * Writes CORBA object
	 * @param node
	 * @param os
	 * @param data
	 */
	void write(TypeTreeNode node, OutputStream os, Object data);
	
}
