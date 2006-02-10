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
package org.apache.tuscany.core.context;

/**
 * A specialization of an AggregateContext that is able to automatically resolve references
 * for its children using EntryPoint or Service interfaces exposed by it or, recursively, any
 * of it parents.
 *
 * @version $Rev$ $Date$
 */
public interface AutowireContext extends AggregateContext {

    /**
     * Returns an reference to the requested service.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException;

    // todo add additional methods that allow other qualifications to be supplied
}
