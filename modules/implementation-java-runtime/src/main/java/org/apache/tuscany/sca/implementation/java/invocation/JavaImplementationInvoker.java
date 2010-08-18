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

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
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
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
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
public class JavaImplementationInvoker implements Invoker {
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
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        
        try {
            // The following call might create a new conversation, as a result, the msg.getConversationID() might 
            // return a new value
            ReflectiveInstanceWrapper wrapper = (ReflectiveInstanceWrapper) scopeContainer.getWrapper(contextId);
            
            // If there is a callback interface and the implementation is stateless, we need to
            // inject callbacks at invocation time. For Composite scope, this has already been done. 
            if (( interfaze.getCallbackInterface() != null )  && (scopeContainer.getScope().equals(Scope.STATELESS))){
            	injectCallbacks(wrapper, (JavaInterface)interfaze.getCallbackInterface());
            }
            
            Object instance = wrapper.getInstance();

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
            
            // Set the thread context classloader of the thread used to invoke an operation 
            // of a Java POJO component implementation is the class loader of the contribution 
            // that contains the POJO implementation class.
            
            Thread.currentThread().setContextClassLoader(instance.getClass().getClassLoader());
            
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = imethod.invoke(instance, payload);
            } else {
                ret = imethod.invoke(instance, (Object[])payload);
            }

            scopeContainer.returnWrapper(wrapper, contextId);
            
            msg.setBody(ret);
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
			

}
