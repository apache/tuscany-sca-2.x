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

package org.apache.tuscany.sca.implementation.spring;

import java.util.List;

import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.apache.tuscany.sca.implementation.spring.processor.InitDestroyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ReferenceAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.PropertyAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ConstructorAnnotationProcessor;
import org.apache.tuscany.sca.implementation.spring.processor.ComponentNameAnnotationProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor; 

// TODO - create a working version of this class...
/**
 * A provider class for runtime Spring implementation instances
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $ 
 */
public class SpringImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    
    // A Spring application context object
    private AbstractApplicationContext springContext;
    
    private SpringImplementation implementation;

    /**
     * Constructor for the provider - takes a component definition and a Spring implementation
     * description
     * @param component - the component in the assembly
     * @param implementation - the implementation
     */
    public SpringImplementationProvider(RuntimeComponent component,
                                        SpringImplementation implementation,
                                        ProxyFactory proxyService,
                                        JavaPropertyValueObjectFactory propertyValueObjectFactory,
                                        List<PolicyHandlerTuple> policyHandlerClassNames) {
        super();
        this.implementation = implementation;
        this.component = component;
        this.implementation.setPolicyHandlerClassNames(policyHandlerClassNames);
        
        SCAParentApplicationContext scaParentContext =
            new SCAParentApplicationContext(component, implementation, proxyService, propertyValueObjectFactory);
        //springContext = new SCAApplicationContext(scaParentContext, implementation.getResource());
        
        XmlBeanFactory beanFactory = new XmlBeanFactory(implementation.getResource());
        BeanPostProcessor initDestroyProcessor = new InitDestroyAnnotationProcessor();
        beanFactory.addBeanPostProcessor(initDestroyProcessor);
        BeanPostProcessor referenceProcessor = new ReferenceAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(referenceProcessor);
        BeanPostProcessor propertyProcessor = new PropertyAnnotationProcessor(propertyValueObjectFactory, component);
        beanFactory.addBeanPostProcessor(propertyProcessor);
        BeanPostProcessor componentNameProcessor = new ComponentNameAnnotationProcessor(component);
        beanFactory.addBeanPostProcessor(componentNameProcessor);
        BeanPostProcessor constructorProcessor = new ConstructorAnnotationProcessor();
        beanFactory.addBeanPostProcessor(constructorProcessor);
        springContext = new GenericApplicationContext(beanFactory, scaParentContext);
        
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
        springContext.stop();
        //System.out.println("SpringImplementationProvider: Spring context stopped");
    } // end method stop

} // end class SpringImplementationProvider
