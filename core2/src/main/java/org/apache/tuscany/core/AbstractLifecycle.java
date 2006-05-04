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
package org.apache.tuscany.core;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.Lifecycle;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractLifecycle implements Lifecycle {

    protected String name;
    protected int lifecycleState = UNINITIALIZED;

    public AbstractLifecycle(String name) {
        this.name = name;
    }

    public AbstractLifecycle() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLifecycleState() {
        return lifecycleState;
    }

    protected void setLifecycleState(int lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public void start() throws CoreRuntimeException {
        setLifecycleState(STARTED);
    }

    public void stop() throws CoreRuntimeException {
        setLifecycleState(STOPPED);
    }

    public String toString() {
        if (name != null){
        switch (lifecycleState) {
            case (Lifecycle.CONFIG_ERROR):
                return new StringBuilder().append("[").append(name).append("] in state [CONFIG_ERROR]").toString();
            case (Lifecycle.ERROR):
                return new StringBuilder().append("[").append(name).append("] in state [ERROR]").toString();
            case (Lifecycle.INITIALIZING):
                return new StringBuilder().append("[").append(name).append("] in state [INITIALIZING]").toString();
            case (Lifecycle.INITIALIZED):
                return new StringBuilder().append("[").append(name).append("] in state [INITIALIZED]").toString();
            case (Lifecycle.RUNNING):
                return new StringBuilder().append("[").append(name).append("] in state [RUNNING]").toString();
            case (Lifecycle.STOPPING):
                return new StringBuilder().append("[").append(name).append("] in state [STOPPING]").toString();
            case (Lifecycle.STOPPED):
                return new StringBuilder().append("[").append(name).append("] in state [STOPPED]").toString();
            case (Lifecycle.UNINITIALIZED):
                return new StringBuilder().append("[").append(name).append("] in state [UNINITIALIZED]").toString();
            default:
                return new StringBuilder().append("[").append(name).append("] in state [UNKNOWN]").toString();
            }
        }else{
            switch (lifecycleState) {
            case (Lifecycle.CONFIG_ERROR):
                return new StringBuilder().append("state [CONFIG_ERROR]").toString();
            case (Lifecycle.ERROR):
                return new StringBuilder().append("state [ERROR]").toString();
            case (Lifecycle.INITIALIZING):
                return new StringBuilder().append("state [INITIALIZING]").toString();
            case (Lifecycle.INITIALIZED):
                return new StringBuilder().append("state [INITIALIZED]").toString();
            case (Lifecycle.RUNNING):
                return new StringBuilder().append("state [RUNNING]").toString();
            case (Lifecycle.STOPPING):
                return new StringBuilder().append("state [STOPPING]").toString();
            case (Lifecycle.STOPPED):
                return new StringBuilder().append("state [STOPPED]").toString();
            case (Lifecycle.UNINITIALIZED):
                return new StringBuilder().append("state [UNINITIALIZED]").toString();
            default:
                return new StringBuilder().append("state [UNKNOWN]").toString();
            }
        }
    }
}
