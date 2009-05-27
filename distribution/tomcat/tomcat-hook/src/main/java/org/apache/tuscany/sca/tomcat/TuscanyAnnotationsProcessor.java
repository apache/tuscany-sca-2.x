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

package org.apache.tuscany.sca.tomcat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.AnnotationProcessor;
import org.apache.catalina.util.DefaultAnnotationProcessor;

public class TuscanyAnnotationsProcessor implements AnnotationProcessor {

    private TuscanyStandardContext tuscanyStandardContext;
    private AnnotationProcessor tomcatAnnotationProcessor;
    private Method tuscanyInjectMethod;
    
    public TuscanyAnnotationsProcessor(TuscanyStandardContext tuscanyStandardContext, javax.naming.Context context) {
        this.tuscanyStandardContext = tuscanyStandardContext;
        this.tomcatAnnotationProcessor = new DefaultAnnotationProcessor(context);
        initInjectMethod(tuscanyStandardContext);
    }

    private void initInjectMethod(TuscanyStandardContext tuscanyStandardContext) {
        
        // this needs to use reflection as the tuscany-hook module can't have any
        // dependencies on the tuscany runtime modules as they're not 
        // in the server classpath 
        // TODO: is there a nicer way ?
        
        ClassLoader cl = tuscanyStandardContext.getParentClassLoader();
        try {
            Class<?> c = Class.forName("org.apache.tuscany.sca.implementation.web.runtime.utils.ContextHelper", true, cl);
            if (c != null) {
                this.tuscanyInjectMethod = c.getMethod("inject", new Class[]{Object.class, ServletContext.class});
            }
        } catch (Exception e){
            // ignore
        }
    }
    
    public void postConstruct(Object instance) throws IllegalAccessException, InvocationTargetException {
        tomcatAnnotationProcessor.postConstruct(instance);
    }

    public void preDestroy(Object instance) throws IllegalAccessException, InvocationTargetException {
        tomcatAnnotationProcessor.preDestroy(instance);
    }

    public void processAnnotations(Object instance) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (tuscanyInjectMethod != null) {
            ServletContext sc = tuscanyStandardContext.getServletContext();
            if (sc != null) {
                Object rc = sc.getAttribute("org.apache.tuscany.sca.implementation.web.RuntimeComponent");
                if (rc != null) {
                    try {
                        tuscanyInjectMethod.invoke(null, instance, sc);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    }
                }
            }
        }
        tomcatAnnotationProcessor.processAnnotations(instance);
    }
}
