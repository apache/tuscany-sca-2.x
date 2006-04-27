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
package org.apache.tuscany.core.async.invocation;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.wire.Interceptor;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A wire interceptor that uses a WorkManager to schedule asynchronous execution of invocations in Work instances.
 */
public class AsyncInterceptor implements Interceptor {
    
    private static final ContextBinder BINDER = new ContextBinder();
    
    private WorkManager workManager;
    private MessageFactory messageFactory;
    private Interceptor next;

    /**
     * Constructs a new instance
     * @param workManager
     */
    public AsyncInterceptor(WorkManager workManager, MessageFactory messageFactory) {
        this.workManager=workManager;
        this.messageFactory=messageFactory;
    }
    
    public Message invoke(final Message message) {

        final ModuleContext currentModuleContext=CurrentModuleContext.getContext();
        
        // Schedule the invocation of the next interceptor in a new Work instance
        try {
            workManager.scheduleWork(new Work() {
                
                public void run() {
                    ModuleContext oldModuleContext=CurrentModuleContext.getContext();
                    try {
                        BINDER.setContext(currentModuleContext);
                        
                        // Invoke the next interceptor
                        next.invoke(message);
                        
                    } catch (Exception e) {
                        //FIXME How do we report exceptions?
                        e.printStackTrace();

                    } finally {
                        
                        BINDER.setContext(oldModuleContext);
                    }
                }
                
                public void release() {
                }
                
            });
        } catch (WorkException e) {
            //FIXME Which exception should we throw here? 
            throw new ServiceRuntimeException(e);
        }
        
        // No return on a OneWay invocation.
        return messageFactory.createMessage();
    }

    public void setNext(Interceptor next) {
        this.next=next;
    }

    private static class ContextBinder extends SCA {
        public void setContext(ModuleContext context) {
            setModuleContext(context);
        }

        public void start() {
            throw new AssertionError();
        }

        public void stop() {
            throw new AssertionError();
        }
    }
    
}
