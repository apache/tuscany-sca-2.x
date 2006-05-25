package org.apache.tuscany.container.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScaReferenceBeanDefinitionParser implements BeanDefinitionParser {

    private static final String REFERENCE_NAME_ATTRIBUTE = "name";
    private static final String REFERENCE_NAME_PROPERTY = "referenceName";
    private static final String TYPE_ATTRIBUTE = "type";
    //private static final String SERVICE_TYPE_PROPERTY = "serviceType";
    //private static final String COMPOSITE_PROPERTY = "scaComposite";
    private static final String DEFAULT_SERVICE_ATTRIBUTE = "default";
    //private static final String DEFAULT_SERVICE_PROPERTY = "defaultServiceName";

    private Deployer deploy;
    private CompositeContext context;

    public ScaReferenceBeanDefinitionParser(Deployer deployer, CompositeContext context ) {

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
