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
package org.apache.tuscany.spi.deployer;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ClassLoader associated with a composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeClassLoader extends URLClassLoader  {
    private static final URL[] NOURLS = {};
    private final List<ClassLoader> parents = new CopyOnWriteArrayList<ClassLoader>();

    public CompositeClassLoader(ClassLoader parent) {
        super(NOURLS);
        parents.add(parent);
    }

    public CompositeClassLoader(URL[] urls, ClassLoader parent) {
        super(urls);
        parents.add(parent);
    }

    public CompositeClassLoader(URL[] urls) {
        super(urls);
    }

    public CompositeClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory urlStreamHandlerFactory) {
        super(urls, null, urlStreamHandlerFactory);
        parents.add(parent);
    }

    public void addURL(URL url) {
        // Require RuntimePermission("createClassLoader")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkCreateClassLoader();
        }
        super.addURL(url);
    }

    public void addParent(ClassLoader parent) {
        // Require RuntimePermission("createClassLoader")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkCreateClassLoader();
        }
        parents.add(parent);
    }


    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // look for already loaded classes
        Class clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }

        // look in our parents
        for (ClassLoader parent : parents) {
            try {
                clazz = parent.loadClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        // look in our classpath
        clazz = findClass(name);
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }


    protected Class<?> findClass(String string) throws ClassNotFoundException {
        return super.findClass(string);
    }

    public URL findResource(String name) {
        // look in our parents
        for (ClassLoader parent : parents) {
            URL resource = parent.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        // look in our classpath
        return super.findResource(name);
    }

    public Enumeration<URL> findResources(String name) throws IOException {
        // LinkedHashSet because we want all resources in the order found but no duplicates
        Set<URL> resources = new LinkedHashSet<URL>();
        for (ClassLoader parent : parents) {
            Enumeration<URL> parentResources = parent.getResources(name);
            while (parentResources.hasMoreElements()) {
                resources.add(parentResources.nextElement());
            }
        }
        Enumeration<URL> myResources = super.findResources(name);
        while (myResources.hasMoreElements()) {
            resources.add(myResources.nextElement());
        }
        return Collections.enumeration(resources);
    }
}
