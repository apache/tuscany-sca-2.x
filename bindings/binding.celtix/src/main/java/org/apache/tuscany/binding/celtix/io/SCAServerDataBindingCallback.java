/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.binding.celtix.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;

/**
 *
 * @version $Rev$ $Date$
 */
public class SCAServerDataBindingCallback extends SCADataBindingCallback
        implements ServerDataBindingCallback {
    Method method;
    Object targetObject;

    public SCAServerDataBindingCallback(WSDLOperationInfo op,
                                        boolean inout,
                                        Method meth,
                                        Object target) {
        super(op, inout);
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
