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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.databinding.jaxb.XMLAdapterExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * A special data type that generate the class on demand
 * @version $Rev$ $Date$
 */
public class GeneratedDataTypeImpl implements DataType<XMLType> {
    private XMLAdapterExtensionPoint xmlAdapters;
    
    private Class<?> physical;
    private XMLType logical;

    private Map<Class<?>, Object> metaDataMap;
    private Method method;
    private String wrapperClassName;
    private String wrapperNamespace;
    private String wrapperName;
    private boolean request;
    private GeneratedClassLoader classLoader;

    private Class<? extends Throwable> exceptionClass;

    public GeneratedDataTypeImpl(XMLAdapterExtensionPoint xmlAdapters, Class<? extends Throwable> exceptionClass, GeneratedClassLoader cl) {
        super();
        this.exceptionClass = exceptionClass;
        this.classLoader = cl;
        QName name = FaultBeanGenerator.getElementName(exceptionClass);
        this.logical = new XMLType(name, name);
        this.xmlAdapters = xmlAdapters;
    }

    public GeneratedDataTypeImpl(XMLAdapterExtensionPoint xmlAdapters,
                                 Method m,
                                 String wrapperClassName,
                                 String wrapperNamespace,
                                 String wrapperName,
                                 boolean request,
                                 GeneratedClassLoader cl) {
        super();
        this.method = m;
        this.wrapperClassName = wrapperClassName;
        this.wrapperNamespace = wrapperNamespace;
        this.wrapperName = wrapperName;
        this.classLoader = cl;
        this.request = request;
        QName name = new QName(wrapperNamespace, wrapperName);
        this.logical = new XMLType(name, name);
        this.xmlAdapters = xmlAdapters;
    }

    public String getDataBinding() {
        return JAXBDataBinding.NAME;
    }

    public Type getGenericType() {
        return getPhysical();
    }

    public XMLType getLogical() {
        return logical;
    }

    public synchronized Class<?> getPhysical() {
        if (physical == null) {
            if (method != null) {
                WrapperBeanGenerator generator = new WrapperBeanGenerator();
                generator.setXmlAdapters(xmlAdapters);
                physical =
                    request ? generator.generateRequestWrapper(method, wrapperClassName, wrapperNamespace, wrapperName, classLoader)
                        : generator.generateResponseWrapper(method, wrapperClassName, wrapperNamespace, wrapperName, classLoader);
                ;
            } else if (exceptionClass != null) {
                FaultBeanGenerator faultBeanGenerator = new FaultBeanGenerator();
                faultBeanGenerator.setXmlAdapters(xmlAdapters);
                physical = faultBeanGenerator.generate(exceptionClass, classLoader);
            }
        }
        return physical;
    }

    public void setDataBinding(String dataBinding) {
        // NOP
    }

    public void setGenericType(Type genericType) {
        // NOP
    }

    public void setLogical(XMLType logical) {
        this.logical = logical;
    }

    public void setPhysical(Class<?> cls) {
        // NOP
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public <T> T getMetaData(Class<T> type) {
        return metaDataMap == null ? null : type.cast(metaDataMap.get(type));
    }

    public <T> void setMetaData(Class<T> type, T metaData) {
        if (metaDataMap == null) {
            metaDataMap = new ConcurrentHashMap<Class<?>, Object>();
        }
        metaDataMap.put(type, metaData);
    }
}
