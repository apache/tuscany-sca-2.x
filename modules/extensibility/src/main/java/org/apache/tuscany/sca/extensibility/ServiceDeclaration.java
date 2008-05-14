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

package org.apache.tuscany.sca.extensibility;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Map;

/**
 * Service declaration using J2SE Jar service provider spec Classes specified
 * inside this declaration are loaded using the ClassLoader used to read the
 * configuration file corresponding to this declaration.
 *
 * @version $Rev$ $Date$
 */
public class ServiceDeclaration {

    private WeakReference<ClassLoader> classLoader;

    private String className;

    private Map<String, String> attributes;

    /**
     * Service declaration constructor
     * 
     * @param className Service implementation class name
     * @param classLoader ClassLoader corresponding to this service
     *                implementation
     * @param attributes Optional attributes for this service declaration
     */
    public ServiceDeclaration(String className, ClassLoader classLoader, Map<String, String> attributes) {

        this.className = className;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.attributes = attributes;
    }

    /**
     * Load this service implementation class
     * 
     * @return Class
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public Class<?> loadClass() throws ClassNotFoundException {

        return Class.forName(className, true, classLoader.get());
    }

    /**
     * Load another class using the ClassLoader of this service implementation
     * 
     * @param anotherClassName
     * @return Class
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String anotherClassName) throws ClassNotFoundException {

        return Class.forName(anotherClassName, true, classLoader.get());
    }

    /**
     * Return the resource corresponding to this service implementation class
     * 
     * @return resource URL
     */
    public URL getResource() {
        return classLoader.get().getResource(className);
    }

    /**
     * ClassLoader associated with this service declaration
     * 
     * @return ClassLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader.get();
    }

    /**
     * Service implementation class corresponding to this declaration
     * 
     * @return The Service implementation class corresponding to this declaration 
     */
    public String getClassName() {
        return className;
    }

    /**
     * Attributes specified for this declaration
     * 
     * @return attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Equals method used to ensure that each service declaration is stored only
     * once in a set of declarations.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServiceDeclaration))
            return false;
        ServiceDeclaration s = (ServiceDeclaration)o;
        if (!className.equals(s.className))
            return false;
        else if (!classLoader.equals(s.classLoader))
            return false;
        else if (attributes == null)
            return s.attributes == null;
        else
            return attributes.equals(s.attributes);

    }

}
