/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
 * A System registry for {@link org.apache.tuscany.core.builder.PolicyBuilder}s. <code>PolicyBuilder</code>s will be invoked when
 * a {@link org.apache.tuscany.core.wire.WireFactory} is constructed by the {@link org.apache.tuscany.core.wire.WireFactory}
 * service.
 * <p/>
 * <code>PolicyBuilder</code>s operate on either a source- or target-side wire and typically are registered by runtime extensions
 * through {@link #registerTargetBuilder} or {@link #registerSourceBuilder}
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
     * Evaluates source-side policy metadata for configured reference and updates the curresponding collection of wire configurations
     *
     * @throws BuilderException
     */
    public void buildSource(ConfiguredReference reference, List<WireSourceConfiguration> configurations) throws BuilderException;

    /**
     * Evaluates target-side policy metadata for configured reference and updates the curresponding collection of wire configurations 
     *
     * @throws BuilderException
     */
    public void buildTarget(ConfiguredService service, WireTargetConfiguration configuration) throws BuilderException;
}
