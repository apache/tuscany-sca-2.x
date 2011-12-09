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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.Constants;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.context.ReflectiveInstanceWrapper;
import org.apache.tuscany.sca.implementation.java.injection.Injector;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.ParameterMode;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 * @version $Rev$ $Date$
 */
public class JavaImplementationInvoker implements Invoker, DataExchangeSemantics {
    protected Operation operation;
    protected Method method;
    protected boolean allowsPBR;

    @SuppressWarnings("unchecked")
    protected final ScopeContainer scopeContainer;
	private final InterfaceContract interfaze;

    public JavaImplementationInvoker(Operation operation, Method method, RuntimeComponent component, InterfaceContract intf) {
        assert method != null : "Operation method cannot be null";
        this.method = method;
        this.operation = operation;
        this.scopeContainer = ((ScopedRuntimeComponent)component).getScopeContainer();
        this.allowsPBR = ((JavaImplementation)component.getImplementation()).isAllowsPassByReference(method);
        this.interfaze = intf;
    }

    public JavaImplementationInvoker(Operation operation, RuntimeComponent component, InterfaceContract intf) {
        // used if the method can't be computed statically in advance 
        this.operation = operation;
        this.scopeContainer = ((ScopedRuntimeComponent)component).getScopeContainer();
        this.interfaze = intf;
    }

    @SuppressWarnings("unchecked")
    public Message invoke(Message msg) {
        
        Operation op = msg.getOperation();
        if (op == null) {
            op = this.operation;
        }
        Object payload = msg.getBody();

        Object contextId = null;

        EndpointReference from = msg.getFrom();

        // store the current thread context classloader
        // as we need to replace it with the class loader
        // used to load the java class as per SCA Spec
        final ClassLoader tccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                    return tccl;
                 }
            });
        
        // TUSCANY-3946 - If the TCCL has not already been set to the contribution classloader earlier
        // in the wire processing then
        // set it so that the thread context classloader of the thread used to invoke an operation 
        // of a Java POJO component implementation is the class loader of the contribution 
        // used to load the POJO implementation class.
        boolean swapTCCL = (msg.getHeaders().get(Constants.SUPPRESS_TCCL_SWAP) == null);
        
        try {
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            ReflectiveInstanceWrapper wrapper = (ReflectiveInstanceWrapper) scopeContainer.getWrapper(contextId);
            
            // If there is a callback interface and the implementation is stateless, we need to
            // inject callbacks at invocation time. For Composite scope, this has already been done. 
            if (( interfaze.getCallbackInterface() != null )  && (scopeContainer.getScope().equals(Scope.STATELESS))){
            	injectCallbacks(wrapper, (JavaInterface)interfaze.getCallbackInterface());
            }
            
            final Object instance = wrapper.getInstance();

            // If the method couldn't be computed statically, or the instance being
            // invoked is a user-specified callback object that doesn't implement
            // the service interface from which the reflective method was obtained,
            // compute the method object dynamically for this invocation.
            Method imethod = method;
            if (imethod == null || !imethod.getDeclaringClass().isInstance(instance)) {
                try {
                    imethod = JavaInterfaceUtil.findMethod(instance.getClass(), operation);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Callback object does not provide method " + e.getMessage());
                }
            }
            
            if (swapTCCL){
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        Thread.currentThread().setContextClassLoader(instance.getClass().getClassLoader());
                        return null;
                     }
                });
            }
            
            int argumentHolderCount = 0;

            // Holder pattern. Any payload parameters <T> which are should be in holders are placed in Holder<T>.
            // Only check Holder for remotable interfaces
            if (imethod != null && op.getInterface().isRemotable()) {
                Object[] payloadArray = (Object[])payload;
                List<Object> payloadList = new ArrayList<Object>();
                int nextIndex = 0;
                for (ParameterMode mode : op.getParameterModes()) {                    
                    if (mode.equals(ParameterMode.IN)) {
                        payloadList.add(payloadArray[nextIndex++]);
                    } else if (mode.equals(ParameterMode.INOUT)) {
                        // Promote array params from [<T>] to [Holder<T>]                  
                        Object item = payloadArray[nextIndex++];                           
                        Holder itemHolder = new Holder(item);
                        payloadList.add(itemHolder);
                        argumentHolderCount++;
                    } else {
                        // Create an empty Holder since we should not pass values for OUT parameters
                        payloadList.add(new Holder());
                        argumentHolderCount++;
                    }                        
                }
                
                // Maybe a bit odd to do but this way I don't have to worry about how the invoke if/else
                // immediately following might need to be changed.
                payload = payloadList.toArray();
            }

            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = imethod.invoke(instance, payload);
            } else {
                ret = imethod.invoke(instance, (Object[])payload);
            }

            scopeContainer.returnWrapper(wrapper, contextId);
            
                        
            if (argumentHolderCount > 0) {
            	
                // Holder pattern. Any payload Holder<T> types are returned as part of the message body.
            	Object[] payloadArray = (Object[])payload;
            	
            	ArrayList<Object> holderOutputs = new ArrayList<Object>();
            	ArrayList<Object> result = new ArrayList<Object>();
                if (imethod != null) {                	
                    
                    for (int i = 0, size = op.getParameterModes().size(); i < size; i++) {                       
                        if (ParameterMode.IN != op.getParameterModes().get(i)) {                        	
                        	// Demote array params from Holder<T> to <T>.                                                   
                        	Holder<Object> item = (Holder<Object>)payloadArray[i];
                        	payloadArray[i] = item.value;
                        	holderOutputs.add(payloadArray[i]);                        	                      
                        }
                    }
                    
                    //
                    // Now we account for the fact that we may have a null because of a void return type,
                    // which is not part of the output DataType, and so should not be returned with the array
                    // of outputs, or we may have a null as value returned
                    // from a method with signature with return type other than void, which should be returned 
                    // in the output array.
                    // 
                    // The logic here is if we already have as many outputs in holders as we have outputs
                    // altogether, then we don't worry about the return value (which should be null).  Might
                    // be simpler to just check for void, but the code in the Java introspector has a lot
                    // of quirks for handling parameterized types, and this seems simpler for now.
                    //
                    int holderOutputSize = holderOutputs.size();
                    int numberOperationOutputs = op.getOutputType().getLogical().size();
                    if (holderOutputSize == numberOperationOutputs) {
                        if (ret != null) {
                            throw new IllegalStateException("Number of holder outputs equal to number of operations outputs." +
                                                            "\nNum = " + holderOutputSize + ", but non-null return value seen: " + ret);
                        }
                        result = holderOutputs;
                    } else if (holderOutputSize == numberOperationOutputs - 1) {
                        result.add(ret);
                        result.addAll(1, holderOutputs);
                    } else {
                        throw new IllegalStateException("Number of holder outputs seen: " + holderOutputSize +  
                                                        "\nNumber of operation outputs: " + numberOperationOutputs);
                    }
                }

                msg.setBody(result.toArray());

            } else {
                msg.setBody(ret);
            }
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
            // reset the tccl if it was replaced above
            // with the contribution classloader
            if (swapTCCL){
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        Thread.currentThread().setContextClassLoader(tccl);
                        return null;
                     }
                });
            }
        }
        return msg;
    }

	private void injectCallbacks(ReflectiveInstanceWrapper wrapper,
			JavaInterface callbackInterface) {
	
		for (Injector injector : wrapper.getCallbackInjectors()) {
			if (injector != null) {
				try {       
					if (ServiceReference.class.isAssignableFrom(injector.getType())) {
						Class<?> intf = JavaIntrospectionHelper.getBusinessInterface(injector.getType(), injector.getGenericType());
						if ( intf.isAssignableFrom(callbackInterface.getJavaClass())) {              		                		                		
							injector.inject(wrapper.getInstance());         
						}
					} else if (injector.getType().isAssignableFrom(callbackInterface.getJavaClass())) {
						injector.inject(wrapper.getInstance());
					} else {
						injector.injectNull(wrapper.getInstance());
					}
				} catch (Exception e) {	                   
					throw new ObjectCreationException("Exception invoking injector - " + e.getMessage(), e);
				}
			}
		}
		
	}

    @Override
    public boolean allowsPassByReference() {
        return allowsPBR;
    }		

}
