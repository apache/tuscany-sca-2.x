package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Processes <code>reference</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaReferenceBeanDefinitionParser implements BeanDefinitionParser {

    public static final String REFERENCE_ELEMENT = "reference";
    private static final String REFERENCE_NAME_ATTRIBUTE = "name";
    private static final String REFERENCE_NAME_PROPERTY = "referenceName";
    private static final String TYPE_ATTRIBUTE = "type";
    //private static final String SERVICE_TYPE_PROPERTY = "serviceType";
    //private static final String COMPOSITE_PROPERTY = "scaComposite";
    private static final String DEFAULT_SERVICE_ATTRIBUTE = "default";
    //private static final String DEFAULT_SERVICE_PROPERTY = "defaultServiceName";

    private CompositeComponentType componentType;

    public ScaReferenceBeanDefinitionParser(CompositeComponentType componentType) {
        this.componentType = componentType;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String referenceName = element.getAttribute(REFERENCE_NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
        String service;
        if (element.hasAttribute(DEFAULT_SERVICE_ATTRIBUTE)) {
            service = element.getAttribute(DEFAULT_SERVICE_ATTRIBUTE);
        }
        return null;
    }

}
