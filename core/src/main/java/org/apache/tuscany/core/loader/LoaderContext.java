/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.loader;

import org.apache.tuscany.common.resource.ResourceLoader;

/**
 * Context holder that can be used during the load process to store information
 * that is not part of the logical model. This should be regarded as transient
 * and references to this context should not be stored inside the model.
 *
 * @version $Rev$ $Date$
 */
public class LoaderContext {
    private final ResourceLoader resourceLoader;

    /**
     * Constructor specifying the loader for application resources.
     *
     * @param resourceLoader the loader for application resources
     */
    public LoaderContext(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Returns a resource loader that can be used to load application resources.
     * @return a resource loader that can be used to load application resources
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
