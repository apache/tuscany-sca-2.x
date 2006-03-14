package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * Implementations perform the first phase of converting a logical model representing an assembly into a series of
 * runtime or executable artifacts. Specifically, <code>RuntimeConfigurationBuilder</code>s are responsible for
 * analyzing logical model elements and producing {@link RuntimeConfiguration}s that are used to generate executable
 * artifacts such as an {@link org.apache.tuscany.core.context.InstanceContext}. In the case of components, the
 * <code>RuntimeConfiguration</code> will typically contain configuration for instantiating implementation instances
 * with injected properties and references; invocation chains; and configuration necessary to build proxies to
 * implementation instances.
 * <p>
 * As the logical model is analyzed, <code>RuntimeConfigurationBuilder</code>s are guaranteed to be called first and
 * are expected to decorate the logical model with initial <code>RuntimeConfiguration</code>. Certain implementations
 * may choose to delegate tasks to other builders. For example, a builder may handle component implementation types and
 * set up an initial <code>RuntimeConfiguration</code> based on introspected metadata specific to the type. It may
 * then choose to delegate to other builders to handle construction of invocation chains based on policies specified in
 * the logical model. Delegation may be set up by implementing builders as system components and wiring them to other
 * builders. This allows the creation of builders that may modify invocation chains regardless of implementation type,
 * such as generic policy builders.
 * <p>
 * When this first phase is complete, a logical model will be decorated with essentially independent
 * <code>RuntimeConfiguration</code>s. The second phase uses {@link WireBuilder}s to analyze wires represented in
 * the logical model and "connect" the source and target invocation chains held in these
 * <code>RuntimeConfiguration</code>s to form a completed wire. <code>WireBuilder<code>s may use a similar delegation strategy and perform various optimizations.
 * 
 * @version $Rev$ $Date$
 * @see RuntimeConfiguration
 * @see org.apache.tuscany.core.builder.WireBuilder
 */
public interface RuntimeConfigurationBuilder<Y extends Context> {

    /**
     * Builds a runtime configuration for the supplied model object for registration under the supplied context.
     * 
     * @param object the logical configuration model node
     * @param context the context that will be the parent of the built context
     * @throws BuilderException
     */
    public void build(AssemblyModelObject object) throws BuilderException;

}
