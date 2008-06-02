package org.apache.tuscany.sca.binding.corba.types.util;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class ReferenceTypeHelper implements TypeHelper {

	public TypeCode getType(TypeTreeNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object read(TypeTreeNode node, InputStream is) {
		return is.read_Object();
	}

	public void write(TypeTreeNode node, OutputStream os, java.lang.Object data) {
		os.write_Object((Object) data);
	}

}
