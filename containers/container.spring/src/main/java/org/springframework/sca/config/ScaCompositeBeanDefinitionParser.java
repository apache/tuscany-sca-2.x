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
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.sca.ScaComposite;
import org.w3c.dom.Element;

/**
 * Parser for &lt;sca:composite&gt; elements
 * 
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaCompositeBeanDefinitionParser implements BeanDefinitionParser {

	static final String SCA_COMPOSITE_BEAN_NAME = "scaComposite";
	private static final String MODULE_ATTRIBUTE_NAME = "component";
	private static final String MODULE_ID = "component";
	private static final String ADAPTER_ATTRIBUTE = "sca-adapter-class";
	private static final String ADAPTER_CLASS_PROPERTY = "scaAdapterClass";
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		if (registry.containsBeanDefinition(SCA_COMPOSITE_BEAN_NAME)) {
			throw new IllegalArgumentException(
					"At most one <sca:composite> element can be declared in a bean factory");
		}
		BeanDefinition beanDef = createScaCompositeBeanDefinition(element);
		registry.registerBeanDefinition(SCA_COMPOSITE_BEAN_NAME, beanDef);
		return beanDef;
	}

	private BeanDefinition createScaCompositeBeanDefinition(Element element) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ScaComposite.class);
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue(MODULE_ID, element.getAttribute(MODULE_ATTRIBUTE_NAME));
		if (element.hasAttribute(ADAPTER_ATTRIBUTE)) {
			props.addPropertyValue(ADAPTER_CLASS_PROPERTY,element.getAttribute(ADAPTER_ATTRIBUTE));
		}
		beanDefinition.setPropertyValues(props);
		return beanDefinition;
	}

}
