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

package org.apache.tuscany.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.AbstractInvocationHandler;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationChain;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler extends AbstractInvocationHandler implements InvocationHandler {
    private Class<?> proxyInterface;
    private RuntimeWire wire;
    private WorkContext workContext;

    public JDKInvocationHandler(Class<?> proxyInterface, RuntimeWire wire, WorkContext workContext) {
        super();
        this.proxyInterface = proxyInterface;
        this.wire = wire;
        this.workContext = workContext;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            Object obj = args[0];
            if (obj == null) {
                return false;
            }
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            return equals(Proxy.getInvocationHandler(obj));
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
        }
        InvocationChain chain = getInvocationChain(method);
        if (chain == null) {
            throw new IllegalArgumentException("No matching opeeration is found: " + method);
        }

        return invoke(chain, null, args, null, null, workContext);
    }

    /**
     * Determines if the given operation matches the given method
     * 
     * @return true if the operation matches, false if does not
     */
    private static boolean match(Operation operation, Method method) {
        Class<?>[] params = method.getParameterTypes();
        DataType<List<DataType>> inputType = operation.getInputType();
        List<DataType> types = inputType.getLogical();
        boolean matched = true;
        if (types.size() == params.length && method.getName().equals(operation.getName())) {
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i];
                if (!clazz.equals(operation.getInputType().getLogical().get(i).getPhysical())) {
                    matched = false;
                }
            }
        } else {
            matched = false;
        }
        return matched;

    }

    private InvocationChain getInvocationChain(Method method) {
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();
            if (match(operation, method)) {
                return chain;
            }
        }
        return null;
    }

}
