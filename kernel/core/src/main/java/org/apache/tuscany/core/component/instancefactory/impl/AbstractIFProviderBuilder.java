package org.apache.tuscany.core.component.instancefactory.impl;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilder;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.osoa.sca.annotations.Reference;

/**
 * Abstarct implementation that supportes registration.
 */
public abstract class AbstractIFProviderBuilder<IFP extends InstanceFactoryProvider, IFPD extends InstanceFactoryProviderDefinition>
    implements IFProviderBuilder<IFP, IFPD> {
    
    /**
     * Returns the IFPD class.
     * @return IFPD class.
     */
    protected abstract Class<IFPD> getIfpdClass();
    
    /**
     * Injects the builder registry.
     * @param registry The builder registry.
     */
    @Reference
    public void setBuilderRegistry(IFProviderBuilderRegistry registry) {
        registry.register(getIfpdClass(), this);
    }

}
