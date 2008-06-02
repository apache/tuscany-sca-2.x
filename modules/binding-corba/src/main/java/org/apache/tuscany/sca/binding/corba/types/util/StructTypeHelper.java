package org.apache.tuscany.sca.binding.corba.types.util;

import java.lang.reflect.Field;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class StructTypeHelper implements TypeHelper {

	public TypeCode getType(TypeTreeNode node) {
		TypeTreeNode[] children = node.getChildren();
		if (children != null) {
			StructMember[] members = new StructMember[children.length];
			for (int i = 0; i < children.length; i++) {
				members[i] = new StructMember(children[i].getJavaClass()
						.getSimpleName(),
						TypeHelpersProxy.getType(children[i]), null);
			}
			TypeCode result = org.omg.CORBA.ORB.init().create_struct_tc(
					node.getJavaClass().getSimpleName(),
					node.getJavaClass().getSimpleName(), members);
			return result;
		} else {
			return null;
		}
	}

	public Object read(TypeTreeNode node, InputStream is) {
		TypeTreeNode[] children = node.getChildren();
		Object result = null;
		if (children != null) {
			try {
				result = node.getJavaClass().newInstance();
				for (int i = 0; i < children.length; i++) {
					Object childResult = TypeHelpersProxy.read(children[i], is);
					Field childField = result.getClass().getField(
							children[i].getName());
					childField.set(result, childResult);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return result;
	}

	public void write(TypeTreeNode node, OutputStream os, Object data) {
		TypeTreeNode[] children = node.getChildren();
		if (children != null) {
			try {
				for (int i = 0; i < children.length; i++) {
					Field childField = node.getJavaClass().getField(
							children[i].getName());
					TypeHelpersProxy.write(children[i], os, childField
							.get(data));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

}
