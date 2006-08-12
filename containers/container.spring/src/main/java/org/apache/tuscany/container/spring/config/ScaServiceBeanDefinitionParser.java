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
package org.apache.tuscany.container.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.w3c.dom.Element;

/**
 * Processes <code>service</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaServiceBeanDefinitionParser implements BeanDefinitionParser {

    public static final String SERVICE_ELEMENT = "service";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String TARGET_ATTRIBUTE = "target";

    public ScaServiceBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String name = element.getAttribute(NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
        String targetName = element.getAttribute(TARGET_ATTRIBUTE);

        BeanDefinitionBuilder proxyBean = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        proxyBean.addPropertyReference("target", targetName);
        proxyBean.addPropertyValue("proxyInterfaces", type);

        // REVIEW: May need to account for singleton-ness of the target?  (ie, if target is singleton=false,
        // perhaps the proxy should also be singleton=false).

        parserContext.getRegistry().registerBeanDefinition(name, proxyBean.getBeanDefinition());

        // REVIEW: It may make sense to register an additional bean to capture/expose the presence &
        // attributes of the service element itself.  Such code as:
        
        /*
        BeanDefinitionBuilder serviceBean = BeanDefinitionBuilder.rootBeanDefinition(SCAService.class);
        serviceBean.addConstructorArg(name);
        serviceBean.addConstructorArg(type);
        serviceBean.addConstructorArg(targetName);

        parserContext.getRegistry().registerBeanDefinition(name, serviceBean.getBeanDefinition());
        */

        return null;
    }
}
