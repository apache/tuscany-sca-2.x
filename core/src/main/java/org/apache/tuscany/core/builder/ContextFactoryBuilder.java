package org.apache.tuscany.core.builder;

import org.apache.tuscany.model.assembly.AssemblyObject;

/**
 * The extension point for component types in the runtime. Implementations perform the first phase of converting an assembly model
 * into a series of runtime artifacts. Specifically, <code>ContextFactoryBuilder</code>s are responsible for analyzing the
 * assembly model and producing {@link ContextFactory}s that are used to generate executable artifacts such as an {@link
 * org.apache.tuscany.core.context.Context}. In the case of components, the <code>ContextFactory</code> will typically contain
 * configuration for instantiating implementation instances with injected properties and references.
 * <p/>
 * As the assembly model is analyzed, <code>ContextFactoryBuilder</code>s are guaranteed to be called first and are expected to
 * decorate the assembly model with <code>ContextFactory</code>s.
 * <p/>
 * The second phase uses {@link WireBuilder}s to connect the source and target wire chains held in these
 * <code>ContextFactory</code>s to form a completed wire. <code>WireBuilder<code>s may use a similar delegation strategy and
 * perform various optimizations.
 *
 * @version $Rev$ $Date$
 * @see ContextFactory
 * @see WireBuilder
 */
public interface ContextFactoryBuilder {

    /**
     * Creates or updates a context factory based on configuration contained in the given model object. The model object is
     * decorated with the factory.
     *
     * @param object the logical configuration model node
     * @throws BuilderException
     */
    public void build(AssemblyObject object) throws BuilderException;

}
