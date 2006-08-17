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

package org.apache.tuscany.databinding.extension;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Init;

/**
 * Base Implementation of DataBinding
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class DataBindingExtension implements DataBinding {
    protected Map<String, Object> attrs = new HashMap<String, Object>();

    protected DataBindingRegistry registry;

    protected boolean isSink = false;

    protected Class baseType = null;

    protected String name = null;

    protected DataBindingExtension(Class baseType) {
        this(baseType, false);
    }

    protected DataBindingExtension(Class baseType, boolean isSink) {
        this(baseType.getName(), baseType, isSink);
    }

    protected DataBindingExtension(String name, Class baseType, boolean isSink) {
        this.name = name;
        this.baseType = baseType;
        this.isSink = isSink;
    }

    @Autowire
    public void setDataBindingRegistry(DataBindingRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init() {
        registry.register(this);
    }

    protected Class getBaseType() {
        return baseType;
    }

    /**
     * @see org.apache.tuscany.databinding.DataType#isSinkOnly()
     */
    public boolean isSinkOnly() {
        return isSink;
    }

    @SuppressWarnings("unchecked")
    public boolean isSupported(Class javaClass) {
        return getBaseType().isAssignableFrom(javaClass);
    }

    public String getName() {
        return name;
    }

    public Object getAttribute(String name) {
        return attrs.get(name);
    }

    public void setAttribute(String name, Object value) {
        attrs.put(name, value);
    }

}
