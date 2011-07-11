package org.apache.tuscany.sca.binding.local;

/**
 * A factory for the Local SCA binding model.
 */
public class LocalSCABindingFactoryImpl implements LocalSCABindingFactory {
    public LocalSCABindingFactoryImpl() {

    }

    @Override
    public LocalSCABinding createLocalBinding() {
        return new LocalSCABindingImpl();
    }

}
