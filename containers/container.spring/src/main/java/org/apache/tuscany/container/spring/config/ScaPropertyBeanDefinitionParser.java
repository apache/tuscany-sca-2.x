package org.apache.tuscany.container.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Processes <code>property</code> elements in a Spring configuration
 *
 * @version $$Rev: $$ $$Date: $$
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
