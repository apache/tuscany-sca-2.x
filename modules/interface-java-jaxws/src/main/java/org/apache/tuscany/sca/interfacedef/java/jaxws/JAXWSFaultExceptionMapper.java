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
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.osoa.sca.ServiceRuntimeException;

/**
 * JAX-WS ExceptionHandler
 * 
 * @version $Rev$ $Date$
 */
public class JAXWSFaultExceptionMapper implements FaultExceptionMapper {
    public static final String GETCAUSE = "getCause";
    public static final String GETLOCALIZEDMESSAGE = "getLocalizedMessage";
    public static final String GETSTACKTRACE = "getStackTrace";
    public static final String GETCLASS = "getClass";

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private DataBindingExtensionPoint dataBindingExtensionPoint;

    public JAXWSFaultExceptionMapper(DataBindingExtensionPoint dataBindingExtensionPoint) {
        super();
        this.dataBindingExtensionPoint = dataBindingExtensionPoint;
    }

    /**
     * The following is quoted from the JAX-WS Specification v2.1
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
    public Throwable wrapFaultInfo(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause, Operation operation) {
        Class<?> exceptionClass = exceptionType.getPhysical();
        if (exceptionClass.isInstance(faultInfo)) {
            return (Throwable)faultInfo;
        }
        DataType<?> faultBeanType = exceptionType.getLogical();
        Class<?> faultBeanClass = faultBeanType.getPhysical();
        try {
            Throwable exc =
                newInstance((Class<? extends Throwable>)exceptionClass, message, faultBeanClass, faultInfo, cause);
            // Include the elem name into the FaultException we build so it can be used for matching in the DataTransformationInterceptor
            // 
            // Note this may happen even if we find a constructor above, that is the type of the non-generic fault exc may be an instance
            // of FaultException
            //
            if ((exc instanceof FaultException) && (faultBeanType.getLogical() instanceof XMLType)) {
                FaultException faultExc = (FaultException)exc;
                DataType<XMLType> faultBeanXMLType = (DataType<XMLType>)faultBeanType;
                XMLType faultLogical = faultBeanXMLType.getLogical();
                faultExc.setFaultName(faultLogical.getElementName());
            }
            return exc;
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Throwable newInstance(Class<? extends Throwable> exceptionClass,
                                  String message,
                                  Class<?> faultBeanClass,
                                  Object faultInfo,
                                  Throwable cause) throws Exception {
        Throwable ex = null;
        Constructor<? extends Throwable> ctor = null;
        try {
            // Get the message property
            Method getMessage = faultBeanClass.getMethod("getMessage");
            message = (String)getMessage.invoke(faultInfo);
        } catch (Throwable e) {
            // Ignore
        }
        try {
            // FIXME: What about if the faultBeanClass is a subclass of the argument type?
            ctor = exceptionClass.getConstructor(String.class, faultBeanClass, Throwable.class);
            ex = ctor.newInstance(message, faultInfo, cause);
        } catch (NoSuchMethodException e1) {
            try {
                ctor = exceptionClass.getConstructor(String.class, faultInfo.getClass());
                ex = ctor.newInstance(message, faultInfo);
            } catch (NoSuchMethodException e2) {
                try {
                    ctor = exceptionClass.getConstructor(String.class, Throwable.class);
                    ex = ctor.newInstance(message, cause);
                    populateException(ex, faultInfo);
                } catch (NoSuchMethodException e3) {
                    try {
                        ctor = exceptionClass.getConstructor(String.class);
                        ex = ctor.newInstance(message);
                        populateException(ex, faultInfo);
                    } catch (NoSuchMethodException e4) {
                        ctor = exceptionClass.getConstructor();
                        if (ctor != null) {
                            ex = ctor.newInstance();
                            populateException(ex, faultInfo);
                        } else {
                            ex = new FaultException(message, faultInfo, cause);
                        }
                    }
                }
            }
        }
        return ex;
    }

    /**
     * Populate the java exception from the fault bean
     * @param ex
     * @param faultBean
     * @throws Exception
     */
    private void populateException(Throwable ex, Object faultBean) throws Exception {
        PropertyDescriptor props[] = Introspector.getBeanInfo(faultBean.getClass()).getPropertyDescriptors();
        for (PropertyDescriptor p : props) {
            Method getter = p.getReadMethod();
            Method setter = p.getWriteMethod();
            if (getter == null || setter == null) {
                continue;
            }
            try {
                Method m = ex.getClass().getMethod(setter.getName(), setter.getParameterTypes());
                Object pv = getter.invoke(faultBean);
                m.invoke(ex, pv);
            } catch (Exception e) {
                // Ignore;
            }
        }
    }

    public Object getFaultInfo(Throwable exception, Class<?> faultBeanClass, Operation operation) {
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
            // Follow the JAX-WS v2.1 Specification section 3.7
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
                if (!isMappedGetter(name)) {
                    continue;
                }
                Method setter = null;
                try {
                    setter = faultBeanClass.getMethod("set" + capitalize(pd.getName()), getter.getReturnType());
                } catch (NoSuchMethodException e) {
                    continue;
                }
                Object prop = getter.invoke(exception);
                setter.invoke(faultBean, prop);
            }
            return faultBean;
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean introspectFaultDataType(DataType<DataType> exceptionType, Operation operation, final boolean generatingFaultBean) {
        QName faultName = null;
        boolean result = false;

        final Class<?> cls = exceptionType.getPhysical();
        if (cls == FaultException.class) {
            return true;
        }
        DataType faultType = (DataType)exceptionType.getLogical();
        Class<?> faultBean = null;
        final WebFault fault = cls.getAnnotation(WebFault.class);
        if (fault != null) {
            if (!"".equals(fault.name()) || !"".equals(fault.targetNamespace())) {
                QName faultQName = ((XMLType)faultType.getLogical()).getElementName();
                String faultNS =
                    "".equals(fault.targetNamespace()) ? faultQName.getNamespaceURI() : fault.targetNamespace();
                String faultLocal = "".equals(fault.name()) ? faultQName.getLocalPart() : fault.name();
                faultName = new QName(faultNS, faultLocal);
                XMLType xmlType = new XMLType(faultName, null);
                faultType.setLogical(xmlType);
            }
            if (!"".equals(fault.faultBean())) {
                faultBean = AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
                    public Class<?> run() {
                        try {
                            return Class.forName(fault.faultBean(), false, cls.getClassLoader());
                        } catch (ClassNotFoundException e) {
                            throw new ServiceRuntimeException(e);
                        }
                    }
                });
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
            final String faultBeanClassName = cls.getPackage().getName() + ".jaxws." + cls.getSimpleName() + "Bean";
            final QName qname = faultName;
            faultType = AccessController.doPrivileged(new PrivilegedAction<DataType<XMLType>>() {
                public DataType<XMLType> run() {
                    try {
                        Class<?> faultBean = Class.forName(faultBeanClassName, false, cls.getClassLoader());
                        return new DataTypeImpl<XMLType>(faultBean, new XMLType(qname, qname));
                    } catch (ClassNotFoundException e) {
                        if (generatingFaultBean) {
                            Class<? extends Throwable> t = (Class<? extends Throwable>)cls;
                            GeneratedClassLoader cl = new GeneratedClassLoader(t.getClassLoader());
                            GeneratedDataTypeImpl dt = new GeneratedDataTypeImpl(t, cl);
                            return dt;
                        } else {
                            return new DataTypeImpl<XMLType>(cls, new XMLType(qname, qname));
                        }
                    }
                }
            });
        } else {
            faultType.setDataBinding(null);
            faultType.setGenericType(faultBean);
            faultType.setPhysical(faultBean);
        }

        // TODO: Use the databinding framework to introspect the fault bean class
        if (faultType.getDataBinding() == null && dataBindingExtensionPoint != null) {
            faultBean = faultType.getPhysical();
            result =
                dataBindingExtensionPoint.introspectType(faultType, operation);
        }
        ((DataType) exceptionType).setLogical(faultType);

        /*
         The introspection of the fault DT may not have calculated the correct element name, 
         though we may have already done this in this method.  Let's look at the DataType now 
         that introspection is done, and, if it has an XMLType, let's set the element to the 
         'faultName' if we calculated one.
         */
        if ((faultName != null) && (faultType.getLogical() instanceof XMLType)) {
            XMLType faultTypeXML = (XMLType)faultType.getLogical();
            // The element name (if set) should match the fault name
            faultTypeXML.setElementName(faultName);
        }

        return result;
    }

    public static boolean isMappedGetter(String methodName) {
        if (GETCAUSE.equals(methodName) || GETLOCALIZEDMESSAGE.equals(methodName)
            || GETSTACKTRACE.equals(methodName)
            || GETCLASS.equals(methodName)) {
            return false;
        } else {
            return true;
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        } else {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }

    public void setDataBindingExtensionPoint(DataBindingExtensionPoint dataBindingExtensionPoint) {
        this.dataBindingExtensionPoint = dataBindingExtensionPoint;
    }

}
