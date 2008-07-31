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

package org.apache.tuscany.sca.runtime.tomcat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NamingException;

import org.apache.AnnotationProcessor;
import org.osoa.sca.annotations.Reference;

/**
 * A Tuscany specific Tomcat annotation processor for processing SCA annotations
 */
public class TuscanyAnnotationProcessor implements AnnotationProcessor {

    public void postConstruct(Object instance) throws IllegalAccessException, InvocationTargetException {
    }

    public void preDestroy(Object instance) throws IllegalAccessException, InvocationTargetException {
    }

    public void processAnnotations(Object instance) throws IllegalAccessException, InvocationTargetException, NamingException {
        
        // Initialize fields annotations
        Field[] fields = instance.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(Reference.class)) {
                Reference annotation = fields[i].getAnnotation(Reference.class);
                injectFieldResource(instance, fields[i], annotation);
            }
        }
        
        // Initialize methods annotations
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Reference.class)) {
                Reference annotation = methods[i].getAnnotation(Reference.class);
                injectMethodResource(instance, methods[i], annotation);
            }
        }

    }

    protected void injectFieldResource(Object instance, Field field, Reference annotation) {
        System.out.println("TuscanyAnnotationProcessor.injectFieldResource" + annotation);
    }

    protected void injectMethodResource(Object instance, Method method, Reference annotation) {
        System.out.println("TuscanyAnnotationProcessor.injectMethodResource" + annotation);
    }

}
