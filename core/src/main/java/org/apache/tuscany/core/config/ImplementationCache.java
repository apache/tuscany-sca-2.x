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
package org.apache.tuscany.core.config;

import org.apache.tuscany.model.assembly.Implementation;

import javax.xml.namespace.QName;

/**
 * Cache of introspected implementations.
 *
 * @version $Rev$ $Date$
 */
public interface ImplementationCache {
    /**
     * Return an implementation from a given namespace.
     *
     * @param type the namespace that defines the type of implementation
     * @param name the name of an implementation in that namespace
     * @return the implementation or null if it is not present in the cache
     */
    Implementation get(QName type, String name);

    /**
     * Add an implementation to the cache
     *
     * @param type           the namespace that defines the type of implementation
     * @param name           the name of an implementation in that namespace
     * @param implementation the introspected implementation
     */
    void put(QName type, String name, Implementation implementation);
}
