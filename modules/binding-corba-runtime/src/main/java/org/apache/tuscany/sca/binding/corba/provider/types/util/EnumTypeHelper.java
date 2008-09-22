package org.apache.tuscany.sca.binding.corba.provider.types.util;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.binding.corba.provider.types.TypeTreeNode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class EnumTypeHelper implements TypeHelper {

    private static final Logger logger = Logger.getLogger(EnumTypeHelper.class.getName());
    
    public Object read(TypeTreeNode node, InputStream is) {
        int value = is.read_long();
        Object result = null;
        try {
            Method method = node.getJavaClass().getMethod("from_int", new Class[] {int.class});
            result = method.invoke(null, new Object[] {value});
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception during reading CORBA enum data", e);
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
            logger.log(Level.WARNING, "Exception during writing CORBA enum data", e);
        }
        os.write_long(value);
    }
}
