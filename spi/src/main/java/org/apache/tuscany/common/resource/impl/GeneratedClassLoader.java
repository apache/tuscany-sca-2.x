/**
 *
 * Copyright 2006 The Apache Software Foundation
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

/**
 * A class loader that allows new classes to be defined from an array of bytes.
 *
 * @version $Rev: 369102 $ $Date: 2006-01-14 13:48:56 -0800 (Sat, 14 Jan 2006) $
 */
@SuppressWarnings({"CustomClassloader"})
class GeneratedClassLoader extends ClassLoader {

    /**
     * Constructs a new GeneratedClassLoader.
     * @param classLoader the parent classloader
     */
    GeneratedClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * Converts an array of bytes into a Class.
     *
     * @param bytes the bytecode for the class; must match the class file format
     * @return a Class defined from the supplied bytecode
     */
    Class<?> addClass(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length);
    }

}
