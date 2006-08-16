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
package org.apache.tuscany.container.spring.impl;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import org.apache.tuscany.container.spring.config.SCAService;
import org.apache.tuscany.container.spring.config.ScaServiceBeanDefinitionParser;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Loads a component type for a Spring <code>ApplicationContext</code>. The implementation creates a new instance of a
 * Spring application context which is configured with SCA namespace handlers for generating component type information
 *
 * @version $$Rev$$ $$Date$$
 */

public class SpringComponentTypeLoader extends ComponentTypeLoaderExtension<SpringImplementation> {

    @Constructor
    public SpringComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    @Override
    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    /**
     * Responsible for loading the Spring composite component type. The the application context is instantiated here as
     * it is needed to derive component type information. Since the component type is loaded per SCDL entry (i.e.
     * composite use) one application context instance will be created per Spring composite instance.
     */
    @SuppressWarnings("unchecked")
    public void load(CompositeComponent<?> parent,
                     SpringImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {
        if (implementation.getComponentType() != null) {
            // FIXME hack
            return;
        }
        Resource resource = implementation.getApplicationResource();
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        GenericApplicationContext ctx = new GenericApplicationContext(beanFactory);
        ctx.refresh();
        SpringComponentType componentType = new SpringComponentType(ctx);

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
                componentType.addServiceType(serviceName, serviceInterface);
                //ServiceDefinition service = createService(serviceInterface);
                //componentType.getServices().put(serviceName, service);
            } catch (ClassNotFoundException e) {
                throw new LoaderException(e);
            }
        }
        // if no service tags are specified, expose all beans
        componentType.setExposeAllBeans(componentType.getServiceTypes().isEmpty());
        implementation.setComponentType(componentType);
    }
}
