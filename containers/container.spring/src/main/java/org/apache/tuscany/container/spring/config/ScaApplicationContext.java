/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;

import org.apache.tuscany.container.spring.impl.SpringScaAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.sca.ScaAdapterAware;
import org.springframework.sca.ScaAdapterPostProcessor;

/**
 * @author Andy Piper
 * @since 2.1
 */
public class ScaApplicationContext extends AbstractXmlApplicationContext {
    private Resource appXml;
    private CompositeComponentType componentType;

    public ScaApplicationContext(Resource appXml, CompositeComponentType componentType) {
        this(null, appXml, componentType);
    }

    public ScaApplicationContext(ApplicationContext parent, Resource appXml, CompositeComponentType componentType) {
        super(parent);
        this.appXml = appXml;
        this.componentType = componentType;
        refresh();
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        // beanDefinitionReader.setEntityResolver(null);
        beanDefinitionReader
            .setNamespaceHandlerResolver(new SCANamespaceHandlerResolver(getClassLoader(), componentType));
    }

    protected Resource[] getConfigResources() {
        return new Resource[]{appXml};
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new ScaAdapterPostProcessor(new SpringScaAdapter(componentType)));
        beanFactory.ignoreDependencyInterface(ScaAdapterAware.class);
    }
}
