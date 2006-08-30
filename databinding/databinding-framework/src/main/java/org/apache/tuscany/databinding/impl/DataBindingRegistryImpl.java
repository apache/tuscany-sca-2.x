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

package org.apache.tuscany.databinding.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Init;

/**
 * The default implementation of a data binding registry
 */
public class DataBindingRegistryImpl implements DataBindingRegistry {
    private final Map<String, DataBinding> bindings = new HashMap<String, DataBinding>();

    /**
     * @see org.apache.tuscany.databinding.DataBindingRegistry#getDataBinding(java.lang.String)
     */
    public DataBinding getDataBinding(String id) {
        return bindings.get(id.toLowerCase());
    }

    /**
     * @see org.apache.tuscany.databinding.DataBindingRegistry#register(org.apache.tuscany.databinding.DataBinding)
     */
    public void register(DataBinding dataBinding) {
        bindings.put(dataBinding.getName().toLowerCase(), dataBinding);
    }

    /**
     * @see org.apache.tuscany.databinding.DataBindingRegistry#unregister(java.lang.String)
     */
    public DataBinding unregister(String id) {
        return bindings.remove(id.toLowerCase());
    }

    @Init(eager = true)
    public void init() {
    }

    @SuppressWarnings("unchecked")
    public DataType<?> introspectType(Class<?> javaType) {
        DataType<?> dataType = null;
        for (DataBinding binding : bindings.values()) {
            dataType = binding.introspect(javaType);
            if (dataType != null) {
                return dataType;
            }
        }
        return null;
    }

}
