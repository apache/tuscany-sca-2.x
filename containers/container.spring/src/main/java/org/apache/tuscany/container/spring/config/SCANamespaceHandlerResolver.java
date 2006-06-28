package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.container.spring.config.SCANamespaceHandler;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;

/**
 * Overrides the default Spring namespace resolver to automatically register {@link SCANamespaceHandler} instead
 * of requiring a value to be supplied in a Spring configuration
 *
 * TODO: Figure out how to activate this impl
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandlerResolver extends DefaultNamespaceHandlerResolver {

    private static final String SCA_NAMESPACE = "http://www.springframework.org/schema/sca";

    private SCANamespaceHandler handler;

    public SCANamespaceHandlerResolver(ClassLoader classLoader, CompositeComponentType componentType) {
        super(classLoader);
        handler = new SCANamespaceHandler(componentType);
    }

    public SCANamespaceHandlerResolver(String handlerMappingsLocation,
                                       ClassLoader classLoader,
                                       CompositeComponentType componentType) {
        super(handlerMappingsLocation, classLoader);
        handler = new SCANamespaceHandler(componentType);
    }

    public NamespaceHandler resolve(String namespaceUri) {
        if (SCA_NAMESPACE.equals(namespaceUri)) {
            return handler;
        }
        return super.resolve(namespaceUri);
    }
}
