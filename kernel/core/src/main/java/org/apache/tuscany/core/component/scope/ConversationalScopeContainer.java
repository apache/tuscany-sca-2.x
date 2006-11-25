/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.component.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.component.event.ConversationEnd;
import org.apache.tuscany.core.component.event.ConversationStart;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeRuntimeException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.osoa.sca.ConversationEndedException;

/**
 * A scope context which manages atomic component instances keyed on a conversation session
 *
 * @version $Rev: 452655 $ $Date: 2006-10-03 18:09:02 -0400 (Tue, 03 Oct 2006) $
 */
public class ConversationalScopeContainer extends AbstractScopeContainer {
    private static final long CONVERSATION_MAX_AGE = 10 * 1000;
    
    private final Map<AtomicComponent, Map<Object, InstanceWrapper>> contexts;
    private final Map<Object, List<InstanceWrapper>> destroyQueues;

    private Timer conversationTimer;

    public ConversationalScopeContainer() {
        this(null);
    }

    public ConversationalScopeContainer(WorkContext workContext) {
        super("Conversational Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicComponent, Map<Object, InstanceWrapper>>();
        destroyQueues = new ConcurrentHashMap<Object, List<InstanceWrapper>>();
        conversationTimer = new Timer();
    }

    public Scope getScope() {
        return Scope.CONVERSATION;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof ConversationStart) {
            Object key = ((ConversationStart) event).getId();
            workContext.setIdentifier(Scope.CONVERSATION, key);
            for (Map.Entry<AtomicComponent, Map<Object, InstanceWrapper>> entry : contexts.entrySet()) {
                if (entry.getKey().isEagerInit()) {
                    getInstance(entry.getKey(), key);
                }
            }
        } else if (event instanceof ConversationEnd) {
            Object key = ((ConversationEnd) event).getId();
            shutdownInstances(key);
            workContext.clearIdentifier(key);
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        contexts.clear();
        synchronized (destroyQueues) {
            destroyQueues.clear();
        }
        lifecycleState = STOPPED;
    }

    public void register(AtomicComponent component) {
        contexts.put(component, new ConcurrentHashMap<Object, InstanceWrapper>());
        component.addListener(this);
    }

    protected InstanceWrapper getInstanceWrapper(AtomicComponent component, boolean create) throws TargetException {
        Object key = workContext.getIdentifier(Scope.CONVERSATION);
        assert key != null : "Conversational session id not bound in work component";
        InstanceWrapper wrapper = getInstance(component, key);
        if (wrapper instanceof TimedoutInstanceWrapper) {
            throw new TargetException(new ConversationEndedException("Conversation has timed out"));
        }
        
        return wrapper;
    }

    private InstanceWrapper getInstance(AtomicComponent component, Object key) {
        Map<Object, InstanceWrapper> wrappers = contexts.get(component);
        assert wrappers != null : "Component [" + component + "] not registered";
        InstanceWrapper ctx = wrappers.get(key);
        if (ctx == null) {
            ctx = new InstanceWrapperImpl(component, component.createInstance());
            ctx.start();
            wrappers.put(key, ctx);
            List<InstanceWrapper> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceWrapper>();
                destroyQueues.put(key, destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
            
            conversationTimer.schedule(new TimeoutConversation(component, key), CONVERSATION_MAX_AGE);
        }
        
        return ctx;
    }

    private void shutdownInstances(Object key) {
        /*
        for (Map<Object, InstanceWrapper> map : contexts.values()) {
            InstanceWrapper wrapper = map.remove(key);
            wrapper.stop();
        }
        */
        List<InstanceWrapper> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null) {
            for (Map<Object, InstanceWrapper> map : contexts.values()) {
                map.remove(key);
            }
            ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
            synchronized (destroyQueue) {
                while (iter.hasPrevious()) {
                    try {
                        iter.previous().stop();
                    } catch (TargetException e) {
                        // TODO send a monitoring event
                    }
                }
            }
        }
    }
    
    private class TimeoutConversation extends TimerTask {
        
        private AtomicComponent component;
        private Object key;
        
        public TimeoutConversation(AtomicComponent component, Object key) {
            this.component = component;
            this.key = key;
        }
        
        public void run() {
            Map<Object, InstanceWrapper> wrappers = contexts.get(component);
            assert wrappers != null : "Component [" + component + "] not registered";
            InstanceWrapper wrapper = wrappers.get(key);
            wrappers.put(key, new TimedoutInstanceWrapper());
            wrapper.stop();
            List<InstanceWrapper> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                return;
            }
            synchronized (destroyQueue) {
                destroyQueue.remove(wrapper);
            }
        }
    }
    
    private class TimedoutInstanceWrapper implements InstanceWrapper {
        
        public Object getInstance() {
            throw new ScopeRuntimeException();
        }
        
        public int getLifecycleState() {
            throw new ScopeRuntimeException();
        }

        public void start() throws CoreRuntimeException {
            throw new ScopeRuntimeException();
        }

        public void stop() throws CoreRuntimeException {
            throw new ScopeRuntimeException();
        }
    }
}
