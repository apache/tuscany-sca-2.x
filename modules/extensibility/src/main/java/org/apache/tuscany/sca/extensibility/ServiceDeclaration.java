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

import java.net.URL;
import java.util.Map;

/**
 * Service declaration using J2SE Jar service provider spec Classes specified
 * inside this declaration are loaded using the ClassLoader used to read the
 * configuration file corresponding to this declaration.
 *
 * @version $Rev$ $Date$
 */
public interface ServiceDeclaration {
    /**
     * Load a java class in the same context as the service definition
     * @param className The class name
     * @return The loaded class
     * @throws ClassNotFoundException
     */
    Class<?> loadClass(String className) throws ClassNotFoundException;
    /**
     * Get the java class for the service impl
     * @return The java class
     */
    Class<?> loadClass() throws ClassNotFoundException;
    /**
     * Get all attributes (name=value pairs) defined for the given entry
     * @return All attributes keyed by name
     */
    Map<String, String> getAttributes();
    
    URL getLocation();
    
    String getClassName();
    
    URL getResource(String name);
    
    /**
     * The service descriptor might be hashed
     * @param obj Another object
     * @return
     */
    boolean equals(Object obj);
    /**
     * The service descriptor might be hashed
     * @return
     */
    int hashCode();
}
