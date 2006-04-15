package org.apache.tuscany.core.builder;

import org.apache.tuscany.model.assembly.AssemblyObject;

/**
 * Implementations perform the first phase of converting a logical model representing an assembly into a series of
 * runtime or executable artifacts. Specifically, <code>ContextFactoryBuilder</code>s are responsible for analyzing
 * logical model elements and producing {@link ContextFactory}s that are used to generate executable artifacts such as
 * an {@link org.apache.tuscany.core.context.Context}. In the case of components, the
 * <code>ContextFactory</code> will typically contain configuration for instantiating implementation instances with
 * injected properties and references; wire chains; and configuration necessary to build proxies to implementation
 * instances.
 * <p>
 * As the logical model is analyzed, <code>ContextFactoryBuilder</code>s are guaranteed to be called first and are
 * expected to decorate the logical model with initial <code>ContextFactory</code>. Certain implementations may
 * choose to delegate tasks to other builders. For example, a builder may handle component implementation types and set
 * up an initial <code>ContextFactory</code> based on introspected metadata specific to the type. It may then choose
 * to delegate to other builders to handle construction of wire chains based on policies specified in the logical
 * model. Delegation may be set up by implementing builders as system components and wiring them to other builders. This
 * allows the creation of builders that may modify wire chains regardless of implementation type, such as generic
 * policy builders.
 * <p>
 * When this first phase is complete, a logical model will be decorated with essentially independent
 * <code>ContextFactory</code>s. The second phase uses {@link WireBuilder}s to analyze wires represented in the
 * logical model and "connect" the source and target wire chains held in these <code>ContextFactory</code>s to
 * form a completed wire.
 * <code>WireBuilder<code>s may use a similar delegation strategy and perform various optimizations.
 * 
 * @version $Rev$ $Date$
 * @see ContextFactory
 * @see org.apache.tuscany.core.builder.WireBuilder
 */
public interface ContextFactoryBuilder{

    /**
     * Creates or updates a context factory based on configuration contained in the given model object. The model object
     * is decorated with the factory.
     * 
     * @param object the logical configuration model node
     * @throws BuilderException
     */
    public void build(AssemblyObject object) throws BuilderException;

}
