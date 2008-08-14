package org.apache.tuscany.sca.binding.corba.impl.types.util;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeNode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class EnumTypeHelper implements TypeHelper {

    public Object read(TypeTreeNode node, InputStream is) {
        int value = is.read_long();
        Object result = null;
        try {
            Method method = node.getJavaClass().getMethod("from_int", new Class[] {int.class});
            result = method.invoke(null, new Object[] {value});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public void write(TypeTreeNode node, OutputStream os, Object data) {
        int value = 0;
        try {
            Method method = data.getClass().getMethod("value", new Class[] {});
            value = (Integer)method.invoke(data, new Object[] {});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        os.write_long(value);
    }
}
