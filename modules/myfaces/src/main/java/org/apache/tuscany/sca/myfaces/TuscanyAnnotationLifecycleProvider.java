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
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.myfaces.config.annotation.DiscoverableLifecycleProvider;
import org.apache.myfaces.shared_impl.util.ClassUtils;

public class TuscanyAnnotationLifecycleProvider implements DiscoverableLifecycleProvider {
    private static final Logger logger = Logger.getLogger(TuscanyAnnotationLifecycleProvider.class.getName());

    private TuscanyAnnotationProcessor annotationProcessor;
    private ServletContext servletContext;


    public TuscanyAnnotationLifecycleProvider(ExternalContext externalContext) {
        this.servletContext = (ServletContext)externalContext.getContext();
        
        // TODO: Should this use any existing AnnotationProcessor?
        //      Tomcat based runtimes may use an org.apache.AnnotationProcessor which
        //      if exists this could delegate to so as to support both SCA and other annotations
        // AnnotationProcessor existingAnnotationProcessor = (AnnotationProcessor)
        // servletContext.getAttribute(org.apache.AnnotationProcessor.class.getName());
        // annotationProcessor = new TuscanyAnnotationProcessor(existingAnnotationProcessor);
        annotationProcessor = new TuscanyAnnotationProcessor();
    }

    public Object newInstance(String className) throws InstantiationException, IllegalAccessException, InvocationTargetException, NamingException, ClassNotFoundException {
        logger.info("Creating instance of " + className);
        Class<?> clazz = ClassUtils.classForName(className);
        Object object = clazz.newInstance();
        annotationProcessor.processAnnotations(object, servletContext);
        annotationProcessor.postConstruct(object);
        return object;
    }

    public boolean isAvailable() {
        return true;
    }

    public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
        logger.info("Destroy instance of " + o.getClass().getName());
        annotationProcessor.preDestroy(o);
    }

}
