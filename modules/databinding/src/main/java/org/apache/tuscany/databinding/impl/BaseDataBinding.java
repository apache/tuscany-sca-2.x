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
package org.apache.tuscany.databinding.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.ExceptionHandler;
import org.apache.tuscany.databinding.SimpleTypeMapper;
import org.apache.tuscany.databinding.WrapperHandler;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

/**
 * Base Implementation of DataBinding
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseDataBinding implements DataBinding {

    protected DataBindingExtensionPoint registry;

    protected Class<?> baseType;

    protected String name;
    protected String[] aliases; 

    /**
     * Create a databinding with the base java type whose name will be used as
     * the name of the databinding
     * 
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected BaseDataBinding(Class<?> baseType) {
        this(baseType.getName(), null, baseType);
    }

    /**
     * Create a databinding with the name and base java type
     * 
     * @param name The name of the databinding
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected BaseDataBinding(String name, Class<?> baseType) {
        this(name, null, baseType);
    }
    
    /**
     * Create a databinding with the name and base java type
     * 
     * @param name The name of the databinding
     * @param aliases The aliases of the databinding
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected BaseDataBinding(String name, String[] aliases, Class<?> baseType) {
        this.name = name;
        this.baseType = baseType;
        this.aliases = aliases;
    }    

    public void setDataBindingRegistry(DataBindingExtensionPoint registry) {
        this.registry = registry;
    }

    public void init() {
        registry.addDataBinding(this);
    }

    @SuppressWarnings("unchecked")
    public boolean introspect(DataType type, Annotation[] annotations) {
        assert type != null;
        Type physical = type.getPhysical();
        if (physical instanceof ParameterizedType) {
            physical = ((ParameterizedType)physical).getRawType();
        }
        if (physical instanceof Class) {
            Class cls = (Class)physical;
            if (baseType != null && baseType.isAssignableFrom(cls)) {
                type.setDataBinding(getName());
                type.setLogical(baseType);
                return true;
            }
        }
        return false;
    }
    
    public DataType introspect(Object value) {
        if (value == null) {
            return null;
        } else {
            DataType<Class> dataType = new DataTypeImpl<Class>(value.getClass(), value.getClass());
            if (introspect(dataType, null)) {
                return dataType;
            } else {
                return null;
            }
        }
    }

    public final String getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.databinding.DataBinding#getWrapperHandler()
     */
    public WrapperHandler getWrapperHandler() {
        return null;
    }

    public ExceptionHandler getExceptionHandler() {
        return null;
    }

    public Object copy(Object object) {
        return object;
    }

    public SimpleTypeMapper getSimpleTypeMapper() {
        return new SimpleTypeMapperImpl();
    }

    public String[] getAliases() {
        return aliases;
    }

}
