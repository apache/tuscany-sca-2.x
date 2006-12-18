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
package org.apache.tuscany.spi.databinding.extension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;

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

    protected Class<?> baseType;

    protected String name;

    /**
     * Create a databinding with the base java type whose name will be used as
     * the name of the databinding
     * 
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
     */
    protected DataBindingExtension(Class<?> baseType) {
        this(baseType.getName(), baseType);
    }

    /**
     * Create a databinding with the name and base java type
     * 
     * @param name The name of the databinding
     * @param baseType The base java class or interface representing the
     *            databinding, for example, org.w3c.dom.Node
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
        if (baseType == null || javaType == null) {
            return null;
        }
        if (baseType.isAssignableFrom(javaType)) {
            return new DataType<Class>(name, javaType, baseType);
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

    public Object copy(Object arg) {
        if (arg == null) {
            return null;
        }
        final Class clazz = arg.getClass();
        if (String.class == clazz || clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz)) {
            // Immutable classes
            return arg;
        }
        try {
            if (arg instanceof Serializable) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = getObjectOutputStream(bos);
                oos.writeObject(arg);
                oos.close();
                bos.close();

                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = getObjectInputStream(bis, clazz.getClassLoader());
                Object objectCopy = ois.readObject();
                ois.close();
                bis.close();
                return objectCopy;
            } else {
                //return arg;
                throw new IllegalArgumentException(
                        "Pass-by-value is not supported for the given object");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Pass-by-value is not supported for the given object", e);
        }
    }

    protected ObjectOutputStream getObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }

    protected ObjectInputStream getObjectInputStream(InputStream is, final ClassLoader cl)
            throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
                    ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, cl);
                } catch (ClassNotFoundException e) {
                    return super.resolveClass(desc);
                }
            }

        };
        return ois;
    }

}
