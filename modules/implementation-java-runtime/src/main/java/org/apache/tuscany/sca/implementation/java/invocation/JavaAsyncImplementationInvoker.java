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

import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.AsyncResponseException;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.InterceptorAsync;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Responsible for asynchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 */
public class JavaAsyncImplementationInvoker extends JavaImplementationInvoker implements InterceptorAsync {
	
    public JavaAsyncImplementationInvoker(Operation operation, Method method, RuntimeComponent component, RuntimeComponentService service) {
    	super( operation, method, component, service);
        assert method != null : "Operation method cannot be null";
        assert ((JavaOperation) operation).isAsyncServer() : "Operation must be async";
    } // end constructor
    
    public Message invoke(Message msg) {
        Operation op = this.operation;
        
        Object payload = msg.getBody();

        // Save the current thread context classloader
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        
        try {
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            InstanceWrapper wrapper = scopeContainer.getWrapper(null);

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
            
            throw new InvocationTargetException( new AsyncResponseException("AsyncResponse") );

        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            boolean isChecked = false;
            for (DataType<?> d : operation.getFaultTypes()) {
                if (d.getPhysical().isInstance(cause)) {
                    isChecked = true;
                    msg.setFaultBody(cause);
                    break;
                }
            } // end for
            
            if (!isChecked) {
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new ServiceRuntimeException(cause.getMessage(), cause);
                }
            }  // end if          
                
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

    protected Invoker next;
    protected InvokerAsyncResponse previous;
	
	public void setNext(Invoker next) {
		this.next = next;
	}

	public Invoker getNext() {
		return next;
	}

	public void invokeAsyncRequest(Message msg) throws Throwable {
		processRequest(msg);
	} // end method invokeAsyncRequest

	public void invokeAsyncResponse(Message msg) {
		msg = processResponse(msg);
        InvokerAsyncResponse thePrevious = (InvokerAsyncResponse)getPrevious();
        if (thePrevious != null ) thePrevious.invokeAsyncResponse(msg);
	} // end method invokeAsyncResponse

	public void setPrevious(InvokerAsyncResponse previous) {
		this.previous = previous;
	}

	public InvokerAsyncResponse getPrevious() {
		return previous;
	}

	public Message processRequest(Message msg) {
        Operation op 	= this.operation;
        Object payload 	= msg.getBody();

        // Replace TCCL with the class loader used to load the java class as per SCA Spec
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        
        try {
            InstanceWrapper wrapper = scopeContainer.getWrapper(null);
            Object instance = wrapper.getInstance();
           
            // Set the TCCL to the classloader used to load the implementation class
            Thread.currentThread().setContextClassLoader(instance.getClass().getClassLoader());
            
            // For an async server method, there is an extra input parameter, which is a DispatchResponse instance 
            // which is typed by the type of the response
            Class<?> responseType = op.getOutputType().getPhysical();
            ResponseDispatch<?> dispatch = ResponseDispatchImpl.newInstance(responseType, msg );
            
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
            
            method.invoke(instance, (Object[])payload2);
            
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            boolean isChecked = false;
            for (DataType<?> d : operation.getFaultTypes()) {
                if (d.getPhysical().isInstance(cause)) {
                    isChecked = true;
                    // Ignore these errors since they should be returned asynchronously
                    break;
                }
            } // end for
            
            if (!isChecked) {
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                } // end if
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new ServiceRuntimeException(cause.getMessage(), cause);
                } // end if
            } // end if           
                
        } catch (Exception e) {
            throw new ServiceRuntimeException(e.getMessage(), e);        
        } finally {
            // set the tccl 
            Thread.currentThread().setContextClassLoader(tccl);
        }
        return msg;
	} // end method processRequest

	public Message postProcessRequest(Message msg) {
		return msg;
	}

	public Message postProcessRequest(Message msg, Throwable e)
			throws Throwable {
		throw e;
	}

	public Message processResponse(Message msg) {
		return msg;
	} // end method processResponse

} // end class JavaAsyncImplementationInvoker
