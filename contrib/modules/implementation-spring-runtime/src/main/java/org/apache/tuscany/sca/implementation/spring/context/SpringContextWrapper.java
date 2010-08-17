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

package org.apache.tuscany.sca.implementation.spring.context;

import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.implementation.spring.invocation.ComponentWrapper;
import org.apache.tuscany.sca.implementation.spring.invocation.PropertyValueWrapper;
import org.apache.tuscany.sca.implementation.spring.invocation.SpringImplementationWrapper;
import org.apache.tuscany.sca.implementation.spring.processor.ComponentNameAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ConstructorAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.InitDestroyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.PropertyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ReferenceAnnotationProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

/**
 * This is the runtime side tie for the corresponding tuscany side stub class.
 * It enables the Tuscany code to invoke methods on a Spring context without
 * needing to know about any Spring classes. See the SpringContextStub class
 * in the implementation-spring module for what the stub does. 
 */
public class SpringContextWrapper {

    private GenericApplicationContext springContext;
    private SpringImplementationWrapper implementation;

    public SpringContextWrapper(SpringImplementationWrapper implementation, List<URL> resource) {
        this.implementation = implementation;
        SCAParentApplicationContext scaParentContext = new SCAParentApplicationContext(implementation);
        springContext = createApplicationContext(scaParentContext, resource);
    }

    public ApplicationContext getApplicationContext() {
        return springContext;
    }

    public void start() {
        // Do refresh here to ensure that Spring Beans are not touched before the SCA config process is complete...
        springContext.refresh();
        springContext.start();
    }

    public void close() {
        springContext.close();
        if (springContext instanceof GenericApplicationContext) {
            springContext.stop();
        }
    }

    /**
     * Include BeanPostProcessor to deal with SCA Annotations in Spring Bean
     */
    private GenericApplicationContext createApplicationContext(SCAParentApplicationContext scaParentContext,
                                                                List<URL> resources) {

        GenericApplicationContext appCtx =
            new SCAGenericApplicationContext(scaParentContext, implementation.getClassLoader());
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appCtx);

        // REVIEW: [rfeng] How do we control the schema validation 
        xmlReader.setValidating(false);

        for (URL resource : resources) {
            xmlReader.loadBeanDefinitions(new UrlResource(resource));
        }
        xmlReader.setBeanClassLoader(implementation.getClassLoader());
        includeAnnotationProcessors(appCtx.getBeanFactory());
        return appCtx;

    }

    public Object getBean(String id) throws BeansException {
        return springContext.getBean(id);
    }

    /**
     * Include BeanPostProcessor to deal with SCA Annotations in Spring Bean
     */
    private void includeAnnotationProcessors(ConfigurableListableBeanFactory beanFactory) {

        // Processor to deal with @Init and @Destroy SCA Annotations
        BeanPostProcessor initDestroyProcessor = new InitDestroyAnnotationProcessor();
        beanFactory.addBeanPostProcessor(initDestroyProcessor);

        // Processor to deal with @Reference SCA Annotations
        ComponentWrapper component = implementation.getComponentWrapper();
        BeanPostProcessor referenceProcessor = new ReferenceAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(referenceProcessor);

        // Processor to deal with @Property SCA Annotations
        PropertyValueWrapper pvs = implementation.getPropertyValueWrapper();
        BeanPostProcessor propertyProcessor = new PropertyAnnotationProcessor(pvs);
        beanFactory.addBeanPostProcessor(propertyProcessor);

        // Processor to deal with @ComponentName SCA Annotations
        BeanPostProcessor componentNameProcessor =
            new ComponentNameAnnotationProcessor(implementation.getComponentName());
        beanFactory.addBeanPostProcessor(componentNameProcessor);

        // Processor to deal with @Constructor SCA Annotations
        BeanPostProcessor constructorProcessor = new ConstructorAnnotationProcessor();
        beanFactory.addBeanPostProcessor(constructorProcessor);
    }

}
