package org.apache.tuscany.container.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import org.apache.tuscany.spi.model.CompositeComponentType;

/**
 * Handler for processing elements defined in a Spring configuration with the SCA namespace
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandler extends NamespaceHandlerSupport {

    public SCANamespaceHandler() {
    }

    // TODO: fix when custom HanespaceHandlerResolver impl can be plugged in 
    public SCANamespaceHandler(CompositeComponentType componentType) {
    }

    public void init() {
        registerBeanDefinitionParser(ScaReferenceBeanDefinitionParser.REFERENCE_ELEMENT,
                new ScaReferenceBeanDefinitionParser());
        registerBeanDefinitionParser(ScaServiceBeanDefinitionParser.SERVICE_ELEMENT,
                new ScaServiceBeanDefinitionParser());
        registerBeanDefinitionParser(ScaCompositeBeanDefinitionParser.COMPOSITE_ELEMENT,
                new ScaCompositeBeanDefinitionParser());
    }
}
