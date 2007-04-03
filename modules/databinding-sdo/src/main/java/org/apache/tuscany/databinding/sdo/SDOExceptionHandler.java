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

package org.apache.tuscany.databinding.sdo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.databinding.ExceptionHandler;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.XMLType;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * SDO implementation of ExceptionHandler
 * 
 * @version $Rev$ $Date$
 */
public class SDOExceptionHandler implements ExceptionHandler {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    // FIXME: Need a way to pass in the HelperContext
    private HelperContext helperContext = HelperProvider.getDefaultContext();

    /**
     * <ul>
     * <li>WrapperException(String message, FaultBean faultInfo) <br>
     * A constructor where WrapperException is replaced with the name of the
     * generated wrapper exception and FaultBean is replaced by the name of the
     * generated fault bean.
     * <li> WrapperException(String message, FaultBean faultInfo, Throwable
     * cause) <br>
     * A constructor whereWrapperException is replaced with the name of the
     * generated wrapper exception and FaultBean is replaced by the name of the
     * generated fault bean. The last argument, cause, may be used to convey
     * protocol specific fault information
     * </ul>
     */
    public Exception createException(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause) {
        Class exceptionClass = (Class)exceptionType.getPhysical();
        DataType<?> faultBeanType = exceptionType.getLogical();
        Class faultBeanClass = (Class)faultBeanType.getPhysical();
        try {
            Constructor constructor =
                exceptionClass.getConstructor(new Class[] {String.class, faultBeanClass, Throwable.class});
            return (Exception)constructor.newInstance(new Object[] {message, faultInfo, cause});
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object getFaultInfo(Exception exception) {
        if (exception == null) {
            return null;
        }
        try {
            Method method = exception.getClass().getMethod("getFaultInfo", EMPTY_CLASS_ARRAY);
            return method.invoke(exception, (Object[])null);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public DataType<?> getFaultType(DataType exceptionDataType) {
        Class exceptionType = (Class) exceptionDataType.getPhysical();
        Class faultBeanClass = null;
        try {
            Method method = exceptionType.getMethod("getFaultInfo", EMPTY_CLASS_ARRAY);
            faultBeanClass = method.getReturnType();
        } catch (NoSuchMethodException e) {
            faultBeanClass = null;
        }
        if (faultBeanClass == null) {
            return null;
        }

        QName faultElement = null;
        try {
            Field field = exceptionType.getField("FAULT_ELEMENT");
            faultElement = (QName)field.get(null);
        } catch (NoSuchFieldException e) {
            // Fall back to type inspection
            Type type = helperContext.getTypeHelper().getType(faultBeanClass);
            if (type != null) {
                String ns = type.getURI();
                String name = helperContext.getXSDHelper().getLocalName(type);
                faultElement = new QName(ns, name);
            }
        } catch (Throwable e) {
            // Ignore
        }
        if (faultElement == null) {
            return null;
        }
        DataType<XMLType> faultType =
            new DataType<XMLType>(SDODataBinding.NAME, faultBeanClass, new XMLType(faultElement, null));
        return faultType;

    }

}
