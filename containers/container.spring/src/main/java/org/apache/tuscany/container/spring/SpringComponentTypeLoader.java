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
package org.apache.tuscany.container.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.apache.tuscany.container.spring.config.SCAService;
import org.apache.tuscany.container.spring.config.ScaServiceBeanDefinitionParser;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.core.implementation.processor.ProcessorUtils;
import org.apache.tuscany.core.implementation.processor.IllegalCallbackException;
import org.osoa.sca.annotations.Constructor;

import java.net.URL;

/**
 * Loads a component type for a Spring <code>ApplicationContext</code>. The implementation creates a new
 * instance of a Spring application context which is configured with SCA namespace handlers for generating
 * component type information
 *
 * @version $$Rev$$ $$Date$$
 */

public class SpringComponentTypeLoader extends ComponentTypeLoaderExtension<SpringImplementation> {

    @Constructor({"registry"})
    public SpringComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    @Override
    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    /* Major work in progress here */
    public void load(CompositeComponent<?> parent, SpringImplementation implementation,
                     DeploymentContext deploymentContext) {
        URL appXml = implementation.getApplicationXml();

        Resource resource = new UrlResource(appXml);
        CompositeComponentType componentType = new CompositeComponentType();
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        GenericApplicationContext ctx = new GenericApplicationContext(beanFactory);
        ctx.refresh();

        // If there are <sca:service> elements, they define (and limit) the services exposed
        // in the componentType.
        String [] serviceBeanNames = ctx.getBeanNamesForType(SCAService.class);
        for (String serviceBeanName : serviceBeanNames) {
            int nSuffix = serviceBeanName.indexOf(ScaServiceBeanDefinitionParser.SERVICE_BEAN_SUFFIX);
            if (nSuffix == -1) {
                continue;
            }

            String serviceName = serviceBeanName.substring(0, nSuffix);
            SCAService serviceBean = (SCAService) ctx.getBean(serviceName);
            String serviceTypeName = serviceBean.getType();
            try {
                Class serviceInterface = Class.forName(serviceTypeName, true, deploymentContext.getClassLoader());
                ServiceDefinition service = ProcessorUtils.createService(serviceInterface);
                componentType.getServices().put(serviceName, service);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalCallbackException e) {
                e.printStackTrace();
            }
        }

        // If there were no <sca:service> elements, expose all beans as SCA services
        // REVIEW: this needs a lot of refinement; we almost certainly don't want to expose
        // _all_ beans willy nilly.
        if (serviceBeanNames.length == 0) {
            String [] allBeanDefNames = ctx.getBeanDefinitionNames();
            for (String beanDefName : allBeanDefNames) {
                BeanDefinition beanDef = ctx.getBeanDefinition(beanDefName);
                String beanClassName = beanDef.getBeanClassName();
                String beanName = (String) beanDef.getAttribute("name");
                try {
                    Class beanClass = Class.forName(beanClassName, true, deploymentContext.getClassLoader());
                    Class [] beanInterfaces = beanClass.getInterfaces();
                    // hack, just using the 1st impl'ed interface for now
                    if (beanInterfaces.length > 0) {
                        ServiceDefinition service = ProcessorUtils.createService(beanInterfaces[0]);
                        componentType.getServices().put(beanName, service);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalCallbackException e) {
                    e.printStackTrace();
                }
            }
        }

        implementation.setComponentType(componentType);
    }
}
