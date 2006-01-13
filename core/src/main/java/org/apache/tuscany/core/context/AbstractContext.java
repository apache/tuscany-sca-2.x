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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Functionality common to all <code>Context<code> implementations
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractContext implements Context {

    public AbstractContext() {
    }

    public AbstractContext(String name) {
        this.name = name;
    }

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected int lifecycleState = UNINITIALIZED;

    public int getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(int state) {
        lifecycleState = state;
    }

    protected List<LifecycleEventListener> contextListener = new CopyOnWriteArrayList();

    public void addContextListener(LifecycleEventListener listener) {
        contextListener.add(listener);
    }

    public void removeContextListener(LifecycleEventListener listener) {
        contextListener.remove(listener);
    }

    public String toString() {
        switch (lifecycleState) {
        case (CONFIG_ERROR):
            return "Context [" + name + "] in state [CONFIG_ERROR]";
        case (ERROR):
            return "Context [" + name + "] in state [ERROR]";
        case (INITIALIZING):
            return "Context [" + name + "] in state [INITIALIZING]";
        case (INITIALIZED):
            return "Context [" + name + "] in state [INITIALIZED]";
        case (RUNNING):
            return "Context [" + name + "] in state [RUNNING]";
        case (STOPPING):
            return "Context [" + name + "] in state [STOPPING]";
        case (STOPPED):
            return "Context [" + name + "] in state [STOPPED]";
        case (UNINITIALIZED):
            return "Context [" + name + "] in state [UNINITIALIZED]";
        default:
            return "Context [" + name + "] in state [UNKNOWN]";
        }
    }

}
