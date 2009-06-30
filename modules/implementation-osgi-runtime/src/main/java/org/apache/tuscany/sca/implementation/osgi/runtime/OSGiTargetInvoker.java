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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The Invoker looks up the corresponding OSGi service from the OSGi service registry
 * and delegate the call to it.
 *
 * @version $Rev$ $Date$
 */
public class OSGiTargetInvoker implements Invoker {

    private Operation operation;

    private final OSGiImplementationProvider provider;
    private final RuntimeComponent component;
    private final RuntimeComponentService service;

    public OSGiTargetInvoker(Operation operation, OSGiImplementationProvider provider, RuntimeComponentService service) {

        this.operation = operation;
        this.service = service;
        this.provider = provider;
        this.component = provider.getComponent();

    }

    private Object invokeTarget(Message msg) throws InvocationTargetException {

        Operation op = msg.getOperation();
        if (op == null) {
            op = this.operation;
        }

        try {
            BundleContext bundleContext = provider.getImplementation().getBundle().getBundleContext();
            JavaInterface javaInterface = (JavaInterface)op.getInterface();
            // String filter = getOSGiFilter(provider.getOSGiProperties(service));
            // FIXME: What is the filter?
            String filter = "(!(sca.reference=*))";
            // "(sca.service=" + component.getURI() + "#service-name\\(" + service.getName() + "\\))";
            ServiceReference ref = bundleContext.getServiceReferences(javaInterface.getName(), filter)[0];
            Object instance = bundleContext.getService(ref);
            Method m = findMethod(instance.getClass(), operation);

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

    private String getOSGiFilter(Hashtable<String, Object> props) {

        String filter = "";

        if (props != null && props.size() > 0) {
            int propCount = 0;
            for (String propName : props.keySet()) {
                if (propName.equals("service.pid")) {
                    continue;
                }
                String value = String.valueOf(props.get(propName));
                StringBuffer buf = new StringBuffer();
                for (char c : value.toCharArray()) {
                    if (c == '(' || c == ')') {
                        buf.append("\\" + c);
                    } else {
                        buf.append(c);
                    }
                }
                filter = filter + "(" + propName + "=" + buf.toString() + ")";
                propCount++;
            }

            if (propCount > 1) {
                filter = "(&" + filter + ")";
            }
        } else {
            filter = null;
        }
        return filter;
    }

    /**
     * @Deprecated
     */
    private static Class<?>[] getPhysicalTypes(Operation operation) {
        DataType<List<DataType>> inputType = operation.getInputType();
        if (inputType == null) {
            return new Class<?>[] {};
        }
        List<DataType> types = inputType.getLogical();
        Class<?>[] javaTypes = new Class<?>[types.size()];
        for (int i = 0; i < javaTypes.length; i++) {
            Type physical = types.get(i).getPhysical();
            if (physical instanceof Class<?>) {
                javaTypes[i] = (Class<?>)physical;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return javaTypes;
    }

    /**
     * Return the method on the implementation class that matches the operation.
     *
     * @param implClass the implementation class or interface
     * @param operation the operation to match
     * @return the method described by the operation
     * @throws NoSuchMethodException if no such method exists
     * @Deprecated
     */
    public static Method findMethod(Class<?> implClass, Operation operation) throws NoSuchMethodException {
        String name = operation.getName();
        if (operation instanceof JavaOperation) {
            name = ((JavaOperation)operation).getJavaMethod().getName();
        }
        Interface interface1 = operation.getInterface();
        int numParams = operation.getInputType().getLogical().size();
        if (interface1 != null && interface1.isRemotable()) {
            List<Method> matchingMethods = new ArrayList<Method>();
            for (Method m : implClass.getMethods()) {
                if (m.getName().equals(name) && m.getParameterTypes().length == numParams) {
                    matchingMethods.add(m);
                }
            }

            // TUSCANY-2180 If there is only one method then we just match on the name
            // (this is the same as the existing behaviour)
            if (matchingMethods.size() == 1) {
                return matchingMethods.get(0);
            }
            if (matchingMethods.size() > 1) {
                // TUSCANY-2180 We need to check the parameter types too
                Class<?>[] paramTypes = getPhysicalTypes(operation);
                return implClass.getMethod(name, paramTypes);
            }

            // No matching method found
            throw new NoSuchMethodException("No matching method for operation " + operation.getName()
                + " is found on "
                + implClass);
        }
        Class<?>[] paramTypes = getPhysicalTypes(operation);
        return implClass.getMethod(name, paramTypes);
    }

}
