package org.apache.tuscany.core.component.instancefactory.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilder;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

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
     * Builds the IF provider.
     */
    public InstanceFactoryProvider build(InstanceFactoryProviderDefinition providerDefinition) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Registers the builder.
     */
    public <IFPD extends InstanceFactoryProviderDefinition> void register(Class<IFPD> ifpdClass, IFProviderBuilder<?, IFPD> builder) {
        registry.put(ifpdClass, builder);
    }

}
