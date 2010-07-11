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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.AsyncResponseException;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Responsible for asynchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 */
public class JavaAsyncImplementationInvoker extends JavaImplementationInvoker {
	
    public JavaAsyncImplementationInvoker(Operation operation, Method method, RuntimeComponent component) {
    	super( operation, method, component);
        assert method != null : "Operation method cannot be null";
        assert ((JavaOperation) operation).isAsyncServer() : "Operation must be async";
    } // end constructor
    
    public Message invoke(Message msg) {
        Operation op = this.operation;
        
        Object payload = msg.getBody();

        Object contextId = null;

        // store the current thread context classloader
        // - replace it with the class loader used to load the java class as per SCA Spec
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        
        try {
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            InstanceWrapper wrapper = scopeContainer.getWrapper(contextId);

            Object instance = wrapper.getInstance();
           
            // Set the TCCL to the classloader used to load the implementation class
            Thread.currentThread().setContextClassLoader(instance.getClass().getClassLoader());
            
            // For an async server method, there is an extra input parameter, which is a DispatchResponse instance 
            // which is typed by the type of the response
            Class<?> responseType = op.getOutputType().getPhysical();
            ResponseDispatch<?> dispatch = ResponseDispatchImpl.newInstance(responseType, msg );
            
            Object ret;
            Object[] payload2;
            if (payload != null && !payload.getClass().isArray()) {
            	payload2 = new Object[2];
            	payload2[0] = payload;
            } else {
            	payload2 = new Object[ ((Object[])payload).length + 1 ];
            	for( int i = 0; i < ((Object[])payload).length; i++) {
            		payload2[i] = ((Object[])payload)[i];
            	} // end for
            }
            payload2[ payload2.length - 1 ] = dispatch;
            
            ret = method.invoke(instance, (Object[])payload2);
            
            //ret = ((ResponseDispatchImpl<?>)dispatch).get(50, TimeUnit.SECONDS);
            throw new InvocationTargetException( new AsyncResponseException("AsyncResponse") );

            //scopeContainer.returnWrapper(wrapper, contextId);
            
            //msg.setBody(ret);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            boolean isChecked = false;
            for (DataType<?> d : operation.getFaultTypes()) {
                if (d.getPhysical().isInstance(cause)) {
                    isChecked = true;
                    msg.setFaultBody(cause);
                    break;
                }
            } 
            
            if (!isChecked) {
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new ServiceRuntimeException(cause.getMessage(), cause);
                }
            }            
                
        } catch (ObjectCreationException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            msg.setFaultBody(e);           
        } finally {
            // set the tccl 
            Thread.currentThread().setContextClassLoader(tccl);
        }
        return msg;
    } // end method invoke

}
