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
 * Represents an external service.
 */
public interface ExternalService extends AggregatePart, Extensible {

    /**
     * Returns the bindings configured on this external service.
     */
    List<Binding> getBindings();

    /**
     * Returns the override option.
     */
    OverrideOption getOverrideOption();

    /**
     * Sets the override option.
     */
    void setOverrideOption(OverrideOption value);

    /**
     * Returns the configured service exposed by this external service.
     * @return
     */
    ConfiguredService getConfiguredService();

    /**
     * Sets the configured service exposed by this external service.
     * @param configuredService
     */
    void setConfiguredService(ConfiguredService configuredService);
}
