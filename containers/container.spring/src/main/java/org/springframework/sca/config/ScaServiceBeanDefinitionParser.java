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
 * Created on 10-Apr-2006 by Adrian Colyer
 */
package org.springframework.sca.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.sca.ScaServiceExporter;
import org.w3c.dom.Element;

/**
 * Parser for the &lt;sca:service/&gt; element 
 * 
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaServiceBeanDefinitionParser implements BeanDefinitionParser {

	private static final String SERVICE_NAME_ATTRIBUTE = "name";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String TARGET_ATTRIBUTE = "target";
	private static final String SERVICE_NAME_PROPERTY = "serviceName";
	private static final String SERVICE_TYPE_PROPERTY = "serviceType";
	private static final String TARGET_PROPERTY = "target";
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		String serviceName = element.getAttribute(SERVICE_NAME_ATTRIBUTE);
		BeanDefinition beanDef = createBeanDefinition(element);
		registry.registerBeanDefinition(serviceName, beanDef);
		return beanDef;
	}
	
	private BeanDefinition createBeanDefinition(Element element) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ScaServiceExporter.class);
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue(SERVICE_TYPE_PROPERTY, element.getAttribute(TYPE_ATTRIBUTE));
		props.addPropertyValue(TARGET_PROPERTY,new RuntimeBeanReference(element.getAttribute(TARGET_ATTRIBUTE)));
		props.addPropertyValue(SERVICE_NAME_PROPERTY,element.getAttribute(SERVICE_NAME_ATTRIBUTE));
		beanDefinition.setPropertyValues(props);
		return beanDefinition;
	}

}
