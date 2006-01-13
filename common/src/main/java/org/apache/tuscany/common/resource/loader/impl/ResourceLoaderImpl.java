/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.resource.loader.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * Default implementation of the ResourceLoader interface.
 */
public class ResourceLoaderImpl implements ResourceLoader {

    private final WeakReference classLoaderReference;
    private List parents;
    private Map resources;

    /**
     * Constructor
     * @param classLoader
     */
    protected ResourceLoaderImpl(ClassLoader classLoader) {
        classLoaderReference = new WeakReference(classLoader);
    }

    /**
     * @see org.apache.tuscany.common.resource.loader.ResourceLoader#getParents()
     */
    public List getParents() {
        if (parents == null) {
            ClassLoader parentClassLoader = ((ClassLoader) classLoaderReference.get()).getParent();
            parents = Collections.singletonList(new ResourceLoaderImpl(parentClassLoader));
        }
        return parents;
    }

    /**
     * @see org.apache.tuscany.common.resource.loader.ResourceLoader#loadClass(java.lang.String)
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, (ClassLoader) classLoaderReference.get());
    }

    /**
     * @see org.apache.tuscany.common.resource.loader.ResourceLoader#getResources(java.lang.String)
     */
    public Iterator getResources(String name) throws IOException {
        if (resources == null)
            resources = new HashMap();

        // Get the cached set of resources
        Set set = (Set) resources.get(name);
        if (set != null) {
            return set.iterator();
        }

        // Create a new set, add all the resources visible from the current ClassLoader
        set = new HashSet();
        ClassLoader classLoader = (ClassLoader) classLoaderReference.get();
        for (Enumeration e = classLoader.getResources(name); e.hasMoreElements();) {
            URL resource = (URL) e.nextElement();
            set.add(resource);
        }

        // Remove the resources visible from the parent ClassLoaders
        for (Iterator p = getParents().iterator(); p.hasNext();) {
            ResourceLoaderImpl parent = (ResourceLoaderImpl) p.next();
            for (Iterator i = parent.getAllResources(name); i.hasNext();) {
                set.remove(i.next());
            }
        }

        // Cache the resulting set
        resources.put(name, set);

        return set.iterator();
    }

    /**
     * @see org.apache.tuscany.common.resource.loader.ResourceLoader#getAllResources(java.lang.String)
     */
    public Iterator getAllResources(String name) throws IOException {
        ClassLoader classLoader = (ClassLoader) classLoaderReference.get();
        Enumeration e = classLoader.getResources(name);
        return Collections.list(e).iterator();
    }

    /**
     * @see org.apache.tuscany.common.resource.loader.ResourceLoader#getResource(java.lang.String)
     */
    public URL getResource(String name) throws IOException {
        Iterator resources = getAllResources(name);
        if (resources.hasNext())
            return (URL) resources.next();
        else
            return null;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResourceLoaderImpl)) {
            return false;
        }
        final ResourceLoaderImpl other = (ResourceLoaderImpl) obj;
        return classLoaderReference.get() == other.classLoaderReference.get();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return classLoaderReference.get().hashCode();
    }
}
