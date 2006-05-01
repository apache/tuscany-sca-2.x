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
package org.apache.tuscany.core.system.config;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.injection.Injector;

/**
 * An extensiblity element which provides {@link org.apache.tuscany.core.injection.Injector}s based on
 * component type metadata specific to system services
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SystemInjectorExtensibilityElement {
    /**
     * Creates an injector
     *
     * @param resolver that returns the current composite context
     */
    public Injector<?> getInjector(ContextResolver resolver);

}

