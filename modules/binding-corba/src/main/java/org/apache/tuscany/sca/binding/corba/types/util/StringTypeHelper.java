package org.apache.tuscany.sca.binding.corba.types.util;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class StringTypeHelper implements TypeHelper {

	public TypeCode getType(TypeTreeNode node) {
		return ORB.init().create_string_tc(0);
	}

	public Object read(TypeTreeNode node, InputStream is) {
		return is.read_string();
	}

	public void write(TypeTreeNode node, OutputStream os, Object data) {
		os.write_string((String) data);
	}

	
}
