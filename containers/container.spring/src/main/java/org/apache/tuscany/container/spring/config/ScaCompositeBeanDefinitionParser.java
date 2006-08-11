package org.apache.tuscany.container.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Processes <code>composite</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaCompositeBeanDefinitionParser implements BeanDefinitionParser {
    public static final String COMPOSITE_ELEMENT = "reference";
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
