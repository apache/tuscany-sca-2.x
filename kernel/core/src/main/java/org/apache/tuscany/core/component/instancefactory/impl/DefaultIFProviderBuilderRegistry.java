package org.apache.tuscany.core.component.instancefactory.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilder;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderException;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Default implementation of the registry.
 * 
 * @version $Revison$ $Date$
 */
public class DefaultIFProviderBuilderRegistry implements IFProviderBuilderRegistry {

    // Internal cache
    private Map<Class<?>, IFProviderBuilder<? extends InstanceFactoryProvider, ? extends InstanceFactoryProviderDefinition>> registry =
        new ConcurrentHashMap<Class<?>, IFProviderBuilder<? extends InstanceFactoryProvider, ? extends InstanceFactoryProviderDefinition>>();

    /**
     * Builds an instnace factory provider from a definition.
     * 
     * @param providerDefinition Provider definition.
     * @param cl Clasloader to use.
     * @return Instance factory provider.
     */
    @SuppressWarnings("unchecked")
    public InstanceFactoryProvider build(InstanceFactoryProviderDefinition providerDefinition, ClassLoader cl)
        throws IFProviderBuilderException {
        
        IFProviderBuilder builder = registry.get(providerDefinition.getClass());
        if(builder == null) {
            throw new IFProviderBuilderException("No registered builder for " + providerDefinition.getClass());
        }
        // TODO Auto-generated method stub
        return builder.build(providerDefinition, cl);
    }

    /**
     * Registers the builder.
     */
    public <IFPD extends InstanceFactoryProviderDefinition> void register(Class<IFPD> ifpdClass, IFProviderBuilder<?, IFPD> builder) {
        registry.put(ifpdClass, builder);
    }

}
