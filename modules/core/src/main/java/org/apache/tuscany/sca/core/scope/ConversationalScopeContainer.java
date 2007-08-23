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

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.store.Store;
import org.osoa.sca.ConversationEndedException;

/**
 * A scope context which manages atomic component instances keyed on ConversationID
 *  
 */
public class ConversationalScopeContainer extends AbstractScopeContainer<Object> {      
        
    private Map<Object, InstanceLifeCycleWrapper> instanceLifecycleCollection = new ConcurrentHashMap<Object, InstanceLifeCycleWrapper>();
    
    //TODO: This needs to observe the value set by ConversationalAttributes for now we will hard code it. 
    private long max_age = 60 * 60 *  1000;  // 1 hour;
    private long max_idle_time = 60 * 60 *  1000;  // 1 hour;
    private long reaper_interval = 60;  // every minute; 
    private ScheduledExecutorService scheduler;
    
    public ConversationalScopeContainer(Store aStore, RuntimeComponent component) {
        super(Scope.CONVERSATION, component); 
        
        // Note: aStore is here to preserve the original factory interface. It is not currently used in this 
        // implemenation since we do not support instance persistence.
        
        // Check System properties to see if timeout values have been specified. All timeout values 
        // will be specifed in seconds.
        //
        String aProperty; 
        aProperty = System.getProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.MaxIdleTime");
        if (aProperty != null)
          try 
           {
                max_idle_time = (new Long(aProperty) * 1000);   
           }
           catch (NumberFormatException nfe) {};
           
        aProperty = System.getProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.MaxAge");
        if (aProperty != null)
            try 
             {
                  max_age = (new Long(aProperty) * 1000);   
             }
             catch (NumberFormatException nfe) {};
             
        aProperty = System.getProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.ReaperInterval");
        if (aProperty != null)
            try 
             {
                  reaper_interval = new Long(aProperty);   
             }
             catch (NumberFormatException nfe) {};
           
             
        // Check to see if the maxAge and/or maxIdleTime have been specified using @ConversationAttributes.  
        // Implementation annoated attributes are honored first.
        if (this.getComponent().getImplementationProvider() instanceof ScopedImplementationProvider) 
         {
            ScopedImplementationProvider aScopedImpl = (ScopedImplementationProvider) this.getComponent().getImplementationProvider();
            
            long maxAge = aScopedImpl.getMaxAge();
            if (maxAge > 0) {
                max_age = maxAge;
            }
            long maxIdleTime = aScopedImpl.getMaxIdleTime();
            if (maxIdleTime > 0 ) {
                max_idle_time = maxIdleTime;
            }
         }                      

    }    
    

    @Override
    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }

        // Get a scheduler and scheduled a task to be run in the future indefinitely until its explicitly shutdown. 
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new ConversationalInstanceReaper(this.instanceLifecycleCollection), 3, reaper_interval, TimeUnit.SECONDS);
        
        lifecycleState = RUNNING;        
       }

    @Override
    public synchronized void stop() {
        
        // Prevent the scheduler from submitting any additional reapers, initiate an orderly shutdown if a reaper task is in progress. 
        if (this.scheduler != null) 
                this.scheduler.shutdown(); 
        
        lifecycleState = STOPPED;
    }

    protected InstanceWrapper getInstanceWrapper(boolean create,Object contextId) throws TargetResolutionException {    
        
        // we might get a null context if the target service has
        // conversational scope but only its callback interface 
        // is conversational. In this case we need to invent a 
        // conversation Id here to store the service against
        // and populate the thread context
        if (contextId == null){
            contextId = UUID.randomUUID().toString();
            Message msgContext = ThreadMessageContext.getMessageContext();
            
            if (msgContext != null){
                msgContext.setConversationID(contextId.toString());
            }
            
        }
        
        InstanceLifeCycleWrapper anInstanceWrapper = this.instanceLifecycleCollection.get(contextId);
        
        if (anInstanceWrapper == null && !create)
                return null;
        
        if (anInstanceWrapper == null) 
        {
            anInstanceWrapper = new InstanceLifeCycleWrapper(contextId);  
            this.instanceLifecycleCollection.put(contextId, anInstanceWrapper);
        }
        // If an existing intsance is found return it only if its not expired and update its 
        // last referenced time. 
        else
        {
          if (anInstanceWrapper.isExpired())
             throw new ConversationEndedException();
          anInstanceWrapper.updateLastReferencedTime();
        }
        
        return anInstanceWrapper.getInstanceWrapper();          
                      
    }
    
    @Override
    public InstanceWrapper getWrapper(Object contextId) throws TargetResolutionException {
        return getInstanceWrapper(true,contextId);
    } 
    
    
    // The remove is invoked when a conversation is explicitly ended.  This can occur by using the @EndsConversation or API.  
    // In this case the instance is immediately removed.  A new conversation will be started on the next operation
    // associated with this conversationId's service reference. 
    //
    @Override
    public void remove(Object contextId) throws TargetDestructionException {
        if (this.instanceLifecycleCollection.containsKey(contextId)) 
        {
         InstanceLifeCycleWrapper anInstanceLifeCycleWrapper = this.instanceLifecycleCollection.get(contextId);
         this.instanceLifecycleCollection.remove(contextId);
         anInstanceLifeCycleWrapper.removeInstanceWrapper();
        } 
    }  
       
    
    /*
     *  This is an inner class that keeps track of the lifecycle of a conversation scoped
     *  implementation instance.   
     * 
     */
    
    private class InstanceLifeCycleWrapper 
    {
        private Object instanceId;
        private long   creationTime;
        private long   lastReferencedTime;
        private long   expirationInterval;
        private long   maxIdleTime;
        
        private InstanceLifeCycleWrapper(Object contextId) throws TargetResolutionException
        {
         this.instanceId = contextId;
         this.creationTime = System.currentTimeMillis();
         this.lastReferencedTime = this.creationTime;
         this.expirationInterval = max_age;
         this.maxIdleTime = max_idle_time;
         this.createInstance();
        }
        
        private boolean isExpired() 
        {               
         long currentTime = System.currentTimeMillis(); 
         if ((this.lastReferencedTime +  this.maxIdleTime) < currentTime) // max idle time exceeded
             return true;
         if ((this.creationTime + this.expirationInterval) < currentTime) // max time to live exceeded
             return true;
             
         return false;
        }
        
        private void updateLastReferencedTime() 
        {
         this.lastReferencedTime = System.currentTimeMillis();
        }
        
        //
        // Return the backing implementation instance  
        //
        private InstanceWrapper getInstanceWrapper()
        {
          InstanceWrapper ctx = wrappers.get(this.instanceId);
          return ctx;
        }
        
        private void removeInstanceWrapper() throws TargetDestructionException 
        {
          InstanceWrapper ctx =  getInstanceWrapper();
          ctx.stop();
          wrappers.remove(this.instanceId);       
        }
        
        private void createInstance() throws TargetResolutionException 
        {
            InstanceWrapper instanceWrapper = createInstanceWrapper();
            instanceWrapper.start();
            wrappers.put(this.instanceId, instanceWrapper);       
        }
        
    }
    
    //
    // This inner class is an instance reaper.  It periodically iterates over the InstanceLifeCycleCollection
    // and for any instances that have expired removes the backing instance and the entry in the InstanceLifeCycle 
    // Collection.
    //
    class ConversationalInstanceReaper implements Runnable 
    {
        private Map<Object, InstanceLifeCycleWrapper> instanceLifecycleCollection;
        
        public ConversationalInstanceReaper(Map<Object, InstanceLifeCycleWrapper> aMap)
        {
         this.instanceLifecycleCollection = aMap;
        }
        
        public void run()
        {
          Iterator<Map.Entry<Object,InstanceLifeCycleWrapper>> anIterator = this.instanceLifecycleCollection.entrySet().iterator();             
        
          while (anIterator.hasNext())
          {
                Map.Entry<Object,InstanceLifeCycleWrapper> anEntry = anIterator.next();   
            InstanceLifeCycleWrapper anInstanceLifeCycleWrapper = anEntry.getValue();
            if (anInstanceLifeCycleWrapper.isExpired())
            {
              try {
                  anInstanceLifeCycleWrapper.removeInstanceWrapper();
                  this.instanceLifecycleCollection.remove(anInstanceLifeCycleWrapper.instanceId);
              } catch (Exception ex) {
                  // TODO - what to do with any asynchronous exceptions?
              }
            }
          }             
        }
    }
}
