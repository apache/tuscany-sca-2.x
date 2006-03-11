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
package org.apache.tuscany.tomcat.integration;

import java.util.Map;
import java.net.URL;

import org.apache.catalina.loader.WebappClassLoader;

/**
 * A version of Tomcat's application classloader that only allows certain classes to be loaded.
 * This is used in the integration tests to make sure that no Tuscany classes are exposed to the
 * application except the ones needed to run the tests.
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"CustomClassloader"})
public class TestClassLoader extends WebappClassLoader {
    private final Map<String, Class<?>> classes;

    public TestClassLoader(Map<String, Class<?>> classes, URL url, ClassLoader parent) {
        super(parent);
        this.classes = classes;
        addURL(url);
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = classes.get(name);
        if (clazz != null) {
            return clazz;
        }
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("org.osoa.")) {
            return super.findClass(name);
        }
        throw new ClassNotFoundException(name);
    }
}
