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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.osoa.sca.ServiceRuntimeException;

/**
 * JAX-WS ExceptionHandler
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSFaultExceptionMapper implements FaultExceptionMapper {
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private DataBindingExtensionPoint dataBindingExtensionPoint;

    public JAXWSFaultExceptionMapper(DataBindingExtensionPoint dataBindingExtensionPoint) {
        super();
        this.dataBindingExtensionPoint = dataBindingExtensionPoint;
    }

    /**
     * The following is quoted from the JAX-WS spec v2.1
     * <ul>
     * <li>WrapperException(String message, FaultBean faultInfo) <br>
     * A constructor where WrapperException is replaced with the name of the
     * generated wrapper exception and FaultBean is replaced by the name of the
     * generated fault bean.
     * <li> WrapperException(String message, FaultBean faultInfo, Throwable
     * cause) <br>
     * A constructor where WrapperException is replaced with the name of the
     * generated wrapper exception and FaultBean is replaced by the name of the
     * generated fault bean. The last argument, cause, may be used to convey
     * protocol specific fault information
     * </ul>
     */
    @SuppressWarnings("unchecked")
    public Throwable wrapFaultInfo(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause) {
        Class<?> exceptionClass = exceptionType.getPhysical();
        if (exceptionClass.isInstance(faultInfo)) {
            return (Throwable)faultInfo;
        }
        DataType<?> faultBeanType = exceptionType.getLogical();
        Class<?> faultBeanClass = faultBeanType.getPhysical();
        try {
            try {
                Constructor<?> constructor =
                    exceptionClass.getConstructor(new Class[] {String.class, faultBeanClass, Throwable.class});
                return (Exception)constructor.newInstance(new Object[] {message, faultInfo, cause});
            } catch (NoSuchMethodException e) {
                // Create a generic fault exception
                return new FaultException(message, faultInfo, cause);
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object getFaultInfo(Throwable exception, Class<?> faultBeanClass) {
        if (exception == null) {
            return null;
        }

        // Check if it's the generic FaultException
        if (exception instanceof FaultException) {
            return ((FaultException)exception).getFaultInfo();
        }

        try {
            Method method = exception.getClass().getMethod("getFaultInfo", EMPTY_CLASS_ARRAY);
            return method.invoke(exception, (Object[])null);
        } catch (NoSuchMethodException e) {
            // Follow the JAX-WS v2.1 spec section 3.7
            return createFaultBean(exception, faultBeanClass);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Object createFaultBean(Throwable exception, Class<?> faultBeanClass) {
        /**
         * For each getter in the exception and its superclasses, a property of the same 
         * type and name is added to the bean. The getCause, getLocalizedMessage and 
         * getStackTrace getters from java.lang.Throwable and the getClass getter from 
         * java.lang.Object are excluded from the list of getters to be mapped.
         */
        // Return the exception as-is if it's already the fault bean
        if (faultBeanClass.isInstance(exception)) {
            return exception;
        }
        try {
            Object faultBean = null;
            for (Constructor<?> ctor : faultBeanClass.getConstructors()) {
                Class<?>[] params = ctor.getParameterTypes();
                if (params.length == 1 && String.class == params[0]) {
                    faultBean = ctor.newInstance(exception.getMessage());
                } else if (params.length == 2 && String.class == params[0]
                    && Throwable.class.isAssignableFrom(params[1])) {
                    faultBean = ctor.newInstance(exception.getMessage(), exception);
                } else if (params.length == 0) {
                    faultBean = ctor.newInstance();
                }
                if (faultBean != null) {
                    break;
                }
            }
            if (faultBean == null) {
                return exception;
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(exception.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                Method getter = pd.getReadMethod();
                String name = getter.getName();
                if ("getClass".equals(name) || "getStackTrace".equals(name)
                    || "getCause".equals(name)
                    || "getLocalizedMessage".equals(name)) {
                    continue;
                }
                String prefix = "get";
                if (name.startsWith("get")) {
                    prefix = "get";
                } else if (name.startsWith("is")) {
                    prefix = "is";
                }
                Method setter = null;
                try {
                    setter =
                        faultBeanClass.getMethod("set" + name.substring(prefix.length()), new Class[] {getter
                            .getReturnType()});
                } catch (NoSuchMethodException e) {
                    continue;
                }
                Object prop = setter.invoke(faultBean, getter.invoke(exception, (Object[])null));
                setter.invoke(faultBean, prop);
            }
            return faultBean;
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean introspectFaultDataType(DataType<DataType> exceptionType) {
        Class<?> cls = exceptionType.getPhysical();
        if (cls == FaultException.class) {
            return true;
        }
        DataType faultType = (DataType)exceptionType.getLogical();
        Class<?> faultBean = null;
        WebFault fault = cls.getAnnotation(WebFault.class);
        if (fault != null) {
            QName faultName = new QName(fault.targetNamespace(), fault.name());
            XMLType xmlType = new XMLType(faultName, null);
            faultType.setLogical(xmlType);
            if (!"".equals(fault.faultBean())) {
                try {
                    faultBean = Class.forName(fault.faultBean(), false, cls.getClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new ServiceRuntimeException(e);
                }
            } else {
                Method m;
                try {
                    m = cls.getMethod("getFaultInfo", (Class[])null);
                    faultBean = m.getReturnType();
                } catch (NoSuchMethodException e) {
                    // Ignore
                }
            }
        }

        if (faultBean == null) {
            String faultBeanClassName = cls.getPackage().getName() + ".jaxws." + cls.getSimpleName() + "Bean";
            try {
                faultBean = Class.forName(faultBeanClassName, false, cls.getClassLoader());
            } catch (ClassNotFoundException e) {
                faultBean = cls;
            }
        }

        faultType.setPhysical(faultBean);
        // TODO: Use the databinding framework to introspect the fault bean class
        if (dataBindingExtensionPoint != null) {
            return dataBindingExtensionPoint.introspectType(faultType, null, Throwable.class
                .isAssignableFrom(faultBean));
        }

        return false;

    }

    public void setDataBindingExtensionPoint(DataBindingExtensionPoint dataBindingExtensionPoint) {
        this.dataBindingExtensionPoint = dataBindingExtensionPoint;
    }

}
