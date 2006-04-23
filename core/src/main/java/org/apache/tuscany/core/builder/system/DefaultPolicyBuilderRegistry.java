/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.builder.system;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.SourcePolicyBuilder;
import org.apache.tuscany.core.builder.SourcePolicyOrderer;
import org.apache.tuscany.core.builder.TargetPolicyBuilder;
import org.apache.tuscany.core.builder.TargetPolicyOrderer;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A system service that serves as the default implementation of a policy builder registry
 *
 * @version $$Rev$$ $$Date$$
 */

@Scope("MODULE")
@Service(interfaces = {PolicyBuilderRegistry.class})
public class DefaultPolicyBuilderRegistry implements PolicyBuilderRegistry {

    private final List<SourcePolicyBuilder> sourceBuilders = new ArrayList<SourcePolicyBuilder>();
    private final List<TargetPolicyBuilder> targetBuilders = new ArrayList<TargetPolicyBuilder>();

    private TargetPolicyOrderer targetOrderer;

    private SourcePolicyOrderer sourceOrderer;

    @Autowire
    public void setTargetOrderer(TargetPolicyOrderer orderer) {
        this.targetOrderer = orderer;
    }

    @Autowire
    public void setSourceOrderer(SourcePolicyOrderer orderer) {
        this.sourceOrderer = orderer;
    }

    public void registerTargetBuilder(TargetPolicyBuilder builder) {
        targetBuilders.add(builder);
    }

    public void unregisterTargetBuilder(TargetPolicyBuilder builder) {
        targetBuilders.remove(builder);
    }

    public void registerSourceBuilder(SourcePolicyBuilder builder) {
        sourceBuilders.add(builder);
    }

    public void unregisterSourceBuilder(SourcePolicyBuilder builder) {
        sourceBuilders.remove(builder);
    }

    public List<TargetPolicyBuilder> getTargetBuilders() {
        return targetBuilders;
    }

    public List<SourcePolicyBuilder> getSourceBuilders() {
        return sourceBuilders;
    }

    public void buildSource(ConfiguredReference reference, List<WireSourceConfiguration> configurations) throws BuilderException {
        for (SourcePolicyBuilder builder : sourceBuilders) {
            builder.build(reference, configurations);
        }
        if (sourceOrderer != null) {
            for (WireSourceConfiguration configuration : configurations) {
                sourceOrderer.order(configuration);
            }
        }
    }

    public void buildTarget(ConfiguredService service, WireTargetConfiguration configuration) throws BuilderException {
        for (TargetPolicyBuilder builder : targetBuilders) {
            builder.build(service, configuration);
        }
        if (targetOrderer != null) {
            targetOrderer.order(configuration);
        }
    }
}
