package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Handler for processing elements defined in a Spring configuration with the SCA namespace
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandler extends NamespaceHandlerSupport {

    private CompositeComponentType componentType;

    public SCANamespaceHandler() {
    }

    // TODO: fix when custom HanespaceHandlerResolver impl can be plugged in 
    public SCANamespaceHandler(CompositeComponentType componentType) {
        this.componentType = componentType;
    }

    public void init() {
        registerBeanDefinitionParser(ScaReferenceBeanDefinitionParser.REFERENCE_ELEMENT,
                new ScaReferenceBeanDefinitionParser(componentType));
        registerBeanDefinitionParser(ScaServiceBeanDefinitionParser.SERVICE_ELEMENT,
                new ScaServiceBeanDefinitionParser(componentType));
        registerBeanDefinitionParser(ScaCompositeBeanDefinitionParser.COMPOSITE_ELEMENT,
                new ScaCompositeBeanDefinitionParser(componentType));
    }
}
