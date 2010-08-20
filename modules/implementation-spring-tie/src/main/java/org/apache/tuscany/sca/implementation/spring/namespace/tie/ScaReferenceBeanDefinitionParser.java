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
package org.apache.tuscany.sca.implementation.spring.namespace.tie;

import org.apache.tuscany.sca.implementation.spring.context.tie.SCAGenericApplicationContext;
import org.apache.tuscany.sca.implementation.spring.elements.tie.SpringSCAReferenceElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parser for the &lt;sca:reference&gt; element
 *
 * @version $Rev$ $Date$
 */
public class ScaReferenceBeanDefinitionParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        if (registry instanceof SCAGenericApplicationContext) {
            SCAGenericApplicationContext context = (SCAGenericApplicationContext)registry;
            SpringSCAReferenceElement referenceElement =
                new SpringSCAReferenceElement(element.getAttributeNS(null, "name"),
                                              element.getAttributeNS(null, "type"));
            referenceElement.setDefaultBean(element.getAttributeNS(null, "default"));
            context.addSCAReferenceElement(referenceElement);
        }

        // do nothing, this is handled by Tuscany
        return null;
    }

}
