package org.apache.tuscany.spi.policy;

import java.util.List;

import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * A System builderRegistry for policy builders.
 * <p/>
 * Policy builders operate on either a source- or target-side wire and typically are registered by runtime
 * extensions through {@link #registerTargetBuilder} or {@link #registerSourceBuilder}
 *
 * @version $Rev$ $Date$
 */
public interface PolicyBuilderRegistry {

    /**
     * Registers a target-side policy builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void registerTargetBuilder(TargetPolicyBuilder builder);

    /**
     * De-registers a target-side builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void unregisterTargetBuilder(TargetPolicyBuilder builder);

    /**
     * Registers a source-side policy builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void registerSourceBuilder(SourcePolicyBuilder builder);

    /**
     * De-registers a source-side builder. Called by extensions to register their builders.
     *
     * @param builder the builder to register
     */
    public void unregisterSourceBuilder(SourcePolicyBuilder builder);

    /**
     * Returns the list of registered target-side builders
     */
    public List<TargetPolicyBuilder> getTargetBuilders();

    /**
     * Returns the list of registered source-side builders
     */
    public List<SourcePolicyBuilder> getSourceBuilders();

    /**
     * Evaluates source-side policy metadata for reference and updates the curresponding collection of wire
     * configurations
     *
     * @throws BuilderException
     */
    public void buildSource(Reference reference, SourceWire wire) throws BuilderException;

    /**
     * Evaluates target-side policy metadata for configured reference and updates the curresponding collection
     * of wire configurations
     *
     * @throws BuilderException
     */
    public void buildTarget(Service service, TargetWire wire) throws BuilderException;
}
