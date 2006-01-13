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
package org.apache.tuscany.model.config;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.apache.tuscany.model.config.impl.ModelConfigurationProcessorImpl;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * A model config processor.
 *
 */
public interface ModelConfigurationProcessor {

    /**
     * The singleton instance.
     */
    ModelConfigurationProcessor INSTANCE = new ModelConfigurationProcessorImpl();

    /**
     * Configure the given resourceSet
     *
     * @param resourceSet
     * @param bundleContext
     */
    ModelConfiguration process(ResourceSet resourceSet, ResourceLoader bundleContext);

}
