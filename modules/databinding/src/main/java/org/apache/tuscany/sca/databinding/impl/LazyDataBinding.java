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

package org.apache.tuscany.sca.databinding.impl;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.ExceptionHandler;
import org.apache.tuscany.sca.databinding.SimpleTypeMapper;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * A data binding facade allowing data bindings to be lazily loaded and
 * initialized.
 * 
 * @version $Rev$ $Date$
 */
public class LazyDataBinding implements DataBinding {

    private String name;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private DataBinding dataBinding;

    public LazyDataBinding(String name, ClassLoader classLoader, String className) {
        this.name = name;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    /**
     * Load and instantiate the data binding class.
     * 
     * @return The data binding.
     */
    @SuppressWarnings("unchecked")
    private DataBinding getDataBinding() {
        if (dataBinding == null) {
            try {
                Class<DataBinding> dataBindingClass =
                    (Class<DataBinding>)Class.forName(className, true, classLoader.get());
                Constructor<DataBinding> constructor = dataBindingClass.getConstructor();
                dataBinding = constructor.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return dataBinding;
    }

    public Object copy(Object object) {
        return getDataBinding().copy(object);
    }

    public String[] getAliases() {
        return null;
    }

    public ExceptionHandler getExceptionHandler() {
        return getDataBinding().getExceptionHandler();
    }

    public String getName() {
        return name;
    }

    public SimpleTypeMapper getSimpleTypeMapper() {
        return getDataBinding().getSimpleTypeMapper();
    }

    public WrapperHandler getWrapperHandler() {
        return getDataBinding().getWrapperHandler();
    }

    public boolean introspect(DataType dataType, Annotation[] annotations) {
        return getDataBinding().introspect(dataType, annotations);
    }

    public DataType introspect(Object value) {
        return getDataBinding().introspect(value);
    }

}
