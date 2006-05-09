package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Extensible;

import java.util.List;

/**
 * A context which contains child component contexts.
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeContext extends Context {

    public String getURI();

    public void setURI(String uri);

    /**
     * Returns the parent context, or null if the context does not have one
     */
    public CompositeContext getParent();

    /**
     * Sets the parent context
     */
    public void setParent(CompositeContext parent);

    /**
     * Adds runtime artifacts represented by the set of model objects to the composite context by merging them with
     * existing artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    public void registerModelObjects(List<? extends Extensible> models) throws ConfigurationException;

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
    public void registerModelObject(Extensible model) throws ConfigurationException;

    /**
     * Returns the child context associated with a given name
     */
    public Context getContext(String name);

    /**
     * Returns the composite managed by this composite context
     */
    @Deprecated
    public Composite getComposite();
    
}
