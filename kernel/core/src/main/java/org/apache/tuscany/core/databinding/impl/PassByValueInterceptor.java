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

package org.apache.tuscany.core.databinding.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * An interceptor to enforce pass-by-value semantics for remotable interfaces
 *
 * @version $Rev$ $Date$
 */
public class PassByValueInterceptor implements Interceptor {
    private DataBinding[] argsDataBindings;
    private DataBinding resultDataBinding;

    private DataBinding dataBinding;

    private Interceptor next;

    public Interceptor getNext() {
        return next;
    }

    public boolean isOptimizable() {
        return false;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        Object obj = msg.getBody();
        msg.setBody(copy((Object[]) obj));
        Message result = getNext().invoke(msg);

        if (!result.isFault()) {
            result.setBody(copy(result.getBody(), getResultDataBinding()));
        }
        return result;
    }


    public Object[] copy(Object[] args) {
        if (args == null) {
            return null;
        }
        Object[] copiedArgs = new Object[args.length];
        Map<Object, Object> map = new IdentityHashMap<Object, Object>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                copiedArgs[i] = null;
            } else {
                Object copiedArg = map.get(args[i]);
                if (copiedArg != null) {
                    copiedArgs[i] = copiedArg;
                } else {
                    DataBinding dataBinding =
                        (getArgsDataBindings() != null) ? getArgsDataBindings()[i] : null;
                    copiedArg = copy(args[i], dataBinding);
                    map.put(args[i], copiedArg);
                    copiedArgs[i] = copiedArg;
                }
            }
        }
        return copiedArgs;
    }

    public Object copy(Object arg, DataBinding argDataBinding) {
        Object copiedArg;
        if (dataBinding != null) {
            copiedArg = dataBinding.copy(arg);
        } else {
            if (argDataBinding != null) {
                copiedArg = argDataBinding.copy(arg);
            } else {
                final Class clazz = arg.getClass();
                if (JavaIntrospectionHelper.isImmutable(clazz)) {
                    // Immutable classes
                    return arg;
                }
                copiedArg = copyJavaObject(arg);
            }
        }
        return copiedArg;
    }

    private Object copyJavaObject(Object arg) {
        try {
            return deserializeJavaObject(serializeJavaObject(arg));
        } catch (IllegalArgumentException e) {
            throw e;
            //System.out.println("Problem serializing...");
            //return arg;
        }
    }

    public byte[] serializeJavaObject(Object arg) throws IllegalArgumentException {
        if (arg == null) {
            return null;
        }

        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;

        try {
            if (arg instanceof Serializable) {
                bos = new ByteArrayOutputStream();
                oos = getObjectOutputStream(bos);
                oos.writeObject(arg);

                return bos.toByteArray();
            } else {
                throw new IllegalArgumentException("Unable to serialize using Java Serialization");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception while serializing argument ", e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Exception while serializing argument ", e);
            }
        }
    }

    public Object deserializeJavaObject(byte[] arg) {
        if (arg == null) {
            return null;
        }
        final Class clazz = arg.getClass();
        if (JavaIntrospectionHelper.isImmutable(clazz)) {
            // Immutable classes
            return arg;
        }

        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;

        try {
            bis = new ByteArrayInputStream(arg);
            ois = getObjectInputStream(bis, clazz.getClassLoader());

            return ois.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception when attempting to Java Deserialization of object ", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Exception when attempting to Java Deserialization of object ", e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                assert bis != null;
                bis.close();
            } catch (IOException e) {
                throw new IllegalArgumentException("Exception when attempting to Java Deserialization of object ", e);
            }
        }
    }

    protected ObjectOutputStream getObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }

    protected ObjectInputStream getObjectInputStream(InputStream is, final ClassLoader cl) throws IOException {
        return new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, cl);
                } catch (ClassNotFoundException e) {
                    return super.resolveClass(desc);
                }
            }

        };
    }

    public DataBinding getDataBinding() {
        return dataBinding;
    }

    public void setDataBinding(DataBinding dataBinding) {
        this.dataBinding = dataBinding;
    }

    public DataBinding[] getArgsDataBindings() {
        return argsDataBindings;
    }

    public void setArgsDataBindings(DataBinding[] argsDataBindings) {
        this.argsDataBindings = argsDataBindings;
    }

    public DataBinding getResultDataBinding() {
        return resultDataBinding;
    }

    public void setResultDataBinding(DataBinding retDataBinding) {
        this.resultDataBinding = retDataBinding;
    }
}
