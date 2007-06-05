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
package org.apache.tuscany.implementation.spring;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Parser for the &lt;sca:reference&gt; element
 *
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $
 */
public class ScaReferenceBeanDefinitionParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        // do nothing, this is handled by Tuscany
        return null;
    }

}
