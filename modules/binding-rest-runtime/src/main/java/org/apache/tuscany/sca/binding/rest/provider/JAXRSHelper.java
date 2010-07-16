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

package org.apache.tuscany.sca.binding.rest.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

/**
 * A utility class that deals with JAX-RS annotations
 */
public class JAXRSHelper {
    private JAXRSHelper() {

    }

    /**
     * A resource class is a Java class that uses JAX-RS annotations to implement a corresponding Web resource.
     * Resource classes are POJOs that have at least one method annotated with @Path or a request method designator.
     * @param cls
     * @return
     */
    public static boolean isJAXRSResource(Class<?> cls) {
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(Path.class)) {
                return true;
            }
            if (isResourceMethod(method)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Root resource class is a resource class annotated with @Path. Root resource classes provide the roots of the
     * resource class tree and provide access to sub-resources
     * @param cls
     * @return
     */
    public static boolean isJAXRSRootResource(Class<?> cls) {
        return cls.isAnnotationPresent(Path.class) && isJAXRSResource(cls);
    }

    public static boolean isResourceMethod(Method method) {
        for (Annotation a : method.getAnnotations()) {
            Class<?> annotationType = a.annotationType();
            if (annotationType == HttpMethod.class) {
                return true;
            }
            // Http method related annotations such as @GET, @POST will have itself annotated with
            // @HttpMethod
            HttpMethod m = a.annotationType().getAnnotation(HttpMethod.class);
            if (m != null) {
                return true;
            }
        }
        return false;

    }
}
