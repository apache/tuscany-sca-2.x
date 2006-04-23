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
package org.apache.tuscany.core.wire.service;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

import java.util.List;

/**
 * Implementations provide a system service that creates {@link org.apache.tuscany.core.wire.SourceWireFactory}s
 * and {@link org.apache.tuscany.core.wire.TargetWireFactory}s. This service is used by {@link
 * org.apache.tuscany.core.builder.ContextFactoryBuilder}s to provide {@link org.apache.tuscany.core.builder.ContextFactory}s with
 * {@link org.apache.tuscany.core.wire.WireFactory}s for their references and target services. This service is typically autowired
 * to.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface WireFactoryService {

    /**
     * Creates the source-side wire factory for a reference
     *
     * @param configuredReference the configured reference to create the wire factory for
     * @throws BuilderConfigException
     */
    public List<SourceWireFactory> createSourceFactory(ConfiguredReference configuredReference) throws BuilderConfigException;

    /**
     * Creates a target-side wire factory for a service implementing a given interface
     *
     * @param configuredService the configured service to create the wire factory for
     * @throws BuilderConfigException
     */
    public TargetWireFactory createTargetFactory(ConfiguredService configuredService) throws BuilderConfigException;

}
