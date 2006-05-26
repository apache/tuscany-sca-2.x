package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Processes <code>service</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaServiceBeanDefinitionParser implements BeanDefinitionParser {

    public static final String SERVICE_ELEMENT = "service";

    private CompositeComponentType componentType;

    public ScaServiceBeanDefinitionParser(CompositeComponentType componentType) {
        this.componentType = componentType;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return null;
    }

}
