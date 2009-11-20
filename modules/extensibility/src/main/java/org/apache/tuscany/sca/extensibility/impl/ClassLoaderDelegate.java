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

package org.apache.tuscany.sca.extensibility.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A classloader that can delegate to a list of other classloaders
 */
public class ClassLoaderDelegate extends ClassLoader {
    private final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

    /**
     * @param parent The parent classloaders
     * @param loaders A list of classloaders to be used to load classes or resources
     */
    public ClassLoaderDelegate(ClassLoader parent, Collection<ClassLoader> loaders) {
        super(parent);
        if (loaders != null) {
            for (ClassLoader cl : loaders) {
                if (cl != null && cl != parent && !classLoaders.contains(cl)) {
                    this.classLoaders.add(cl);
                }
            }
        }
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        for (ClassLoader delegate : classLoaders) {
            try {
                return delegate.loadClass(className);
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        throw new ClassNotFoundException(className);
    }

    @Override
    protected URL findResource(String resName) {
        for (ClassLoader delegate : classLoaders) {
            URL url = delegate.getResource(resName);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String resName) throws IOException {
        Set<URL> urlSet = new HashSet<URL>();
        for (ClassLoader delegate : classLoaders) {
            Enumeration<URL> urls = delegate.getResources(resName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    urlSet.add(urls.nextElement());
                }
            }
        }
        return Collections.enumeration(urlSet);
    }
}
