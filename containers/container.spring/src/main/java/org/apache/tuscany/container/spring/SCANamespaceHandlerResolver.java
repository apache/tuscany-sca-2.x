package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandlerResolver extends DefaultNamespaceHandlerResolver {

    private static final String SCA_NAMESPACE = ""; //FIXME

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

    /**
     * Locate the {@link org.springframework.beans.factory.xml.NamespaceHandler} for the supplied namespace
     * URI from the configured mappings.
     */
    public NamespaceHandler resolve(String namespaceUri) {
        if (SCA_NAMESPACE.equals(namespaceUri)) {
            return handler;
        }
        return super.resolve(namespaceUri);
    }

}
