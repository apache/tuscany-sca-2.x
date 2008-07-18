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

package org.apache.tuscany.sca.binding.corba.impl.reference;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

import org.omg.CORBA.Object;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceInstanceCreator {

    private static final CallbackFilter FILTER = new CallbackFilter() {
        public int accept(Method method) {
            return 1;
        }
    };

    /**
     * Dynamically creates instance of user defined interface. Instance is
     * enhanced by RemoteMethodInterceptor
     * 
     * @param reference
     *            CORBA reference
     * @param forClass
     *            user defined interface
     * @return dynamic implementation instance
     */
    public static java.lang.Object createInstance(Object reference, Class<?> forClass) {
        java.lang.Object result = null;
        try {
            Enhancer enhancer = new Enhancer();
            enhancer.setInterfaces(new Class[] {forClass});
            enhancer.setCallbackFilter(FILTER);
            enhancer.setCallbackTypes(new Class[] {NoOp.class, MethodInterceptor.class});
            Class<?> newClass = enhancer.createClass();
            Enhancer.registerStaticCallbacks(newClass, new Callback[] {NoOp.INSTANCE,
                                                                       new InterfaceMethodInterceptor(reference, forClass)});
            result = newClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
