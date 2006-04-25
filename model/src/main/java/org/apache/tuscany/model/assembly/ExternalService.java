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
package org.apache.tuscany.model.assembly;

import java.util.List;


/**
 * An external service consumed by its containing {@link Composite}.
 * All references used by the composite are specified as external services.
 */
public interface ExternalService extends Part {

    /**
     * Returns the bindings that can be used by operations on this external service.
     * A single external service may be bound to multiple transports.
     */
    List<Binding> getBindings();

    /**
     * Returns the override option that determines if any wiring for this external service
     * that is contained in this composite can be overridden by wired supplied from outside.
     */
    OverrideOption getOverrideOption();

    /**
     * Set the override option that determines if any wiring for this external service
     * that is contained in this composite can be overridden by wired supplied from outside.
     *
     * @param value the option that determines how wires can be overriden
     */
    void setOverrideOption(OverrideOption value);

    /**
     * Returns the configured service that this external service provides to other parts of the containing composite.
     * @return the configured service that this external service provides to other parts of the containing composite
     */
    ConfiguredService getConfiguredService();

    /**
     * Sets  the configured service that this external service provides to other parts of the containing composite
     * @param configuredService the configured service that this external service provides to other parts of the containing composite
     */
    void setConfiguredService(ConfiguredService configuredService);
}
