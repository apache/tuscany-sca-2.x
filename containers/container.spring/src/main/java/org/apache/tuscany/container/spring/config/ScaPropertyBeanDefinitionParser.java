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
import org.w3c.dom.Element;

/**
 * Processes <code>property</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */

public class ScaPropertyBeanDefinitionParser implements BeanDefinitionParser {
    public static final String PROPERTY_ELEMENT = "property";
/*
    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
*/

    public ScaPropertyBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
/*
        String id = element.getAttribute(ID_ATTRIBUTE);
        String name = element.getAttribute(NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
*/
        return null;
    }
}
