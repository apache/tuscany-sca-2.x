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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * Default implementation of the ResourceLoader interface.
 *
 * @version $Rev: 369102 $ $Date: 2006-01-14 13:48:56 -0800 (Sat, 14 Jan 2006) $
 */
@SuppressWarnings({"ClassLoader2Instantiation"})
public class ResourceLoaderImpl implements ResourceLoader {
    private final WeakReference<ClassLoader> classLoaderReference;
    private WeakReference<GeneratedClassLoader> generatedClassLoaderReference;
    private final List<ResourceLoader> parents;

    /**
     * Constructs a new ResourceLoaderImpl to wrap a ClassLoader.
     *
     * @param classLoader the classloader to wrap
     */
    public ResourceLoaderImpl(ClassLoader classLoader) {
        classLoaderReference = new WeakReference<ClassLoader>(classLoader);
        generatedClassLoaderReference = new WeakReference<GeneratedClassLoader>(new GeneratedClassLoader(classLoader));
        ClassLoader parentCL = classLoader.getParent();
        if (null == parentCL) {
            parents = Collections.emptyList();
        } else {
            parents = Collections.singletonList((ResourceLoader) new ResourceLoaderImpl(parentCL));
        }
    }


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

    public Class<?> loadClass(String name) throws ClassNotFoundException {
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
            cl = new GeneratedClassLoader(getClassLoader());
            generatedClassLoaderReference = new WeakReference<GeneratedClassLoader>(cl);
        }
        return cl.addClass(bytes);
    }

    public Iterator<URL> getResources(String name) throws IOException {
        return new EnumerationIterator<URL>(getClassLoader().getResources(name));
    }

    public URL getResource(String name) {
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
            // the try/catch is needed here to get IDEA to shut up
            // there should be no performance overhead here except when the Exception is thrown
            // so I am going to leave it in - feel free to remove if there is any issue
            try {
                return e.nextElement();
            } catch (NoSuchElementException e1) {
                throw e1;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
