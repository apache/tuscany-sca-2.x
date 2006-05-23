/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.spi.loader;

import org.apache.tuscany.spi.model.Implementation;

/**
 * @version $Rev$ $Date$
 */
public interface ComponentTypeLoader<I extends Implementation> {
    /**
     * Load the component type information for the supplied implementation.
     *
     * @param implementation the implementation whose component type information should be loaded
     */
    void load(I implementation);
}
