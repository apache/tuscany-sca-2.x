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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * An interceptor to enforce pass-by-value semantics for remotable interfaces
 */
public class PassByValueInterceptor implements Interceptor {
    private Interceptor next;

    public Interceptor getNext() {
        return next;
    }

    public Message invoke(Message msg) {
        Object obj = msg.getBody();
        msg.setBody(copy((Object[]) obj));
        Message result = next.invoke(msg);
        if (!result.isFault()) {
            result.setBody(copy(result.getBody()));
        }
        return result;
    }

    public boolean isOptimizable() {
        return false;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public static Object[] copy(Object[] args) {
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
                    copiedArg = copy(args[i]);
                    map.put(args[i], copiedArg);
                    copiedArgs[i] = copiedArg;
                }
            }
        }
        return copiedArgs;
    }

    public static Object copy(Object arg) {
        if (arg == null) {
            return null;
        }
        final Class cls = arg.getClass();
        if (JavaIntrospectionHelper.isImmutable(cls)) {
            // Immutable classes
            return arg;
        }
        try {
            if (arg instanceof Serializable) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(arg);
                oos.close();
                bos.close();

                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis) {

                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        try {
                            return Class.forName(desc.getName(), false, cls.getClassLoader());
                        } catch (ClassNotFoundException e) {
                            return super.resolveClass(desc);
                        }
                    }

                };
                Object objectCopy = ois.readObject();
                ois.close();
                bis.close();
                return objectCopy;
            } else {
                throw new IllegalArgumentException("Pass-by-value is not supported for the given object");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Pass-by-value is not supported for the given object", e);
        }
    }

}
