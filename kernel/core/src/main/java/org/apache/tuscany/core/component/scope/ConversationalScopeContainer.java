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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.Store;
import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

/**
 * A scope context which manages atomic component instances keyed on a conversation session
 *
 * @version $Rev: 452655 $ $Date: 2006-10-03 18:09:02 -0400 (Tue, 03 Oct 2006) $
 */
public class ConversationalScopeContainer extends AbstractScopeContainer implements ScopeContainer {
    private Store nonDurableStore;
    private Map<AtomicComponent, AtomicComponent> components;

    public ConversationalScopeContainer(@Autowire Store store, @Autowire WorkContext workContext) {
        super("Conversational Scope", workContext);
        this.nonDurableStore = store;
        components = new ConcurrentHashMap<AtomicComponent, AtomicComponent>();
    }

    public Scope getScope() {
        return Scope.CONVERSATION;
    }

    public void onEvent(Event event) {
        checkInit();
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        lifecycleState = STOPPED;
    }

    public void register(AtomicComponent component) {
        components.put(component, component);
        component.addListener(this);
    }

    @Override
    public Object getInstance(AtomicComponent component) throws TargetException {
        String conversationId = getConversationId(component);
        try {
            workContext.setCurrentAtomicComponent(component);
            Object instance = nonDurableStore.readRecord(component, conversationId);
            if (instance != null) {
                if (component.getMaxIdleTime() > 0) {
                    // update expiration
                    long expire = System.currentTimeMillis() + component.getMaxIdleTime();
                    nonDurableStore.updateRecord(component, conversationId, instance, expire);
                }
                return instance;
            } else {
                Object o = component.createInstance();
                long expire = calculateExpiration(component);
                nonDurableStore.insertRecord(component, conversationId, o, expire);
                return o;
            }
        } catch (StoreReadException e) {
            TargetException e2 = new TargetException(e);
            e2.setIdentifier(component.getName());
            throw e2;
        } catch (StoreWriteException e) {
            TargetException e2 = new TargetException(e);
            e2.setIdentifier(component.getName());
            throw e2;
        } finally {
            workContext.setCurrentAtomicComponent(null);
        }
    }

    public Object getAssociatedInstance(AtomicComponent component) throws TargetException {
        String conversationId = getConversationId(component);
        try {
            workContext.setCurrentAtomicComponent(component);
            Object instance = nonDurableStore.readRecord(component, conversationId);
            if (instance != null) {
                if (component.getMaxIdleTime() > 0) {
                    // update expiration
                    long expire = System.currentTimeMillis() + component.getMaxIdleTime();
                    nonDurableStore.updateRecord(component, conversationId, instance, expire);
                }
                return instance;
            } else {
                throw new TargetNotFoundException(component.getName());
            }
        } catch (StoreReadException e) {
            throw new TargetException(e);
        } catch (StoreWriteException e) {
            throw new TargetException(e);
        } finally {
            workContext.setCurrentAtomicComponent(null);
        }
    }

    public void persistNew(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        try {
            nonDurableStore.insertRecord(component, id, instance, expiration);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        try {
            nonDurableStore.updateRecord(component, id, instance, expiration);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    public void remove(AtomicComponent component) throws PersistenceException {
        String conversationId = getConversationId(component);
        try {
            nonDurableStore.removeRecord(component, conversationId);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    protected InstanceWrapper getInstanceWrapper(AtomicComponent component, boolean create) throws TargetException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the conversation id associated with the current invocation context
     */
    private String getConversationId(AtomicComponent component) {
        String conversationId = (String) workContext.getIdentifier(Scope.CONVERSATION);
        if (conversationId == null) {
            TargetException e = new TargetException("Conversation id not set in context");
            e.setIdentifier(component.getName());
            throw e;
        }
        return conversationId;
    }

    private long calculateExpiration(AtomicComponent component) {
        if (component.getMaxAge() > 0) {
            long now = System.currentTimeMillis();
            return now + component.getMaxAge();
        } else if (component.getMaxIdleTime() > 0) {
            long now = System.currentTimeMillis();
            return now + component.getMaxIdleTime();
        } else {
            return Store.DEFAULT_EXPIRATION_OFFSET;
        }
    }
}
