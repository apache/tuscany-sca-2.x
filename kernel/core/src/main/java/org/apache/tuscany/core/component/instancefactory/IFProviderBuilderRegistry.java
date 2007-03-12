package org.apache.tuscany.core.component.instancefactory;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Registry for instance factory builders.
 * 
 * @version $Revision$ $Date$
 */
public interface IFProviderBuilderRegistry {

    /**
     * 
     * Registers an instance factory provider builder.
     * 
     * @param ifpdClass Instance factory provider definition class.
     * @param builder Instance factory provider builder.
     */
    <IFPD extends InstanceFactoryProviderDefinition> void register(Class<IFPD> ifpdClass, IFProviderBuilder<?, IFPD> builder);
    
    /**
     * Builds an instnace factory provider from a definition.
     * @param providerDefinition Provider definition.
     * @return Instance factory provider.
     */
    InstanceFactoryProvider build(InstanceFactoryProviderDefinition providerDefinition);
}
