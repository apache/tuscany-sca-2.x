package org.apache.tuscany.spi.policy;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * A registry for policy builders that dispatches to the appropriate builder when converting an assembly to
 * runtime artifacts. Policy builders operate on either a source- or target-side wires.
 *
 * @version $Rev$ $Date$
 */
public interface PolicyBuilderRegistry {

    int INITIAL = 0;
    int EXTENSION = 1;
    int FINAL = 2;

    /**
     * Registers a target-side policy builder. Called by extensions to register their builders.
     *
     * @param phase   the phase hwne the builder must be run
     * @param builder the builder to register
     */
    void registerTargetBuilder(int phase, TargetPolicyBuilder builder);

    /**
     * Registers a source-side policy builder. Called by extensions to register their builders.
     *
     * @param phase   the phase hwne the builder must be run
     * @param builder the builder to register
     */
    void registerSourceBuilder(int phase, SourcePolicyBuilder builder);

    /**
     * Evaluates source-side policy metadata for referenceDefinition and updates the curresponding collection
     * of wire configurations
     *
     * @throws BuilderException
     */
    void buildSource(ReferenceDefinition referenceDefinition, OutboundWire wire) throws BuilderException;

    /**
     * Evaluates target-side policy metadata for configured reference and updates the curresponding
     * collection of wire configurations
     *
     * @throws BuilderException
     */
    void buildTarget(ServiceDefinition serviceDefinition, InboundWire wire) throws BuilderException;
}
