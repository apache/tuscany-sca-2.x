package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.Part;

/**
 * A context which contains child component contexts.
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeContext extends Context {
    /**
     * Adds a runtime artifact represented by the model object to the composite context by merging it with existing
     * artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    void registerModelObject(Extensible model) throws ConfigurationException;

    /**
     * Register a Context as a child of this composite.
     *
     * @param context the context to add as a child
     */
    void registerContext(Context context);

    /**
     * Returns the child context associated with a given name
     */
    Context getContext(String name);

    /**
     * Returns the composite managed by this composite context
     */
    @Deprecated
    Composite getComposite();
    
}
