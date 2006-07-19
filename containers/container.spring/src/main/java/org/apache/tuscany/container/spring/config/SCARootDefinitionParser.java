package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;

/**
 * Overrides the default top-level Spring parser to use
 * {@link org.apache.tuscany.container.spring.config.SCANamespaceHandlerResolver}
 * for resolving namespace handlers
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCARootDefinitionParser extends DefaultBeanDefinitionDocumentReader {

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
