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
package org.apache.tuscany.sca.core.scope;

import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.event.Event;
import org.apache.tuscany.sca.event.RuntimeEventListener;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.PersistenceException;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.TargetDestructionException;
import org.apache.tuscany.sca.scope.TargetResolutionException;
import org.apache.tuscany.sca.store.Store;
import org.apache.tuscany.sca.store.StoreExpirationEvent;
import org.apache.tuscany.sca.store.StoreReadException;
import org.apache.tuscany.sca.store.StoreWriteException;

/**
 * A scope context which manages atomic component instances keyed on a
 * conversation session
 * 
 * @version $Rev: 452655 $ $Date: 2006-10-03 18:09:02 -0400 (Tue, 03 Oct 2006) $
 */
public class ConversationalScopeContainer<KEY> extends AbstractScopeContainer<KEY> {
    private final Store nonDurableStore;

    public ConversationalScopeContainer(Store store, RuntimeComponent component) {
        super(Scope.CONVERSATION, component);
        this.nonDurableStore = store;
        if (store != null) {
            store.addListener(new ExpirationListener());
        }
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

    public void persistNew(RuntimeComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
         try {
            nonDurableStore.insertRecord(component, id, instance, expiration);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(RuntimeComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
         try {
            nonDurableStore.updateRecord(component, id, instance, expiration);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove() throws PersistenceException {
        remove(component);
    }
    
    public void remove(RuntimeComponent component) throws PersistenceException {
         String conversationId = getConversationId();
        try {
//            workContext.setCurrentAtomicComponent(component);
            // FIXME this should be an InstanceWrapper and shouldn't we stop it?
            Object instance = nonDurableStore.readRecord(component, conversationId);
            if (instance != null) {
                nonDurableStore.removeRecord(component, conversationId);
            }
        } catch (StoreReadException e) {
            throw new PersistenceException(e);
        } catch (StoreWriteException e) {
            throw new PersistenceException(e);
        }
    }

    public InstanceWrapper getWrapper(KEY contextId) throws TargetResolutionException {
        
        boolean create = true; // FIXME
        
        String conversationId = getConversationId();
        try {
//            workContext.setCurrentAtomicComponent(component);
            InstanceWrapper wrapper = (InstanceWrapper)nonDurableStore.readRecord(component, conversationId);
            if (wrapper != null) {
//                if (component.getMaxIdleTime() > 0) {
//                    // update expiration
//                    long expire = System.currentTimeMillis() + component.getMaxIdleTime();
//                    nonDurableStore.updateRecord(component, conversationId, wrapper, expire);
//                }
            } else if (create) {
                // FIXME should the store really be persisting the wrappers
                wrapper = createInstanceWrapper();
                wrapper.start();
                long expire = calculateExpiration(component);
                nonDurableStore.insertRecord(component, conversationId, wrapper, expire);
            }
            return wrapper;
        } catch (StoreReadException e) {
            throw new TargetResolutionException("Error retrieving target instance", e);
        } catch (StoreWriteException e) {
            throw new TargetResolutionException("Error persisting target instance", e);
        } finally {
//            workContext.setCurrentAtomicComponent(null);
        }
    }

    /**
     * Returns the conversation id associated with the current invocation
     * context
     * 
     * @return the conversation id
     */
    private String getConversationId() {
        String conversationId = ThreadMessageContext.getMessageContext().getConversationID();
        assert conversationId != null;
        return conversationId;
    }

     private long calculateExpiration(RuntimeComponent component) {
//        if (component.getMaxAge() > 0) {
//            long now = System.currentTimeMillis();
//            return now + component.getMaxAge();
//        } else if (component.getMaxIdleTime() > 0) {
//            long now = System.currentTimeMillis();
//            return now + component.getMaxIdleTime();
//        } else {
            return Store.DEFAULT_EXPIRATION_OFFSET;
//        }
    }

    /**
     * Receives expiration events from the store and notifies the corresponding
     * atomic component
     */
    private static class ExpirationListener implements RuntimeEventListener {

        public ExpirationListener() {
        }

        public void onEvent(Event event) {
            if (event instanceof StoreExpirationEvent) {
                StoreExpirationEvent expiration = (StoreExpirationEvent)event;
                InstanceWrapper wrapper = (InstanceWrapper)expiration.getInstance();
                try {
                    wrapper.stop();
                } catch (TargetDestructionException e) {
                    // monitor.destructionError(e);
                }
            }
        }
    }
}
