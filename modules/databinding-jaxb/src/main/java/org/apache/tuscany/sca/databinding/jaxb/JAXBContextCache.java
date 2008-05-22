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

package org.apache.tuscany.sca.databinding.jaxb;

import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

/**
 * @version $Rev$ $Date$
 */
public class JAXBContextCache {
    private static HashMap<String, Class<?>> loadClassMap = new HashMap<String, Class<?>>();

    static {
        loadClassMap.put("byte", byte.class);
        loadClassMap.put("int", int.class);
        loadClassMap.put("short", short.class);
        loadClassMap.put("long", long.class);
        loadClassMap.put("float", float.class);
        loadClassMap.put("double", double.class);
        loadClassMap.put("boolean", boolean.class);
        loadClassMap.put("char", char.class);
        loadClassMap.put("void", void.class);
    }

    protected static Class<?>[] COMMON_CLASSES =
        new Class[] {boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class,
                     double.class, Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class,
                     Float.class, Double.class, String.class, boolean[].class, byte[].class, char[].class,
                     short[].class, int[].class, long[].class, float[].class, double[].class, String[].class,
                     BigInteger.class, BigDecimal.class, Calendar.class, Date.class, QName.class, URI.class,
                     XMLGregorianCalendar.class, Duration.class,
                     // java.lang.Object.class, 
                     Image.class,
                     // java2XSD.put(javax.activation.DataHandler.class, "base64Binary");
                     Source.class, UUID.class

        };

    protected static final Set<Class<?>> COMMON_CLASSES_SET = new HashSet<Class<?>>(Arrays.asList(COMMON_CLASSES));

    protected LRUCache<Object, JAXBContext> cache;
    protected LRUCache<JAXBContext, Unmarshaller> upool;
    protected LRUCache<JAXBContext, Marshaller> mpool;
    protected JAXBContext commonContext;

    public JAXBContextCache() {
        this(64, 64, 64);
    }

    public JAXBContextCache(int contextSize, int marshallerSize, int unmarshallerSize) {
        cache = new LRUCache<Object, JAXBContext>(new WeakHashMap<Object, JAXBContext>(), contextSize);
        upool = new LRUCache<JAXBContext, Unmarshaller>(new WeakHashMap<JAXBContext, Unmarshaller>(), unmarshallerSize);
        mpool = new LRUCache<JAXBContext, Marshaller>(new WeakHashMap<JAXBContext, Marshaller>(), marshallerSize);
        commonContext = getCommonJAXBContext();
    }

    public static JAXBContext getCommonJAXBContext() {
        try {
            return JAXBContext.newInstance(COMMON_CLASSES);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @param name of primitive type
     * @return primitive Class or null
     */
    public static Class<?> getPrimitiveClass(String text) {
        return loadClassMap.get(text);
    }

    /**
     * Return the class for this name
     *
     * @return Class
     */
    private static Class<?> forName(final String className, final boolean initialize, final ClassLoader classloader)
        throws ClassNotFoundException {
        // NOTE: This method must remain private because it uses AccessController
        Class<?> cl = null;
        try {
            cl = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                public Class<?> run() throws ClassNotFoundException {
                    // Class.forName does not support primitives
                    Class<?> cls = getPrimitiveClass(className);
                    if (cls == null) {
                        cls = Class.forName(className, initialize, classloader);
                    }
                    return cls;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (ClassNotFoundException)e.getException();
        }

        return cl;
    }

    /**
     * @param p  Package
     * @param cl
     * @return true if each package has a ObjectFactory class or package-info
     */
    public static boolean checkPackage(String p, ClassLoader cl) {

        // Each package must have an ObjectFactory
        try {
            Class<?> cls = forName(p + ".ObjectFactory", false, cl);
            if (cls != null) {
                return true;
            }
            //Catch Throwable as ClassLoader can throw an NoClassDefFoundError that
            //does not extend Exception. So we will absorb any Throwable exception here.
        } catch (Throwable e) {
            // Ignore
        }

        try {
            Class<?> cls = forName(p + ".package-info", false, cl);
            if (cls != null) {
                return true;
            }
            //Catch Throwable as ClassLoader can throw an NoClassDefFoundError that
            //does not extend Exception. So we will absorb any Throwable exception here.
        } catch (Throwable e) {
            // Ignore
        }

        return false;
    }

    public Marshaller getMarshaller(JAXBContext context) throws JAXBException {
        synchronized (mpool) {
            Marshaller marshaller = mpool.get(context);
            if (marshaller == null) {
                marshaller = context.createMarshaller();
                mpool.put(context, marshaller);
            }
            return marshaller;
        }
    }

    public Unmarshaller getUnmarshaller(JAXBContext context) throws JAXBException {
        synchronized (upool) {
            Unmarshaller unmarshaller = upool.get(context);
            if (unmarshaller == null) {
                unmarshaller = context.createUnmarshaller();
                upool.put(context, unmarshaller);
            }
            return unmarshaller;
        }
    }

    public JAXBContext getJAXBContext(Class<?> cls) throws JAXBException {
        if (COMMON_CLASSES_SET.contains(cls)) {
            return commonContext;
        }
        synchronized (cache) {
            JAXBContext context = cache.get(cls);
            if (context != null) {
                return context;
            }
            Package pkg = cls.getPackage();
            if (pkg != null) {
                context = cache.get(pkg);
                if (context != null) {
                    return context;
                }
            }

            if (pkg != null && checkPackage(pkg.getName(), cls.getClassLoader())) {
                context = JAXBContext.newInstance(pkg.getName(), cls.getClassLoader());
                cache.put(pkg, context);
            } else {
                context = JAXBContext.newInstance(cls);
                cache.put(cls, context);
            }
            return context;

        }
    }

    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
        synchronized (upool) {
            upool.clear();
        }
        synchronized (upool) {
            upool.clear();
        }
    }

    public static class LRUCache<K, V> {
        private Map<K, V> cache;
        private List<K> keyQueue;
        private int queueSizeThreshold;

        public LRUCache(int queueSizeThreshold) {
            super();
            this.cache = new HashMap<K, V>();
            this.keyQueue = new ArrayList<K>();
            this.queueSizeThreshold = queueSizeThreshold;
        }

        public LRUCache(Map<K, V> cache, int queueSizeThreshold) {
            super();
            this.cache = cache;
            this.keyQueue = new ArrayList<K>(cache.keySet());
            this.queueSizeThreshold = queueSizeThreshold;
        }

        public V get(K key) {
            V value = cache.get(key);
            if (value != null) {
                // Move the most recently used key to the front of the queue
                if (!key.equals(keyQueue.get(0))) {
                    keyQueue.remove(key);
                    keyQueue.add(0, key);
                }
            }
            return value;
        }

        public void put(K key, V value) {
            if (cache.containsKey(key)) {
                // Adjust the key usage
                if (!key.equals(keyQueue.get(0))) {
                    keyQueue.remove(key);
                    keyQueue.add(0, key);
                }
            } else {
                if (keyQueue.size() >= queueSizeThreshold) {
                    // Remove the least recently used key
                    K last = keyQueue.remove(keyQueue.size() - 1);
                    keyQueue.add(0, key);
                    cache.remove(last);
                } else {
                    keyQueue.add(0, key);
                }
            }
            cache.put(key, value);
        }

        public V remove(K key) {
            V data = cache.remove(key);
            keyQueue.remove(key);
            return data;
        }

        public void clear() {
            cache.clear();
            keyQueue.clear();
        }

        public Map<K, V> getCache() {
            return Collections.unmodifiableMap(cache);
        }

    }

}
