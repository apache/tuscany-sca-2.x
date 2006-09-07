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
package org.apache.tuscany.host.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @version $Rev$ $Date$
 */
public final class LaunchHelper {
    private LaunchHelper() {
    }

    /**
     * Scan a directory for jar files to be added to the classpath.
     *
     * @param lib the directory to scan
     * @return the URLs or jar files in that directory
     */
    public static URL[] scanDirectoryForJars(File lib) {
        File[] jars = lib.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            try {
                urls[i] = jars[i].toURI().toURL();
            } catch (MalformedURLException e) {
                // toURI should have escaped the URL
                throw new AssertionError();
            }
        }
        return urls;
    }

    /**
     * Set a JavaBean property on an object.
     *
     * @param instance the object whose property should be set
     * @param name the name of the property
     * @param value the value to set it to
     */
    public static void setProperty(Object instance, String name, Object value) {
        Class<?> beanType = instance.getClass();
        name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Class<?> propertyType = value.getClass();
        try {
            Method setter = beanType.getMethod(name, propertyType);
            setter.invoke(instance, value);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    /**
     * Invoke a method on an object.
     *
     * @param instance the object to invoke
     * @param name the name of the method to invoke
     * @param args arguments to call the method with
     * @return the value returned by the method
     * @throws InvocationTargetException if the method throw an Exception
     */
    public static Object invoke(Object instance, String name, Class<?>[] paramTypes, Object... args)
        throws InvocationTargetException {
        try {
            Method method = instance.getClass().getMethod(name, paramTypes);
            return method.invoke(instance, (Object[]) args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
