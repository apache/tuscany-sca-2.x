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

package org.apache.tuscany.sca.databinding.javabeans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.tuscany.sca.databinding.ExceptionHandler;
import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;

/**
 * DataBinding for JavaBeans
 */
public class JavaBeansDataBinding extends BaseDataBinding {
    /**
     * Defining a weight to a very high number so that the transformer won't be picked
     * up by other paths unless it's the only available path
     */
    public static final int HEAVY_WEIGHT = 10000;
    public static final String NAME = Object.class.getName();

    public JavaBeansDataBinding() {
        super(NAME, Object.class);
    }
    
    @Override
    public Object copy(Object arg) {
        if (arg == null) {
            return null;
        }
        final Class clazz = arg.getClass();
        if (String.class == clazz || clazz.isPrimitive()
            || Number.class.isAssignableFrom(clazz)
            || Boolean.class.isAssignableFrom(clazz)
            || Character.class.isAssignableFrom(clazz)
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
                // return arg;
                throw new IllegalArgumentException("Argument type '" + arg.getClass().getCanonicalName() + "' is not Serializable. " + 
                                                   " Pass-by-value cannot be performed on this argument");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Pass-by-value is not supported for the given object", e);
        }
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new JavaBeansExceptionHandler();
    }

    protected ObjectOutputStream getObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }

    protected ObjectInputStream getObjectInputStream(InputStream is, final ClassLoader cl) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
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
