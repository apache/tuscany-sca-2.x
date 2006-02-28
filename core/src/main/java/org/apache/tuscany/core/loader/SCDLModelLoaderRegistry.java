/**
 *
 * Copyright 2005 The Apache Software Foundation
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

import java.util.List;

import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;

/**
 * A ModelLoaderRegistry maintains a list of SCDLModelLoaders that have been contributed
 * to the system by various extension components (such as implementations or bindings).
 *
 *
 * @version $Rev$ $Date$
 */
public interface SCDLModelLoaderRegistry {
    /**
     * Returns the list of registered model loaders.
     *
     * @return the list of registered model loaders
     */
    List<SCDLModelLoader> getLoaders();

    /**
     * Register a model loader.
     *
     * @param loader the loader being contributed by the extension component
     */
    void registerLoader(SCDLModelLoader loader);

    /**
     * Unregister a model loader.
     *
     * @param loader the loader previously contributed by the extension component
     */
    void unregisterLoader(SCDLModelLoader loader);
}
