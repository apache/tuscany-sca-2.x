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
package org.apache.tuscany.core.context.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.EventFilter;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.filter.TrueFilter;
import org.apache.tuscany.core.context.event.Event;

/**
 * @version $Rev$ $Date$
 */
public class AbstractLifecycle {
    private static final EventFilter TRUE_FILTER = new TrueFilter();
    protected String name;
    protected int lifecycleState = Lifecycle.UNINITIALIZED;
    // Listeners for context events
    private Map<EventFilter, List<RuntimeEventListener>> listeners;

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

    public void addListener(RuntimeEventListener listener) {
        addListener(TRUE_FILTER, listener);
    }

    public void removeListener(RuntimeEventListener listener) {
        assert (listener != null) : "Listener cannot be null";
        synchronized(getListeners()){
            for (List<RuntimeEventListener> currentList :getListeners().values() ) {
                for(RuntimeEventListener current : currentList){
                    if (current == listener){
                        currentList.remove(current);
                        return;
                    }
                }
            }
        }
    }

    public void addListener(EventFilter filter, RuntimeEventListener listener){
        assert (listener != null) : "Listener cannot be null";
        synchronized (getListeners()){
            List<RuntimeEventListener> list = getListeners().get(filter);
            if (list == null){
                list = new CopyOnWriteArrayList<RuntimeEventListener>();
                listeners.put(filter,list);
            }
            list.add(listener);
        }
    }

    public void publish(Event event){
        assert(event != null): "Event object was null";
        for(Map.Entry<EventFilter,List<RuntimeEventListener>> entry :getListeners().entrySet()){
           if(entry.getKey().match(event)){
               for(RuntimeEventListener listener : entry.getValue()){
                   listener.onEvent(event);
               }
           }
        }
    }

    protected  Map<EventFilter, List<RuntimeEventListener>> getListeners(){
        if (listeners == null) {
            listeners = new ConcurrentHashMap<EventFilter, List<RuntimeEventListener>>();
        }
        return listeners;
    }

    public String toString() {
        switch (lifecycleState) {
        case (Lifecycle.CONFIG_ERROR):
            return "Context [" + name + "] in state [CONFIG_ERROR]";
        case (Lifecycle.ERROR):
            return "Context [" + name + "] in state [ERROR]";
        case (Lifecycle.INITIALIZING):
            return "Context [" + name + "] in state [INITIALIZING]";
        case (Lifecycle.INITIALIZED):
            return "Context [" + name + "] in state [INITIALIZED]";
        case (Lifecycle.RUNNING):
            return "Context [" + name + "] in state [RUNNING]";
        case (Lifecycle.STOPPING):
            return "Context [" + name + "] in state [STOPPING]";
        case (Lifecycle.STOPPED):
            return "Context [" + name + "] in state [STOPPED]";
        case (Lifecycle.UNINITIALIZED):
            return "Context [" + name + "] in state [UNINITIALIZED]";
        default:
            return "Context [" + name + "] in state [UNKNOWN]";
        }
    }
}
