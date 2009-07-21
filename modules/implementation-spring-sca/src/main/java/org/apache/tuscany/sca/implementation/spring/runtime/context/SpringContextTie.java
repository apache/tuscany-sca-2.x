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

package org.apache.tuscany.sca.implementation.spring.runtime.context;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.implementation.spring.processor.ComponentNameAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ComponentStub;
import org.apache.tuscany.sca.implementation.spring.processor.ConstructorAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.InitDestroyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.PropertyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.PropertyValueStub;
import org.apache.tuscany.sca.implementation.spring.processor.ReferenceAnnotationProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.UrlResource;

/**
 * This is the runtime side tie for the corresponding tuscany side stub class.
 * It enables the Tuscany code to invoke methods on a Spring context without
 * needing to know about any Spring classes. See the SpringContextStub class
 * in the implementation-spring module for what the stub does. 
 */
public class SpringContextTie {

    private AbstractApplicationContext springContext;
    private SpringImplementationStub implementation;
    
    public SpringContextTie(SpringImplementationStub implementation, URL resource) {
        this.implementation = implementation;
        SCAParentApplicationContext scaParentContext = new SCAParentApplicationContext(implementation);
        springContext = createApplicationContext(scaParentContext, resource);  
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
//    private Object createApplicationContext(SCAParentApplicationContext scaParentContext) {

    private AbstractApplicationContext createApplicationContext(SCAParentApplicationContext scaParentContext, URL resource) {

        XmlBeanFactory beanFactory = new XmlBeanFactory(new UrlResource(resource));
        AbstractApplicationContext appContext = null;
        
        for (String bean : beanFactory.getBeanDefinitionNames()) {
                String beanClassName = (beanFactory.getType(bean)).getName();
                if (beanClassName.indexOf(".ClassPathXmlApplicationContext") != -1 || 
                                beanClassName.indexOf(".FileSystemXmlApplicationContext") != -1) 
                {
                        BeanDefinition beanDef = beanFactory.getBeanDefinition(bean);                           
                        String[] listValues = null;
                        List<ConstructorArgumentValues.ValueHolder> conArgs = 
                                beanDef.getConstructorArgumentValues().getGenericArgumentValues();
                        for (ConstructorArgumentValues.ValueHolder conArg : conArgs) {
                                if (conArg.getValue() instanceof TypedStringValue) {
                                        TypedStringValue value = (TypedStringValue) conArg.getValue();
                                        if (value.getValue().indexOf(".xml") != -1)
                                                listValues = new String[]{value.getValue()};
                                }
                                if (conArg.getValue() instanceof ManagedList) {
                                        Iterator itml = ((ManagedList)conArg.getValue()).iterator();
                                        StringBuffer values = new StringBuffer();
                                        while (itml.hasNext()) {
                                                TypedStringValue next = (TypedStringValue)itml.next();
                                                if (next.getValue().indexOf(".xml") != -1) {
                                                        values.append(next.getValue());
                                                        values.append("~");
                                                }
                                        }
                                        listValues = (values.toString()).split("~");                                    
                                }
                        }
                        
                        if (beanClassName.indexOf(".ClassPathXmlApplicationContext") != -1) {                                                                   
                                appContext = new ClassPathXmlApplicationContext(listValues, false, scaParentContext);                                   
                                appContext.refresh(); // TODO why is this needed here now?
                                includeAnnotationProcessors(appContext.getBeanFactory());
                                return appContext;
                        } else {
                                appContext = new FileSystemXmlApplicationContext(listValues, false, scaParentContext);                                  
                                appContext.refresh(); // TODO why is this needed here now?
                                includeAnnotationProcessors(appContext.getBeanFactory());
                                return appContext;
                        }
                }               
        }
        
        // use the generic application context as default 
        includeAnnotationProcessors(beanFactory);
        appContext = new GenericApplicationContext(beanFactory, scaParentContext);
        return appContext;
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
        ComponentStub component = new ComponentStub(implementation.getComponentTie());
        BeanPostProcessor referenceProcessor = new ReferenceAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(referenceProcessor);
        
        // Processor to deal with @Property SCA Annotations
        PropertyValueStub pvs = new PropertyValueStub(implementation.getPropertyValueTie());
        BeanPostProcessor propertyProcessor = new PropertyAnnotationProcessor(pvs);
        beanFactory.addBeanPostProcessor(propertyProcessor);
        
        // Processor to deal with @ComponentName SCA Annotations
        BeanPostProcessor componentNameProcessor = new ComponentNameAnnotationProcessor(implementation.getComponentName());
        beanFactory.addBeanPostProcessor(componentNameProcessor);
        
        // Processor to deal with @Constructor SCA Annotations
        BeanPostProcessor constructorProcessor = new ConstructorAnnotationProcessor();
        beanFactory.addBeanPostProcessor(constructorProcessor);         
    }

}
