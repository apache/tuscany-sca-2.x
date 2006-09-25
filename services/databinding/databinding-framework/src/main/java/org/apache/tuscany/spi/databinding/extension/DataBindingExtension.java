/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.spi.databinding.extension;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Base Implementation of DataBinding
 */
@Scope("MODULE")
@Service(DataBinding.class)
public abstract class DataBindingExtension implements DataBinding {
    protected DataBindingRegistry registry;

    protected Class<?> baseType = null;

    protected String name = null;

    /**
     * Create a databinding with the base java type whose name will be used as the name of the databinding
     * 
     * @param baseType The base java class or interface representing the databinding, for example, org.w3c.dom.Node
     */
    protected DataBindingExtension(Class<?> baseType) {
        this(baseType.getName(), baseType);
    }

    /**
     * Create a databinding with the name and base java type
     * 
     * @param name The name of the databinding
     * @param baseType The base java class or interface representing the databinding, for example, org.w3c.dom.Node
     */
    protected DataBindingExtension(String name, Class<?> baseType) {
        this.name = name;
        this.baseType = baseType;
    }

    @Autowire
    public void setDataBindingRegistry(DataBindingRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init() {
        registry.register(this);
    }

    public DataType introspect(Class<?> javaType) {
        if (baseType.isAssignableFrom(javaType)) {
            DataType<Class> dataType = new DataType<Class>(name, javaType, baseType);
            return dataType;
        } else {
            return null;
        }
    }

    public DataType introspect(Object value) {
        if (value == null) {
            return null;
        } else {
            return introspect(value.getClass());
        }
    }

    public final String getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.DataBinding#getWrapperHandler()
     */
    public WrapperHandler getWrapperHandler() {
        return null;
    }
}
