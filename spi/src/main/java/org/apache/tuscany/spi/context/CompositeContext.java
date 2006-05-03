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
package org.apache.tuscany.spi.context;

/**
 * A context which contains child component contexts.
 *
 * @version $Rev: 393708 $ $Date: 2006-04-12 21:29:05 -0700 (Wed, 12 Apr 2006) $
 */
public interface CompositeContext extends Context {
    /**
     * Register a Context as a child of this composite.
     *
     * @param context the context to add as a child
     */
    void registerContext(Context context);

    /**
     * Returns the child context associated with a given name
     */
    Context getContext(String name);
}
