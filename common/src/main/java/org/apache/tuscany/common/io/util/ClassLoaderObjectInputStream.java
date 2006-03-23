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
package org.apache.tuscany.common.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 *         <p/>
 *         An implementation of an ObjectInputStream that takes a ClassLoader or works
 *         with the current Thread context ClassLoader.
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {
    protected ClassLoader classLoader;

    /**
     * Constructor
     *
     * @param in
     * @param classLoader
     * @throws IOException
     */
    public ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    /**
     * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
     */
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {

        final String name = desc.getName();
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                public Class<?> run() throws ClassNotFoundException, IOException {
                    try {
                        return Class.forName(name, false, classLoader);
                    } catch (ClassNotFoundException e) {
                        return ClassLoaderObjectInputStream.super.resolveClass(desc);
                    }
                }
            });
        } catch (PrivilegedActionException ex) {
            Exception e = ex.getException();
            if (e instanceof ClassNotFoundException) {
                throw (ClassNotFoundException) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            }
            return null;
        }
    }
}