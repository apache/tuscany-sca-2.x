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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.conversation.ConversationListener;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.conversation.ExtendedConversation;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.store.Store;

/**
 * A scope context which manages atomic component instances keyed on ConversationID
 *
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainer extends AbstractScopeContainer<Object> implements ConversationListener {
    private ConversationManager conversationManager;
    private Map<Object, InstanceLifeCycleWrapper> instanceLifecycleCollection =
        new ConcurrentHashMap<Object, InstanceLifeCycleWrapper>();

    public ConversationalScopeContainer(Store aStore, RuntimeComponent component) {
        super(Scope.CONVERSATION, component);

        // Note: aStore is here to preserve the original factory interface. It is not currently used in this 
        // implementation since we do not support instance persistence.

        // Check System properties to see if timeout values have been specified. All timeout values 
        // will be specified in seconds.
        //

    }


    protected InstanceWrapper getInstanceWrapper(boolean create, Object contextId) throws TargetResolutionException {

        // we might get a null context if the target service has
        // conversational scope but only its callback interface 
        // is conversational. In this case we need to invent a 
        // conversation Id here to store the service against
        // and populate the thread context
        if (contextId == null) {
            contextId = UUID.randomUUID().toString();
            Message msgContext = ThreadMessageContext.getMessageContext();

            if (msgContext != null) {
                msgContext.getFrom().getReferenceParameters().setConversationID(contextId);
            }
        }    
        
        InstanceLifeCycleWrapper anInstanceWrapper = this.instanceLifecycleCollection.get(contextId);

        if (anInstanceWrapper == null && !create)
            return null;

        if (anInstanceWrapper == null) {
            anInstanceWrapper = new InstanceLifeCycleWrapper(contextId);
            this.instanceLifecycleCollection.put(contextId, anInstanceWrapper);
        }

        return anInstanceWrapper.getInstanceWrapper(contextId);

    }

    @Override
    public InstanceWrapper getWrapper(Object contextId) throws TargetResolutionException {
        return getInstanceWrapper(true, contextId);
    }

    /**
     * This method allows a new context id to be registered alongside an existing one. This happens in
     * one case, when a conversation includes a stateful callback. The client component instance
 	 * must be registered against all outgoing conversation ids so that the component instance 
	 * can be found when the callback arrives
	 * 
     * @param existingContextId the context id against which the component is already registered
     * @param context this should be a conversation object so that the conversation can b stored 
     *                and reset when the component instance is removed
     */
    @Override
    public void addWrapperReference(Object existingContextId, Object contextId) throws TargetResolutionException {
       
        
        // get the instance wrapper via the existing id
        InstanceLifeCycleWrapper existingInstanceWrapper = this.instanceLifecycleCollection.get(existingContextId);
        InstanceLifeCycleWrapper newInstanceWrapper = this.instanceLifecycleCollection.get(contextId);

        // only add the extra reference once
        if (newInstanceWrapper == null) {
            // add the id to the list of ids that the wrapper holds. Used for reference
            // counting and conversation resetting on destruction. 
            existingInstanceWrapper.addCallbackConversation(contextId);

            // add the reference to the collection
            this.instanceLifecycleCollection.put(contextId, existingInstanceWrapper);
        }
    }

    @Override
    public void registerWrapper(InstanceWrapper wrapper, Object contextId) throws TargetResolutionException {
        // if a wrapper for a different instance is already registered for this contextId, remove it
        InstanceLifeCycleWrapper anInstanceWrapper = this.instanceLifecycleCollection.get(contextId);
        if (anInstanceWrapper != null) {
            if (anInstanceWrapper.getInstanceWrapper(contextId).getInstance() != wrapper.getInstance()) {
                remove(contextId);
            } else {
                return;
            }
        }

        anInstanceWrapper = new InstanceLifeCycleWrapper(wrapper, contextId);
        this.instanceLifecycleCollection.put(contextId, anInstanceWrapper);
    }

    // The remove is invoked when a conversation is explicitly ended. This can occur by using the @EndsConversation or API.  
    // In this case the instance is immediately removed. A new conversation will be started on the next operation
    // associated with this conversationId's service reference. 
    //
    @Override
    public void remove(Object contextId) throws TargetDestructionException {
        if (contextId != null) {
            if (this.instanceLifecycleCollection.containsKey(contextId)) {
                InstanceLifeCycleWrapper anInstanceLifeCycleWrapper = this.instanceLifecycleCollection.get(contextId);
                this.instanceLifecycleCollection.remove(contextId);
                anInstanceLifeCycleWrapper.removeInstanceWrapper(contextId);
            }
        }
    }

    /*
     *  This is an inner class that keeps track of the lifecycle of a conversation scoped 
	 *  implementation instance. 
	 * 
	 */

    private class InstanceLifeCycleWrapper {
        private Object clientConversationId;
        private List<Object> callbackConversations = new ArrayList<Object>();

        private InstanceLifeCycleWrapper(Object contextId) throws TargetResolutionException {
            this.clientConversationId = contextId;
            this.createInstance(contextId);
        }

        private InstanceLifeCycleWrapper(InstanceWrapper wrapper, Object contextId) throws TargetResolutionException {
            this.clientConversationId = contextId;
            wrappers.put(contextId, wrapper);
        }


        // Associates a callback conversation with this instance. Each time the scope container
        // is asked to remove an object given a ontextId an associated conversation object will 
        // have its conversationId reset to null. When the list of ids is empty the component instance
        // will be removed from the scope container
        private void addCallbackConversation(Object conversationID) {
            InstanceWrapper ctx = getInstanceWrapper(clientConversationId);
            callbackConversations.add(conversationID);
            wrappers.put(conversationID, ctx);
        }

        //
        // Return the backing implementation instance  
        //
        private InstanceWrapper getInstanceWrapper(Object contextId) {
            InstanceWrapper ctx = wrappers.get(contextId);
            return ctx;
        }

        private void removeInstanceWrapper(Object contextId) throws TargetDestructionException {
            InstanceWrapper ctx = getInstanceWrapper(contextId);
            wrappers.remove(contextId);

            // find out if we are dealing with the original client conversation id
            // and reset accordingly
            if ( ( clientConversationId != null ) && ( clientConversationId.equals(contextId)) ) {
                clientConversationId = null;
            } else {
                // reset the conversationId in the conversation object if present 
                // so that and ending callback causes the conversation in the originating
                // service reference in the client to be reset
                callbackConversations.remove(contextId);
            }

            // stop the component if this removes the last reference
            if (clientConversationId == null && callbackConversations.isEmpty()) {
                ctx.stop();
            }
        }

        private void createInstance(Object contextId) throws TargetResolutionException {
            InstanceWrapper instanceWrapper = createInstanceWrapper();
            instanceWrapper.start();
            wrappers.put(contextId, instanceWrapper);
        }

    }

    /**
	  * @see org.apache.tuscany.sca.core.conversation.ConversationListener#conversationEnded(org.apache.tuscany.sca.core.conversation.ExtendedConversation)
	  */
    public void conversationEnded(ExtendedConversation conversation) {
        try {
            remove(conversation.getConversationID());
        } catch (Exception ex) {
            
        }
    }

    /**
	  * @see org.apache.tuscany.sca.core.conversation.ConversationListener#conversationExpired(org.apache.tuscany.sca.core.conversation.ExtendedConversation)
	  */
    public void conversationExpired(ExtendedConversation conversation) {
    	
    	Object conversationId = conversation.getConversationID();
    	InstanceLifeCycleWrapper ilcw = instanceLifecycleCollection.get(conversationId);
    	if (ilcw != null) {
    		// cycle through all the references to this instance and
    		// remove them from the underlying wrappers collection and
    		// from the lifecycle wrappers collection
    		
    		for (Object conversationID : ilcw.callbackConversations) {
    			try{
        			ilcw.removeInstanceWrapper(conversationID);
    				remove(conversationID);
                } catch(TargetDestructionException tde) {
    				System.out.println("Could not remove conversation id " + conversationID);
    			}
    		}
    			
    		
    		if (ilcw.clientConversationId != null) {
    			try{
        			ilcw.removeInstanceWrapper(ilcw.clientConversationId);
    				remove(ilcw.clientConversationId);
                } catch(TargetDestructionException tde) {
    				System.out.println("Could not remove conversation id " + ilcw.clientConversationId);
    			}
    		}
    		
        }
    	
    }

    /**
	  * @see org.apache.tuscany.sca.core.conversation.ConversationListener#conversationStarted(org.apache.tuscany.sca.core.conversation.ExtendedConversation)
	  */
    public void conversationStarted(ExtendedConversation conversation) {
        startContext(conversation.getConversationID());
    }

    /**
	  * @return the conversationManager
	  */
    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    /**
	  * @param conversationManager the conversationManager to set
	  */
    public void setConversationManager(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

}
