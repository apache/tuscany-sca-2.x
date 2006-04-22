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

import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

import java.util.List;

/**
 * Implementations are responsible for providing a system service that creates {@link org.apache.tuscany.core.wire.SourceWireFactory}s
 * and {@link org.apache.tuscany.core.wire.TargetWireFactory}s
 *
 * @version $$Rev$$ $$Date$$
 */
public interface WireFactoryService {

    public List<SourceWireFactory> createSourceFactory(ConfiguredReference configuredReference);

    public SourceWireFactory createSourceFactory(QualifiedName qName, ConfiguredService configuredService);

    /**
     * Creates a target-side wire factory for a service implementing a given interface
     *
     * @param configuredService the configured service to create the wire factory for
     */
    public TargetWireFactory createTargetFactory(ConfiguredService configuredService);

}
