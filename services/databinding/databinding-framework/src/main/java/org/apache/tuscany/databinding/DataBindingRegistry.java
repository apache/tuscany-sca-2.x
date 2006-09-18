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

package org.apache.tuscany.databinding;

import org.apache.tuscany.spi.model.DataType;

/**
 * The registry for data bindings
 */
public interface DataBindingRegistry {
    /**
     * Register a data binding
     * @param dataBinding
     */
    public void register(DataBinding dataBinding);

    /**
     * Look up a data binding by id
     * @param id
     * @return
     */
    public DataBinding getDataBinding(String id);

    /**
     * Unregister a data binding
     * @param id
     * @return
     */
    public DataBinding unregister(String id);
    
    /**
     * Introspect the java class to figure out what DataType supports it
     * @param javaType The java class or interface
     * @return
     */
    public DataType introspectType(Class<?> javaType);
    public DataType introspectType(Object value);
}
