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
package org.apache.tuscany.core.context;

/**
 * Implementations enable lazy retrieval of a scope id associated with a request, i.e. an id (and presumably a context) do not
 * have to be generated if the scope is never accessed. Identifiers are associated with the current request thread and keyed on
 * scope type.
 * 
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.container.module.EventContext
 */
public interface ScopeIdentifier {

    /**
     * Returns the scope id for the request.
     */
    public Object getIdentifier();
}
