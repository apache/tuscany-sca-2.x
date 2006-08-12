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
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

import org.apache.tuscany.container.spring.config.SCABeanDefinitionReader;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.model.CompositeComponentType;

/**
 * Loads a component type for a Spring <code>ApplicationContext</code>. The implementation creates a new
 * instance of a Spring application context which is configured with SCA namespace handlers for generating
 * component type information
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringComponentTypeLoader implements ComponentTypeLoader<SpringImplementation> {

    /* Major work in progress here */
    public void load(CompositeComponent<?> parent, SpringImplementation implementation,
                     DeploymentContext deploymentContext) {
        Resource resource = null; //FIXME
        CompositeComponentType componentType = new CompositeComponentType();
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new SCABeanDefinitionReader(beanFactory, componentType);
        reader.loadBeanDefinitions(resource);
        GenericApplicationContext ctx = new GenericApplicationContext(beanFactory);
        ctx.refresh();
        implementation.setComponentType(componentType);
    }
}
