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
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSchema;
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

    protected static Class<?>[] COMMON_ARRAY_CLASSES =
        new Class[] {char[].class, short[].class, int[].class, long[].class, float[].class, double[].class,
                     String[].class
                     };

    protected static final Set<Class<?>> COMMON_CLASSES_SET = new HashSet<Class<?>>(Arrays.asList(COMMON_ARRAY_CLASSES));

    protected LRUCache<Object, JAXBContext> cache;
    protected LRUCache<JAXBContext, Unmarshaller> upool;
    protected LRUCache<JAXBContext, Marshaller> mpool;
    protected JAXBContext commonContext;

    public JAXBContextCache() {
        this(CACHE_SIZE, CACHE_SIZE, CACHE_SIZE);
    }

    public JAXBContextCache(int contextSize, int marshallerSize, int unmarshallerSize) {
        cache = new LRUCache<Object, JAXBContext>(contextSize);
        upool = new LRUCache<JAXBContext, Unmarshaller>(unmarshallerSize);
        mpool = new LRUCache<JAXBContext, Marshaller>(marshallerSize);
        commonContext = getCommonJAXBContext();
    }

    public static JAXBContext getCommonJAXBContext() {
        try {
            return JAXBContext.newInstance(COMMON_CLASSES_SET.toArray(new Class<?>[COMMON_CLASSES_SET.size()]));
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
                return cls.isAnnotationPresent(XmlSchema.class);
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

    public LRUCache<Object, JAXBContext> getCache() {
        return cache;
    }

    public JAXBContext getJAXBContext(Class<?> cls) throws JAXBException {
        if (COMMON_CLASSES_SET.contains(cls) || BUILTIN_CLASSES_SET.contains(cls)) {
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
        } 
        
        // Is the common one
        if (COMMON_CLASSES_SET.containsAll(classSet)) {
            return commonContext;
        }
        
        // For single class
        if (classSet.size() == 1) {
            return getJAXBContext(classSet.iterator().next());
        }
        synchronized (cache) {
            JAXBContext context = cache.get(classSet);
            if (context != null) {
                return context;
            }
            context = JAXBContext.newInstance(classSet.toArray(new Class<?>[classSet.size()]));
            cache.put(classSet, context);
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

}
