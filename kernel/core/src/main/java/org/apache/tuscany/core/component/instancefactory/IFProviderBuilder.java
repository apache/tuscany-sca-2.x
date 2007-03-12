package org.apache.tuscany.core.component.instancefactory;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Interface for building instance factories.
 */
public interface IFProviderBuilder<IFP extends InstanceFactoryProvider, 
                                                IFPD extends InstanceFactoryProviderDefinition> {

    /**
     * Builds an instance factory provider from provider definition.
     * 
     * @param ifpd Instance factory provider definition.
     * @param cl Classloader to use.
     * @return Instance factory provider.
     */
    IFP build(IFPD ifpd, ClassLoader cl);
}
