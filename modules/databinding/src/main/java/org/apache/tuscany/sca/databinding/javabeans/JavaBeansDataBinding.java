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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * DataBinding for JavaBeans
 *
 * @version $Rev$ $Date$
 */
public class JavaBeansDataBinding extends BaseDataBinding {
    private final static Logger logger = Logger.getLogger(JavaBeansDataBinding.class.getName());
    /**
     * Defining a weight to a very high number so that the transformer won't be picked
     * up by other paths unless it's the only available path
     */
    public static final int HEAVY_WEIGHT = 10000;
    public static final String NAME = "java:complexType";

    public JavaBeansDataBinding() {
        super(NAME, Object.class);
    }

    protected JavaBeansDataBinding(String name, Class<?> baseType) {
        super(name, baseType);
    }

    @Override
    public Object copy(Object arg, DataType dataType, Operation operation) {
        if (arg == null) {
            return null;
        }
        final Class<?> clazz = arg.getClass();
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

                // Work out which ClassLoader to use for deserializing arg
                // We want to use:
                //   * The ClassLoader of arg if it is not the System ClassLoader
                //   * The ThreadContext ClassLoader if the ClassLoader of arg is the System ClassLoader
                //     because Collection classes are loaded by the System ClassLoader but their contents
                //     may be loaded from another ClassLoader
                //
                ClassLoader classLoaderToUse = clazz.getClassLoader();
                if (classLoaderToUse == null)
                {
                    // ClassLoader of arg is the System ClassLoader so we will use the ThreadContext ClassLoader
                    // instead
                    classLoaderToUse = Thread.currentThread().getContextClassLoader();
                }

                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = getObjectInputStream(bis, classLoaderToUse);
                Object objectCopy = ois.readObject();
                ois.close();
                bis.close();
                return objectCopy;
            } else if (arg instanceof Cloneable) {
                Method clone;
                try {
                    clone = arg.getClass().getMethod("clone");
                    try {
                        return clone.invoke(arg, (Object[])null);
                    } catch (InvocationTargetException e) {
                        if (e.getTargetException() instanceof CloneNotSupportedException) {
                            // Ignore
                        } else {
                            throw new IllegalArgumentException(e);
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                } catch (NoSuchMethodException e) {
                    // Ignore it
                }
            }
                // return arg;
            logger.warning("Argument type '" + arg.getClass().getName()
                + "' is not Serializable or Cloneable. Pass-by-value is skipped.");
            return arg;
       } catch (Exception e) {
            throw new IllegalArgumentException("Pass-by-value is not supported for the given object: " + arg.getClass()
                .getName(), e);
        }
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
                    try {
                        // For OSGi, use context ClassLoader if the bundle ClassLoader cannot load the class
                        if (cl != Thread.currentThread().getContextClassLoader()) {
                            return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
                        }
                    } catch (ClassNotFoundException e1) {
                        // ignore
                    } catch (NoClassDefFoundError e1) {
                        // ignore
                    }
                    return super.resolveClass(desc);
                }
            }

        };
        return ois;
    }

}
