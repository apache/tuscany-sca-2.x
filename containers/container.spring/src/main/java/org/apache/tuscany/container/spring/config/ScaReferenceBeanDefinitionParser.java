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

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Processes <code>reference</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaReferenceBeanDefinitionParser implements BeanDefinitionParser {

    public static final String REFERENCE_ELEMENT = "reference";
    private static final String REFERENCE_NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String DEFAULT_SERVICE_ATTRIBUTE = "default";

    public ScaReferenceBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String name = element.getAttribute(REFERENCE_NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
        String service = null;
        if (element.hasAttribute(DEFAULT_SERVICE_ATTRIBUTE)) {
            service = element.getAttribute(DEFAULT_SERVICE_ATTRIBUTE);
        }

        RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClass(SCAReference.class);

        ConstructorArgumentValues ctorArgs = beanDef.getConstructorArgumentValues();
        ctorArgs.addIndexedArgumentValue(0, name);
        ctorArgs.addIndexedArgumentValue(1, type);

        if (service != null) {
            // won't throw exception if validation is turned on (boolean type set in schema)
            beanDef.getPropertyValues().addPropertyValue(DEFAULT_SERVICE_ATTRIBUTE, service);
        }

        // create a bean definition holder to be able to register the
        // bean definition with the bean definition registry
        // (obtained through the ParserContext).  Use name as key.
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDef, name);

        // register the BeanDefinitionHolder (which contains the bean definition)
        // with the BeanDefinitionRegistry
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

        return beanDef;
    }
}
