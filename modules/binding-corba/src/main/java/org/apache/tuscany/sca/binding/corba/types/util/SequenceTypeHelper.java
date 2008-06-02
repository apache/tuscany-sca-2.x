package org.apache.tuscany.sca.binding.corba.types.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class SequenceTypeHelper implements TypeHelper {

	public TypeCode getType(TypeTreeNode node) {
		TypeCode typeCode = 
			org.omg.CORBA.ORB.init().create_alias_tc(
				node.getJavaClass().getSimpleName(),
				node.getJavaClass().getSimpleName(),
				org.omg.CORBA.ORB.init().create_sequence_tc(0,
						TypeHelpersProxy.getType(node.getChildren()[0])));
		return typeCode;
	}

	public Object read(TypeTreeNode node, InputStream is) {
		Object sequence = null;
		try {
			int size = is.read_long();
			sequence = Array.newInstance(node.getChildren()[0].getJavaClass(),
					size);
			for (int i = 0; i < size; i++) {
				Array.set(sequence, i, TypeHelpersProxy.read(
						node.getChildren()[0], is));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sequence;
	}

	public void write(TypeTreeNode node, OutputStream os, Object data) {
		int sum = 0;
		// determine length of array
		List<Object> array = new ArrayList<Object>();
		while (true) {
			try {
				array.add(Array.get(data, sum));
				sum++;
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		os.write_long(sum);
		Iterator<Object> iter = array.iterator();
		while (iter.hasNext()) {
			Object elem = iter.next();
			TypeHelpersProxy.write(node.getChildren()[0], os, elem);
		}

	}
}
