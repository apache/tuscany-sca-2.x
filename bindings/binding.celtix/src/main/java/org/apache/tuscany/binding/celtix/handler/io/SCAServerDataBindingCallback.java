package org.apache.tuscany.binding.celtix.handler.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import commonj.sdo.helper.TypeHelper;

import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;

public class SCAServerDataBindingCallback extends SCADataBindingCallback
    implements ServerDataBindingCallback {
    Method method;
    Object targetObject;

    public SCAServerDataBindingCallback(WSDLOperationInfo op, TypeHelper helper,
                                        boolean inout, Method meth, Object target) {
        super(op, helper, inout);
        method = meth;
        targetObject = target;
    }
    

    public void invoke(ObjectMessageContext octx) throws InvocationTargetException {
        Object ret;
        try {
            ret = method.invoke(targetObject, octx.getMessageObjects());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
        octx.setReturn(ret);
    }
    
    public void initObjectContext(ObjectMessageContext octx) {
        Object o[] = new Object[method.getParameterTypes().length];
        //REVIST - holders?
        octx.setMessageObjects(o);
    }
    
}
