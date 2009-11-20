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

package org.apache.tuscany.sca.myfaces;

import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.tuscany.sca.implementation.web.runtime.utils.ContextHelper;

//public class TuscanyAnnotationProcessor implements org.apache.AnnotationProcessor {
public class TuscanyAnnotationProcessor {

//    private AnnotationProcessor annotationProcessor;

    public TuscanyAnnotationProcessor() {
    }

    public void postConstruct(Object arg0) throws IllegalAccessException, InvocationTargetException {
//        annotationProcessor.postConstruct(arg0);
    }

    public void preDestroy(Object arg0) throws IllegalAccessException, InvocationTargetException {
//        annotationProcessor.preDestroy(arg0);
    }

    public void processAnnotations(Object arg0, ServletContext servletContext) throws IllegalAccessException, InvocationTargetException, NamingException {
        ContextHelper.inject(arg0, servletContext);
//        annotationProcessor.processAnnotations(arg0);
    }

    public void processAnnotations(Object arg0) throws IllegalAccessException, InvocationTargetException, NamingException {
//        annotationProcessor.processAnnotations(arg0);
    }
}
