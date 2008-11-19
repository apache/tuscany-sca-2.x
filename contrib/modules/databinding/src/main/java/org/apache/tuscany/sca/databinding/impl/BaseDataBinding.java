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


import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.SimpleTypeMapper;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Base Implementation of DataBinding
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseDataBinding implements DataBinding {

    private Class<?> baseType;

    private String name;

    /**
     * Create a databinding with the base java type whose name will be used as
     * the name of the databinding
     * 
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected BaseDataBinding(Class<?> baseType) {
        this(baseType.getName(), baseType);
    }

    /**
     * Create a databinding with the name and base java type
     * 
     * @param name The name of the databinding
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected BaseDataBinding(String name, Class<?> baseType) {
        this.name = name;
        this.baseType = baseType;
    }

    @SuppressWarnings("unchecked")
    public boolean introspect(DataType type, Operation operation) {
        assert type != null;
        Class cls = type.getPhysical();
        if (baseType != null && baseType.isAssignableFrom(cls)) {
            type.setDataBinding(getName());
            if (type.getLogical() == null) {
                type.setLogical(XMLType.UNKNOWN);
            }
            return true;
        }
        return false;
    }

    public DataType introspect(Object value, Operation operation) {
        if (value == null) {
            return null;
        } else {
            DataType<Class> dataType = new DataTypeImpl<Class>(value.getClass(), value.getClass());
            if (introspect(dataType, (Operation) null)) {
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
     * @see org.apache.tuscany.sca.databinding.DataBinding#getWrapperHandler()
     */
    public WrapperHandler getWrapperHandler() {
        return null;
    }

    public Object copy(Object object, DataType dataType, Operation operation) {
        return object;
    }

    public XMLTypeHelper getXMLTypeHelper() {
        return null;
    }

}
