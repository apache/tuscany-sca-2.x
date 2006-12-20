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
package org.apache.tuscany.runtime.webapp;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @version $Rev$ $Date$
 */
public interface WebappUtil {

    String getApplicationName();

    WebappRuntime getRuntime(ClassLoader bootClassLoader) throws TuscanyInitException;

    /**
     * Return the classloader that should be used to boot the Tuscany runtime. This will be a child of the web
     * application's ClassLoader.
     *
     * @param webappClassLoader the web application's classloader
     * @return a classloader that can be used to load the Tuscany runtime classes
     */
    ClassLoader getBootClassLoader(ClassLoader webappClassLoader) throws InvalidResourcePath;

    URL getSystemScdl(ClassLoader bootClassLoader) throws InvalidResourcePath;

    URL getApplicationScdl(ClassLoader bootClassLoader) throws InvalidResourcePath;

    URL getScdlURL(String path, ClassLoader classLoader) throws MalformedURLException;

    /**
     * Return a init parameter from the servlet context or provide a default.
     *
     * @param name  the name of the parameter
     * @param value the default value
     * @return the value of the specified parameter, or the default if not defined
     */
    String getInitParameter(String name, String value);
}
