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

package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Java->OSGi references use OSGiTargetInvoker to call methods from OSGi bundles
 * OSGi->Java references use JDKProxyService and invocation handler and do not use this class
 * OSGi->OSGi references go through OSGi reference mechanisms when a proxy is not used
 *    When a proxy is used, this invoker is used to call methods from OSGi bundles
 *    A proxy is used for OSGi->OSGi if
 *       1) target reference properties are specified  OR
 *       2) there are one or more non-blocking methods in the target interface OR
 *       3) scope is not COMPOSITE
 *
 * @version $Rev$ $Date$
 */
public class OSGiTargetInvoker<T> implements Invoker {

    private Operation operation;
    protected InstanceWrapper<T> target;

    private final OSGiImplementationProvider provider;
    private final RuntimeComponentService service;

    public OSGiTargetInvoker(Operation operation, OSGiImplementationProvider provider, RuntimeComponentService service) {

        this.operation = operation;
        this.service = service;
        this.provider = provider;

    }

    private Object invokeTarget(Message msg) throws InvocationTargetException {

        Operation op = msg.getOperation();
        if (op == null) {
            op = this.operation;
        }

        try {
            JavaInterface javaInterface = (JavaInterface)op.getInterface();
            // FIXME: What is the filter?
            Object instance = provider.osgiBundle.getBundleContext().getServiceReference(javaInterface.getName());

            Method m = JavaInterfaceUtil.findMethod(instance.getClass(), operation);

            Object ret = invokeMethod(instance, m, msg);

            return ret;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    protected Object invokeMethod(Object instance, Method m, Message msg) throws InvocationTargetException {

        try {

            Object payload = msg.getBody();

            if (payload != null && !payload.getClass().isArray()) {
                return m.invoke(instance, payload);
            } else {
                return m.invoke(instance, (Object[])payload);
            }

        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }

}
