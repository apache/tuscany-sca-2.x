package org.apache.tuscany.spi.policy;

import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
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

    public static final int INITIAL = 0;
    public static final int EXTENSION = 1;
    public static final int FINAL = 2;

    /**
     * Registers a target-side policy builder. Called by extensions to register their builders.
     * @param phase the phase hwne the builder must be run
     * @param builder the builder to register
     */
    public void registerTargetBuilder(int phase, TargetPolicyBuilder builder);

    /**
     * Registers a source-side policy builder. Called by extensions to register their builders.
     * @param phase the phase hwne the builder must be run
     * @param builder the builder to register
     */
    public void registerSourceBuilder(int phase, SourcePolicyBuilder builder);

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
