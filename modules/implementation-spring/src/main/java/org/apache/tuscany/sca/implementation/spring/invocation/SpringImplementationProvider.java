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
package org.apache.tuscany.sca.implementation.spring.invocation;

import java.util.List;
import java.util.Iterator;

import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.implementation.spring.SpringImplementation;
import org.apache.tuscany.sca.implementation.spring.processor.InitDestroyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ReferenceAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.PropertyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ConstructorAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ComponentNameAnnotationProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor; 
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.UrlResource;

/**
 * A provider class for runtime Spring implementation instances
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $ 
 */
public class SpringImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    
    // A Spring application context object
    private AbstractApplicationContext springContext;
    
    private SpringImplementation implementation;
    
    private JavaPropertyValueObjectFactory propertyValueObjectFactory;

    /**
     * Constructor for the provider - takes a component definition and a Spring implementation
     * description
     * @param component - the component in the assembly
     * @param implementation - the implementation
     */
    public SpringImplementationProvider(RuntimeComponent component,
                                        SpringImplementation implementation,
                                        ProxyFactory proxyService,
                                        JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.implementation = implementation;
        this.component = component;
        this.propertyValueObjectFactory = propertyValueObjectFactory;
        
        SCAParentApplicationContext scaParentContext =
            new SCAParentApplicationContext(component, implementation, proxyService, propertyValueObjectFactory);
        //springContext = new SCAApplicationContext(scaParentContext, implementation.getResource());        
        
        XmlBeanFactory beanFactory = new XmlBeanFactory(new UrlResource(implementation.getResource()));
        springContext = createApplicationContext(beanFactory, scaParentContext);        
        
    } // end constructor

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return new SpringInvoker(component, springContext, service, operation);
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    /**
     * Start this Spring implementation instance
     */
    public void start() {
        // Do refresh here to ensure that Spring Beans are not touched before the SCA config process 
        // is complete...
        springContext.refresh();
        springContext.start();
        // System.out.println("SpringImplementationProvider: Spring context started");
    } // end method start()

    /**
     * Stop this implementation instance
     */
    public void stop() {
        // TODO - complete
    	springContext.close();
    	if (springContext instanceof GenericApplicationContext)
    		springContext.stop();
        //System.out.println("SpringImplementationProvider: Spring context stopped");
    } // end method stop
    
    
    /**
     * Include BeanPostProcessor to deal with SCA Annotations in Spring Bean
     */
    private void includeAnnotationProcessors(ConfigurableListableBeanFactory beanFactory) {
    	
    	// Processor to deal with @Init and @Destroy SCA Annotations
    	BeanPostProcessor initDestroyProcessor = new InitDestroyAnnotationProcessor();
        beanFactory.addBeanPostProcessor(initDestroyProcessor);
        
        // Processor to deal with @Reference SCA Annotations
        BeanPostProcessor referenceProcessor = new ReferenceAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(referenceProcessor);
        
        // Processor to deal with @Property SCA Annotations
        BeanPostProcessor propertyProcessor = new PropertyAnnotationProcessor(propertyValueObjectFactory, component);
        beanFactory.addBeanPostProcessor(propertyProcessor);
        
        // Processor to deal with @ComponentName SCA Annotations
        BeanPostProcessor componentNameProcessor = new ComponentNameAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(componentNameProcessor);
        
        // Processor to deal with @Constructor SCA Annotations
        BeanPostProcessor constructorProcessor = new ConstructorAnnotationProcessor();
        beanFactory.addBeanPostProcessor(constructorProcessor);    	
    }
    
    
    /**
     * Include BeanPostProcessor to deal with SCA Annotations in Spring Bean
     */
    private AbstractApplicationContext createApplicationContext(XmlBeanFactory beanFactory,
    															SCAParentApplicationContext scaParentContext) {
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
    				//includeAnnotationProcessors(appContext.getBeanFactory());
					return appContext;
    			} else {
    				appContext = new FileSystemXmlApplicationContext(listValues, false, scaParentContext);    				
    				//includeAnnotationProcessors(appContext.getBeanFactory());
					return appContext;
    			}
    		}    		
    	}
    	
    	// use the generic application context as default 
        includeAnnotationProcessors(beanFactory);
        appContext = new GenericApplicationContext(beanFactory, scaParentContext);
        return appContext;
    }
    
} // end class SpringImplementationProvider
