package org.apache.tuscany.core.builder.system;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.SourcePolicyBuilder;
import org.apache.tuscany.core.builder.TargetPolicyBuilder;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

import java.util.List;

/**
 * System wide registry for ContextFactoryBuilder implementations that attach builders to wires
 *
 * @version $Rev$ $Date$
 */
public interface PolicyBuilderRegistry {

    /**
     * Register a builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void registerTargetBuilder(TargetPolicyBuilder builder);

    /**
     * Register a builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void unregisterTargetBuilder(TargetPolicyBuilder builder);

    public void registerSourceBuilder(SourcePolicyBuilder builder);

    /**
     * Register a builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void unregisterSourceBuilder(SourcePolicyBuilder builder);

    public List<TargetPolicyBuilder> getTargetBuilders();

    public List<SourcePolicyBuilder> getSourceBuilders();

    public void build(ConfiguredReference reference, List<WireSourceConfiguration> configurations) throws BuilderException;

    public void build(ConfiguredService service, WireTargetConfiguration configuration) throws BuilderException;
}
