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
package org.apache.tuscany.core.context.impl;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.EventFilter;
import org.apache.tuscany.core.context.event.Event;
import org.apache.tuscany.core.context.filter.TrueFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Functionality common to all <code>Context<code> implementations
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractContext extends AbstractLifecycle implements Context {

    public AbstractContext() {
    }

    public AbstractContext(String name) {
        super(name);
    }


}
