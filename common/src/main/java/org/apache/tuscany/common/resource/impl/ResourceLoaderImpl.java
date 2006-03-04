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
package org.apache.tuscany.common.resource.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * Default implementation of the ResourceLoader interface.
 *
 * @version $Rev: 369102 $ $Date: 2006-01-14 13:48:56 -0800 (Sat, 14 Jan 2006) $
 */
public class ResourceLoaderImpl implements ResourceLoader {
    private final WeakReference<ClassLoader> classLoaderReference;
    private WeakReference<GeneratedClassLoader> generatedClassLoaderReference;
    private final List<ResourceLoader> parents;

    /**
     * A class loader that allows new classes to be defined from an array of bytes
     */
    private class GeneratedClassLoader extends ClassLoader {
        
        /**
         * Constructs a new ResourceLoaderImpl.GeneratedClassLoader.
         */
        public GeneratedClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        /**
         * Converts an array of bytes into a Class.
         * @param bytes
         * @return
         */
        private Class<?> addClass(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }
        
    }

    /**
     * Constructs a new ResourceLoaderImpl.
     * @param classLoader
     */
    public ResourceLoaderImpl(ClassLoader classLoader) {
        classLoaderReference = new WeakReference(classLoader);
        generatedClassLoaderReference = new WeakReference(new GeneratedClassLoader(classLoader));
        ClassLoader parentCL = classLoader.getParent();
        parents = parentCL == null ? Collections.EMPTY_LIST : Collections.singletonList(new ResourceLoaderImpl(parentCL));
    }


    /**
     * Return the classloader backing this resource loader.
     *
     * @return the classloader that backs this resource loader
     * @throws IllegalStateException if the classloader has been garbage collected
     */
    //FIXME Temporary used to set the classloader on the thread context, need to changed to private 
    public ClassLoader getClassLoader() throws IllegalStateException {
        ClassLoader cl = classLoaderReference.get();
        if (cl == null) {
            throw new IllegalStateException("Referenced ClassLoader has been garbage collected");
        }
        return cl;
    }

    public List<ResourceLoader> getParents() {
        return parents;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        GeneratedClassLoader cl = generatedClassLoaderReference.get();
        if (cl != null) {
            return Class.forName(name, true, cl);
        } else {
            return Class.forName(name, true, getClassLoader());
        }
    }
    
    public Class<?> addClass(byte[] bytes) {
        GeneratedClassLoader cl = generatedClassLoaderReference.get();
        if (cl == null) {
            cl=new GeneratedClassLoader(getClassLoader());
            generatedClassLoaderReference = new WeakReference(cl);
        }
        return cl.addClass(bytes);
    }

    public Iterator<URL> getResources(String name) throws IOException {
        // This implementation used to cache but users are not likely
        // to ask for the same resource multiple times.

        // Create a new set, add all the resources visible from the current ClassLoader
        Set<URL> set = new HashSet();
        ClassLoader classLoader = getClassLoader();
        for (Enumeration<URL> e = classLoader.getResources(name); e.hasMoreElements();) {
            set.add(e.nextElement());
        }

        // Remove the resources visible from the parent ClassLoaders
        for (ResourceLoader parent : getParents()) {
            for (Iterator<URL> i = parent.getAllResources(name); i.hasNext();) {
                set.remove(i.next());
            }
        }
        return set.iterator();
    }

    public Iterator<URL> getAllResources(String name) throws IOException {
        return new EnumerationIterator(getClassLoader().getResources(name));
    }

    public URL getResource(String name) throws IOException {
        return getClassLoader().getResource(name);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResourceLoaderImpl)) {
            return false;
        }
        final ResourceLoaderImpl other = (ResourceLoaderImpl) obj;
        return getClassLoader() == other.getClassLoader();
    }

    public int hashCode() {
        return getClassLoader().hashCode();
    }

    private static class EnumerationIterator<E> implements Iterator<E> {
        private final Enumeration<E> e;

        public EnumerationIterator(Enumeration<E> e) {
            this.e = e;
        }

        public boolean hasNext() {
            return e.hasMoreElements();
        }

        public E next() {
            return e.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
