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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;

import org.apache.tuscany.sca.databinding.util.LRUCache;

/**
 * @version $Rev$ $Date$
 */
public class JAXBContextCache {
    private static final int CACHE_SIZE = 128;

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

    protected static Class<?>[] JAXB_BUILTIN_CLASSES =
        {byte[].class, boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class,
         short.class, void.class, java.awt.Image.class, java.io.File.class, java.lang.Boolean.class,
         java.lang.Byte.class, java.lang.Character.class, java.lang.Class.class, java.lang.Double.class,
         java.lang.Float.class, java.lang.Integer.class, java.lang.Long.class, java.lang.Object.class,
         java.lang.Short.class, java.lang.String.class, java.lang.Void.class, java.math.BigDecimal.class,
         java.math.BigInteger.class, java.net.URI.class, java.net.URL.class, java.util.Calendar.class,
         java.util.Date.class, java.util.GregorianCalendar.class, java.util.UUID.class,
         javax.activation.DataHandler.class, javax.xml.bind.JAXBElement.class, javax.xml.datatype.Duration.class,
         javax.xml.datatype.XMLGregorianCalendar.class, javax.xml.namespace.QName.class,
         javax.xml.transform.Source.class};

    protected static final Set<Class<?>> BUILTIN_CLASSES_SET = new HashSet<Class<?>>(Arrays.asList(JAXB_BUILTIN_CLASSES));

    /*
    protected static Class<?>[] COMMON_ARRAY_CLASSES =
        new Class[] {char[].class, short[].class, int[].class, long[].class, float[].class, double[].class,
                     String[].class
                     };

    protected static final Set<Class<?>> COMMON_CLASSES_SET = new HashSet<Class<?>>(Arrays.asList(COMMON_ARRAY_CLASSES));
    */

    protected LRUCache<Object, JAXBContext> cache;
    protected Pool<JAXBContext, Marshaller>  mpool;
    protected Pool<JAXBContext, Unmarshaller> upool;

    // protected JAXBContext commonContext;
    protected JAXBContext defaultContext;

    public JAXBContextCache() {
        this(CACHE_SIZE, CACHE_SIZE, CACHE_SIZE);
    }

    public JAXBContextCache(int contextSize, int marshallerSize, int unmarshallerSize) {
        cache = new LRUCache<Object, JAXBContext>(contextSize);
        mpool = new Pool<JAXBContext, Marshaller>();
        upool = new Pool<JAXBContext, Unmarshaller>();
        defaultContext = getDefaultJAXBContext();
    }

    private static JAXBContext newJAXBContext(final Class<?>... classesToBeBound) throws JAXBException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>() {
                public JAXBContext run() throws JAXBException {
                    return JAXBContext.newInstance(classesToBeBound);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (JAXBException)e.getException();
        }
    }

    private static JAXBContext newJAXBContext(final String contextPath, final ClassLoader classLoader)
        throws JAXBException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>() {
                public JAXBContext run() throws JAXBException {
                    return JAXBContext.newInstance(contextPath, classLoader);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (JAXBException)e.getException();
        }
    }

    public static JAXBContext getDefaultJAXBContext() {
        try {
            return newJAXBContext();
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

    public Marshaller getMarshaller(JAXBContext context) throws JAXBException {
        Marshaller marshaller = mpool.get(context);
        if (marshaller == null) {
            marshaller = context.createMarshaller();
        }
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshaller;
    }

    public void releaseJAXBMarshaller(JAXBContext context, Marshaller marshaller) {
        if (marshaller != null) {
            marshaller.setAttachmentMarshaller(null);
            mpool.put(context, marshaller);
            // No point unsetting marshaller's JAXB_FRAGMENT property, since we'll just reset it when
            // doing the next get.
        }
    }

    public Unmarshaller getUnmarshaller(JAXBContext context) throws JAXBException {
        Unmarshaller unmarshaller = upool.get(context);
        if (unmarshaller == null) {
            unmarshaller = context.createUnmarshaller();
        }
        return unmarshaller;
    }

    public void releaseJAXBUnmarshaller(JAXBContext context, Unmarshaller unmarshaller) {
        if (unmarshaller != null) {
            unmarshaller.setAttachmentUnmarshaller(null);
            upool.put(context, unmarshaller);
        }
    }

    public LRUCache<Object, JAXBContext> getCache() {
        return cache;
    }

    public JAXBContext getJAXBContext(Class<?> cls) throws JAXBException {
        if (BUILTIN_CLASSES_SET.contains(cls)) {
            return defaultContext;
        }
        return getJAXBContext(new Class<?>[] {cls});
    }

    public JAXBContext getJAXBContext(Class<?>[] classes) throws JAXBException {
        Set<Class<?>> classSet = new HashSet<Class<?>>(Arrays.asList(classes));
        return getJAXBContext(classSet);
    }

    public JAXBContext getJAXBContext(Set<Class<?>> classes) throws JAXBException {
        // Remove the JAXB built-in types to maximize the cache hit
        Set<Class<?>> classSet = new HashSet<Class<?>>(classes);
        classSet.removeAll(BUILTIN_CLASSES_SET);

        // FIXME: [rfeng] Remove java classes that are mapped to the same XSD type to avoid
        // conflicts
        if (classSet.contains(Date[].class)) {
            classSet.remove(Calendar[].class);
        }

        if (classSet.contains(URI[].class)) {
            classSet.remove(UUID[].class);
        }

        if (classSet.contains(Source[].class)) {
            classSet.remove(Image[].class);
            classSet.remove(DataHandler[].class);
        }

        classSet = getJAXBClasses(classSet);

        if(classSet.isEmpty()) {
            return defaultContext;
        }

        synchronized (cache) {
            JAXBContext context = cache.get(classSet);
            if (context != null) {
                return context;
            }
            context = newJAXBContext(classSet.toArray(new Class<?>[classSet.size()]));
            cache.put(classSet, context);
            return context;
        }
    }

    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
        /*
        synchronized (upool) {
            upool.clear();
        }
        synchronized (upool) {
            upool.clear();
        }
        */
    }

    //
    // This inner class is copied in its entirety from the Axis2 utility class,
    // org.apache.axis2.jaxws.message.databinding.JAXBUtils.   We could look into extending but it's such a basic data structure
    // without other dependencies so we might be better off copying it and avoiding a new
    // Axis2 dependency here.
    //

    /**
     * Pool a list of items for a specific key
     *
     * @param <K> Key
     * @param <V> Pooled object
     */
    private static class Pool<K,V> {
        private SoftReference<Map<K,List<V>>> softMap =
            new SoftReference<Map<K,List<V>>>(
                    new ConcurrentHashMap<K, List<V>>());

        // The maps are freed up when a LOAD FACTOR is hit
        private static final int MAX_LIST_FACTOR = 50;
        private static final int MAX_LOAD_FACTOR = 32;  // Maximum number of JAXBContext to store

        /**
         * @param key
         * @return removed item from pool or null.
         */
        public V get(K key) {
            List<V> values = getValues(key);
            synchronized (values) {
                if (values.size()>0) {
                    V v = values.remove(values.size()-1);
                    return v;

                }
            }
            return null;
        }

        /**
         * Add item back to pool
         * @param key
         * @param value
         */
        public void put(K key, V value) {
            adjustSize();
            List<V> values = getValues(key);
            synchronized (values) {
                if (values.size() < MAX_LIST_FACTOR) {
                    values.add(value);
                }
            }
        }

        /**
         * Get or create a list of the values for the key
         * @param key
         * @return list of values.
         */
        private List<V> getValues(K key) {
            Map<K,List<V>> map = softMap.get();
            List<V> values = null;
            if (map != null) {
                values = map.get(key);
                if(values !=null) {
                    return values;
                }
            }
            synchronized (this) {
                if (map != null) {
                    values = map.get(key);
                }
                if (values == null) {
                    if (map == null) {
                        map = new ConcurrentHashMap<K, List<V>>();
                        softMap =
                            new SoftReference<Map<K,List<V>>>(map);
                    }
                    values = new ArrayList<V>();
                    map.put(key, values);

                }
                return values;
            }
        }

        /**
         * AdjustSize
         * When the number of keys exceeds the maximum load, half
         * of the entries are deleted.
         *
         * The assumption is that the JAXBContexts, UnMarshallers, Marshallers, etc. require
         * a large footprint.
         */
        private void adjustSize() {
            Map<K,List<V>> map = softMap.get();
            if (map != null && map.size() > MAX_LOAD_FACTOR) {
                // Remove every other Entry in the map.
                Iterator it = map.entrySet().iterator();
                boolean removeIt = false;
                while (it.hasNext()) {
                    it.next();
                    if (removeIt) {
                        it.remove();
                    }
                    removeIt = !removeIt;
                }
            }
        }
    }

    /**
     * Find the JAXB classes (looking into packages) to be bound
     * @param classes A collection of classes
     * @return A set of classes that include the ObjectFactory and indexed JAXB classes
     * @throws JAXBException
     */
    private static Set<Class<?>> getJAXBClasses(Collection<Class<?>> classes) throws JAXBException {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        // Index the packages
        Map<Package, ClassLoader> pkgs = getPackages(classes);
        Set<Package> nonJAXBPackages = new HashSet<Package>();
        for (Map.Entry<Package, ClassLoader> p : pkgs.entrySet()) {
            Package pkg = p.getKey();
            if (pkg == null) {
                continue;
            }
            Set<Class<?>> set = getJAXBClasses(pkg.getName(), p.getValue());
            if (set.isEmpty()) {
                // No JAXB package
                nonJAXBPackages.add(pkg);
            } else {
                // Add JAXB ObjectFactory and indexed classes
                classSet.addAll(set);
            }
        }
        // Adding classes that are not part of JAXB packages
        for (Class<?> cls : classes) {
            Package pkg = getPackage(cls);
            if (pkg == null || nonJAXBPackages.contains(pkg)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * Get the package for a class, taking array into account
     * @param cls
     * @return
     */
    private static Package getPackage(Class<?> cls) {
        Class<?> type = cls;
        while (type.isArray()) {
            type = type.getComponentType();
        }
        return type.getPackage();
    }

    /**
     * Get a map of packages
     * @param classes
     * @return
     */
    private static Map<Package, ClassLoader> getPackages(Collection<Class<?>> classes) {
        Map<Package, ClassLoader> pkgs = new HashMap<Package, ClassLoader>();
        for (Class<?> cls : classes) {
            Package pkg = getPackage(cls);
            if (pkg != null) {
                pkgs.put(pkg, cls.getClassLoader());
            }
        }
        return pkgs;
    }

    /**
     * Find ObjectFactory and indexed JAXB classes for the package
     * @param pkg
     * @param classLoader
     * @return
     * @throws JAXBException
     */
    private static Set<Class<?>> getJAXBClasses(String pkg, ClassLoader classLoader) throws JAXBException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        List<Class<?>> indexedClasses;

        // look for ObjectFactory and load it
        final Class<?> o;
        try {
            o = forName(pkg + ".ObjectFactory", false, classLoader);
            classes.add(o);
        } catch (ClassNotFoundException e) {
            // not necessarily an error
        }

        // look for jaxb.index and load the list of classes
        try {
            indexedClasses = loadIndexedClasses(pkg, classLoader);
        } catch (IOException e) {
            throw new JAXBException(e);
        }
        if (indexedClasses != null) {
            classes.addAll(indexedClasses);
        }

        return classes;
    }

    /**
     * Look for jaxb.index file in the specified package and load it's contents
     *
     * @param pkg package name to search in
     * @param classLoader ClassLoader to search in
     * @return a List of Class objects to load, null if there weren't any
     * @throws IOException if there is an error reading the index file
     * @throws JAXBException if there are any errors in the index file
     */
    private static List<Class<?>> loadIndexedClasses(String pkg, ClassLoader classLoader) throws IOException,
        JAXBException {
        if (classLoader == null) {
            return null;
        }
        final String resource = pkg.replace('.', '/') + "/jaxb.index";
        final InputStream resourceAsStream = classLoader.getResourceAsStream(resource);

        if (resourceAsStream == null) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
        try {
            List<Class<?>> classes = new ArrayList<Class<?>>();
            String className = in.readLine();
            while (className != null) {
                className = className.trim();
                if (className.startsWith("#") || (className.length() == 0)) {
                    className = in.readLine();
                    continue;
                }

                try {
                    classes.add(forName(pkg + '.' + className, false, classLoader));
                } catch (ClassNotFoundException e) {
                    throw new JAXBException(e);
                }

                className = in.readLine();
            }
            return classes;
        } finally {
            in.close();
        }
    }

}

