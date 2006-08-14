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
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Processes <code>composite</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaCompositeBeanDefinitionParser implements BeanDefinitionParser {
    public static final String COMPOSITE_ELEMENT = "composite";
/*
    private static final String COMPONENT_ATTRIBUTE = "component";
    private static final String SCA_ADAPTER_CLASS_ATTRIBUTE = "sca-adapter-class";
*/

    public ScaCompositeBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
/*
        String component = element.getAttribute(COMPONENT_ATTRIBUTE);
        String adapterClass;
        if (element.hasAttribute(SCA_ADAPTER_CLASS_ATTRIBUTE)) {
            adapterClass = element.getAttribute(SCA_ADAPTER_CLASS_ATTRIBUTE);
        }
*/
        return null;
    }
}
