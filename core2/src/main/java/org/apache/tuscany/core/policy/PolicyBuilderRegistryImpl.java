package org.apache.tuscany.core.policy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.policy.SourcePolicyBuilder;
import org.apache.tuscany.spi.policy.TargetPolicyBuilder;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $Rev$ $Date$
 */
public class PolicyBuilderRegistryImpl implements PolicyBuilderRegistry {

    private final List<List<SourcePolicyBuilder>> sourceBuilders;
    private final List<List<TargetPolicyBuilder>> targetBuilders;

    public PolicyBuilderRegistryImpl() {
        sourceBuilders = new ArrayList<List<SourcePolicyBuilder>>();
        targetBuilders = new ArrayList<List<TargetPolicyBuilder>>();
        for (int i = 0; i <= FINAL; i++) {
            sourceBuilders.add(new ArrayList<SourcePolicyBuilder>());
            targetBuilders.add(new ArrayList<TargetPolicyBuilder>());
        }
    }

    public void registerTargetBuilder(int phase, TargetPolicyBuilder builder) {
        assert(phase >= FINAL): "Illegal phase";
        targetBuilders.get(phase).add(builder);
    }

    public void registerSourceBuilder(int phase, SourcePolicyBuilder builder) {
        assert(phase >= FINAL): "Illegal phase";
        sourceBuilders.get(phase).add(builder);
    }


    public void buildSource(Reference reference, SourceWire wire) throws BuilderException {
        for (List<SourcePolicyBuilder> builders : sourceBuilders) {
            for (SourcePolicyBuilder builder : builders) {
                builder.build(reference,wire);
            }
        }
    }

    public void buildTarget(Service service, TargetWire wire) throws BuilderException {
        for (List<TargetPolicyBuilder> builders : targetBuilders) {
            for (TargetPolicyBuilder builder : builders) {
                builder.build(service,wire);
            }
        }
    }

}
