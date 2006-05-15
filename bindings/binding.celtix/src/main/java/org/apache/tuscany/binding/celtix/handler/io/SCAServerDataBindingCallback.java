/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.celtix.handler.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import commonj.sdo.helper.TypeHelper;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;

public class SCAServerDataBindingCallback extends SCADataBindingCallback
    implements ServerDataBindingCallback {
    Method method;
    Object targetObject;

    public SCAServerDataBindingCallback(WSDLOperationInfo op, TypeHelper helper,
                                        ResourceLoader l,
                                        boolean inout, Method meth, Object target) {
        super(op, helper, l, inout);
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
