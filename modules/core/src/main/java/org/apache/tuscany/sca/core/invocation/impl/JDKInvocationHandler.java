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

package org.apache.tuscany.sca.core.invocation.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.core.context.ServiceReferenceExt;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -3366410500152201371L;

    protected MessageFactory messageFactory;
    protected Endpoint target;
    protected Invocable source;
    protected ServiceReferenceExt<?> callableReference;
    protected Class<?> businessInterface;

    protected boolean fixedWire = true;

    protected transient Map<Method, InvocationChain> chains = new IdentityHashMap<Method, InvocationChain>();

    public JDKInvocationHandler(MessageFactory messageFactory, Class<?> businessInterface, Invocable source) {
        this.messageFactory = messageFactory;
        this.source = source;
        this.businessInterface = businessInterface;
    }

    public JDKInvocationHandler(MessageFactory messageFactory, ServiceReference<?> callableReference) {
        this.messageFactory = messageFactory;
        this.callableReference = (ServiceReferenceExt<?>)callableReference;
        if (callableReference != null) {
            this.businessInterface = callableReference.getBusinessInterface();
            this.source = (RuntimeEndpointReference) this.callableReference.getEndpointReference();
        }
    }


    public Class<?> getBusinessInterface() {
        return businessInterface;
    }
    
    protected Object getCallbackID() {
            return null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }
        if (source == null) {
            throw new ServiceRuntimeException("No runtime source is available");
        }
        
        if (source instanceof RuntimeEndpointReference) {
            RuntimeEndpointReference epr = (RuntimeEndpointReference)source;
            if (epr.isOutOfDate()) {
                epr.rebuild();
                chains.clear();
            }
        }
        
        InvocationChain chain = getInvocationChain(method, source);
        
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }        

        // Holder pattern. Items stored in a Holder<T> are promoted to T.
        // After the invoke, the returned data <T> are placed back in Holder<T>.
        Object [] promotedArgs = promoteHolderArgs( args );
                
        Object result = invoke(chain, promotedArgs, source);
        
        // Returned Holder data <T> are placed back in Holder<T>.
        boolean holderPattern = false;
        Class [] parameters = method.getParameterTypes();
        if ( parameters != null ) {
        	for ( int i = 0, resultIdx = 0; i < parameters.length; i++ ) {
        		Class parameterType = parameters[ i ];              
        		if ( isHolder( parameterType ) ) {
        			holderPattern = true;
        			// Pop results and place in holder (demote).
        			Holder holder = (Holder) args[ i ]; 
        			Object[] resultArray = (Object[])result;
        			holder.value = resultArray[++resultIdx];
        		}            
        	}
        }
        if ( holderPattern ) 
        	return ((Object[])result)[0];
        else
        	return result;
    }

    /**
     * Handle the methods on the Object.class
     * @param method
     * @param args
     */
    protected Object invokeObjectMethod(Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("toString".equals(name)) {
            return "[Proxy - " + toString() + "]";
        } else if ("equals".equals(name)) {
            Object obj = args[0];
            if (obj == null) {
                return false;
            }
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            return equals(Proxy.getInvocationHandler(obj));
        } else if ("hashCode".equals(name)) {
            return hashCode();
        } else {
            return method.invoke(this);
        }
    }

    /**
     * Determines if the given operation matches the given method
     * 
     * @return true if the operation matches, false if does not
     */
    // FIXME: Should it be in the InterfaceContractMapper?
    @SuppressWarnings("unchecked")
    private static boolean match(Operation operation, Method method) {
        if (operation instanceof JavaOperation) {
            JavaOperation javaOp = (JavaOperation)operation;
            Method m = javaOp.getJavaMethod();
            if (!method.getName().equals(m.getName())) {
                return false;
            }
            if (method.equals(m)) {
                return true;
            }
        } else {
            if (!method.getName().equals(operation.getName())) {
                return false;
            }
        }

        // For remotable interface, operation is not overloaded. 
        if (operation.getInterface().isRemotable()) {
            return true;
        }

        Class<?>[] params = method.getParameterTypes();

        DataType<List<DataType>> inputType = null;
        if (operation.isWrapperStyle()) {
            inputType = operation.getWrapper().getUnwrappedInputType();
        } else {
            inputType = operation.getInputType();
        }
        List<DataType> types = inputType.getLogical();
        boolean matched = true;
        if (types.size() == params.length && method.getName().equals(operation.getName())) {
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i];
                Class<?> type = types.get(i).getPhysical();
                // Object.class.isAssignableFrom(int.class) returns false
                if (type != Object.class && (!type.isAssignableFrom(clazz))) {
                    matched = false;
                }
            }
        } else {
            matched = false;
        }
        return matched;

    }

    protected synchronized InvocationChain getInvocationChain(Method method, Invocable source) {
        if (source instanceof RuntimeEndpoint) {
            InvocationChain invocationChain = source.getBindingInvocationChain();
            for (InvocationChain chain : source.getInvocationChains()) {
                Operation operation = chain.getTargetOperation();
                if (method.getName().equals(operation.getName())) {
                    invocationChain.setTargetOperation(operation);
                }
            }
            return source.getBindingInvocationChain();
        }
        if (fixedWire && chains.containsKey(method)) {
            return chains.get(method);
        }
        InvocationChain found = null;
        for (InvocationChain chain : source.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();
            if (operation.isDynamic()) {
                operation.setName(method.getName());
                found = chain;
                break;
            } else if (match(operation, method)) {
                found = chain;
                break;
            }
        }
        if (fixedWire) {
            chains.put(method, found);
        }
        return found;
    }

    protected void setEndpoint(Endpoint endpoint) {
        this.target = endpoint;
    }
    
    protected Object invoke(InvocationChain chain, Object[] args, Invocable source)
                            throws Throwable {
    	return invoke( chain, args, source, null );
    }

    /**
     * Invoke the chain
     * @param chain - the chain
     * @param args - arguments to the invocation as an array of Objects
     * @param source - the Endpoint or EndpointReference to which the chain relates
     * @param msgID - an ID for the message being sent, may be null
     * @return - the Response message from the invocation
     * @throws Throwable - if any exception occurs during the invocation
     */
    protected Object invoke(InvocationChain chain, Object[] args, Invocable source, String msgID)
                         throws Throwable {
        Message msg = messageFactory.createMessage();
        if (source instanceof RuntimeEndpointReference) {
            msg.setFrom((RuntimeEndpointReference)source);
        }
        if (target != null) {
            msg.setTo(target);
        } else {
            if (source instanceof RuntimeEndpointReference) {
                msg.setTo(((RuntimeEndpointReference)source).getTargetEndpoint());
            }
        }
        Invoker headInvoker = chain.getHeadInvoker();
        Operation operation = chain.getTargetOperation();
        msg.setOperation(operation);
        msg.setBody(args);

        Message msgContext = ThreadMessageContext.getMessageContext();
        
        // Deal with header information that needs to be copied from the message context to the new message...
        transferMessageHeaders( msg, msgContext);
        
        ThreadMessageContext.setMessageContext(msg);
        
        // If there is a supplied message ID, place its value into the Message Header under "MESSAGE_ID"
        if( msgID != null ){
        	msg.getHeaders().put("MESSAGE_ID", msgID);
        } // end if

        try {
            // dispatch the source down the chain and get the response
            Message resp = headInvoker.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable)body;
            }
            return body;
        } finally {
            ThreadMessageContext.setMessageContext(msgContext);
        }
    }
    
    /**
     * Transfer relevant header information from the old message (incoming) to the new message (outgoing)
     * @param newMsg
     * @param oldMsg
     */
    private void transferMessageHeaders( Message newMsg, Message oldMsg ) {
    	if( oldMsg == null ) return;
    	// For the present, simply copy all the headers 
    	if( !oldMsg.getHeaders().isEmpty() ) newMsg.getHeaders().putAll( oldMsg.getHeaders() );
    } // end transferMessageHeaders

    /**
     * @return the callableReference
     */
    public ServiceReference<?> getCallableReference() {
        return callableReference;
    }

    /**
     * @param callableReference the callableReference to set
     */
    public void setCallableReference(ServiceReference<?> callableReference) {
        this.callableReference = (ServiceReferenceExt<?>)callableReference;
    }
            
    /**
     * Creates a copy of arguments. Holder<T> values are promoted to T.
     * Note. It is essential that arg Holders not be destroyed here.
     * PromotedArgs should not destroy holders. They are used on response return.
     * @param args containing Holders and other objects.
     * @return Object [] 
     */
    protected static Object [] promoteHolderArgs( Object [] args ) {
    	if ( args == null )
    		return args;
    	Object [] promotedArgs = new Object[ args.length ];
    	
    	for ( int i = 0; i < args.length; i++ ) {
    		Object argument = args[ i ];
    		if ( argument != null ) {
    			if ( isHolder( argument ) ) {
    				promotedArgs[ i ] = ((Holder)argument).value;      
    			} else {
    				promotedArgs[ i ] = args[ i ];
    			}
                  
    		}
    	}
    	return promotedArgs;
    }
    
    /**
     * Given a Class, tells if it is a Holder by comparing to "javax.xml.ws.Holder"
     * @param testClass
     * @return boolean whether class is Holder type.
     */
    protected static boolean isHolder( Class testClass ) {
    	if ( testClass.getName().startsWith( "javax.xml.ws.Holder" )) {
    		return true;
    	}
    	return false;        
    }
    
         
    /**
     * Given an Object, tells if it is a Holder by comparing to "javax.xml.ws.Holder"
     * @param testClass
     * @return boolean stating whether Object is a Holder type.
     * @author DOB
     */
    protected static boolean isHolder( Object object ) {
    	String objectName = object.getClass().getName();
    	if ( object instanceof javax.xml.ws.Holder ) {
    		return true;
    	}
    	return false;        
    }
        
}
