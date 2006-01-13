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
 * A context that offers automatic wiring capabilities
 * 
 * @version $Rev$ $Date$
 */
public interface AutowireContext extends AggregateContext{

    /**
     * Returns an instance of the given type
     * 
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveInstance(Class<T> instanceInterace) throws AutowireResolutionException;

}
