package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.container.spring.config.SCANamespaceHandlerResolver;
import org.springframework.beans.factory.xml.DefaultXmlBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;

/**
 * Overrides the default top-level Spring parser to use {@link org.apache.tuscany.container.spring.config.SCANamespaceHandlerResolver} for resolving
 * namespace handlers
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCARootDefinitionParser extends DefaultXmlBeanDefinitionParser {

    private CompositeComponentType componentType;

    public SCARootDefinitionParser(CompositeComponentType componentType) {
        this.componentType = componentType;
    }

    protected NamespaceHandlerResolver createNamespaceHandlerResolver() {
        ClassLoader classLoader = getReaderContext().getReader().getBeanClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return new SCANamespaceHandlerResolver(classLoader, componentType);
    }


}
