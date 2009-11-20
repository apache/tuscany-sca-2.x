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
package org.apache.tuscany.sca.binding.jms.provider;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * MessageProcessor for sending/receiving Serializable objects with the JMSBinding.
 * 
 */
public class ObjectMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = Logger.getLogger(ObjectMessageProcessor.class.getName());

    public ObjectMessageProcessor(JMSBinding jmsBinding, ExtensionPointRegistry registry) {
        super(jmsBinding);
    }

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        if (session == null) {
            logger.fine("no response session to create message: " + String.valueOf(o));
            return null;
        }
        try {

            ObjectMessage message = session.createObjectMessage();
            
            if (o != null){
                if (!(o instanceof Serializable)) {
                    throw new IllegalStateException("JMS ObjectMessage payload not Serializable: " + o);
                }
    
                message.setObject((Serializable)o);
            }
            
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    @Override
    public Object extractPayloadFromJMSMessage(Message msg) {
        try {
            Object o = ((ObjectMessage)msg).getObject();
            if (o instanceof Throwable ) {
                if (o instanceof RuntimeException) {
                    throw new ServiceRuntimeException("remote service exception, see nested exception", (RuntimeException)o);
                } else {
                    return new InvocationTargetException((Throwable) o);
                }
            }
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
        return extractPayload(msg);
    }
    
    @Override
    protected Object extractPayload(Message msg) {
        try {

            return ((ObjectMessage)msg).getObject();

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }    
    
    // special methods for handling operations with single parameters
    
    public Message createJMSMessageForSingleParamOperation(Session session, Object o, boolean wrapSingleInput) {
        if (session == null) {
            logger.fine("no response session to create message: " + String.valueOf(o));
            return null;
        }
        try {

            ObjectMessage message = session.createObjectMessage();

            if (o != null) {
                if (!(o instanceof Serializable)) {
                    throw new IllegalStateException("JMS ObjectMessage payload not Serializable: " + o);
                }

                // If the user has specifically requests that single parameters
                // be wrapped then leave is as is as it will have already been 
                // wrapped by Tuscany. Otherwise unwrap it.
                if (wrapSingleInput) {
                    message.setObject((Serializable) o);
                } else { // unwrap from array
                    message.setObject((Serializable) ((Object[]) o)[0]);
                }

            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    public Object extractPayloadFromJMSMessageForSingleParamOperation(Message msg, Class<?> argType, boolean wrapSingle) {
        // We always have a one arg operation if this method is called so we need to 
        // decide if the data on the wire is wrapped or not. This is the algorithm.
        //
        // If the payload is null then create an empty array and pass it on
        // If the payload is not an array then it must represent an unwrapped 
        //    single arg. Wrap it up and pass it on
        // If the payload is an array then determine if it's a wrapped single arg or not
        //    If the service interface arg type matches the type of the array and not it's contents
        //      then it's an unwrapped argument so wrap it and pass it on
        //    If the service interface arg type matches the type of the contents and not the type
        //      of the array then the parameter is already wrapped so pass it on as is 
        //    If the service interface arg type matches both the type of the 
        //      array and the type of its contents then assume that the whole array is the
        //      parameter and decide whether to unwrap it or pass it on as is based on the 
        //      setting of the wrapSingle attribute
        //
        
        try {
            Object payload = ((ObjectMessage) msg).getObject();
            
            if (payload instanceof Throwable) {
                if (payload instanceof RuntimeException) {
                    throw new ServiceRuntimeException("remote service exception, see nested exception", (RuntimeException) payload);
                } else {
                    return new InvocationTargetException((Throwable) payload);
                }
            }
            
            if (payload == null) {
                // methodA(null) was not wrapped on wire so wrap it here in order
                // that it passes through the rest of the Tuscany wire successfully
                return new Object[] { payload }; 
            }

            boolean payloadIsArray = payload.getClass().isArray();

            // Non-array payload is single arg
            if (!payloadIsArray) {
                // methodB(arg) wasn't wrapped on wire so wrap it here in order
                // that it passes through the rest of the Tuscany wire successfully
                return new Object[] { payload }; 
            } else {
                int size = ((Object[]) payload).length;
                
                // An initial quick check to determine whether the payload is not 
                // wrapped. If the array has anything other than a single entry
                // then it's not the result of reference side wrapping so wrap it 
                // here and pass it on
                if (size != 1) { 
                    return new Object[] { payload };
                }

                // we know the array has only one entry now so get it
                Object arrayContents = ((Object[]) payload)[0];

                // Is the operation argument the same type as the array itself?
                if (argType.isAssignableFrom(payload.getClass())) {
                    
                    // So we believe that the whole array is the argument but need
                    // to check what is in the array to be sure
                    if (arrayContents == null) {
                        // There is nothing in the array so it could be an accident that
                        // the array type matches the argument type, e.g. op(Object)
                        // so rely on the wrapSingle setting to choose
                        if (wrapSingle) {
                            return payload;
                        } else {
                            return new Object[] { payload };
                        }
                    } else if (argType.isAssignableFrom(arrayContents.getClass())) { 
                        // We can't tell as the argument type matches both the array type and 
                        // the array contents type so use the wrapSingle setting to choose
                        if (wrapSingle) {
                            return payload;
                        } else {
                            return new Object[] { payload };
                        }
                    } else {
                        // So by now we know the whole array is intended to be the 
                        // parameter to wrap it and send it on
                        return new Object[] { payload }; 
                    }

                } else {
                    // The array type doesn't match the argument type so assume that the 
                    // array contents will match the argument type and that hence the
                    // parameter is already wrapped so just send it as is. If the contents
                    // type doesn't match the argument type a exception will be thrown further
                    // along the wire
                    return payload;
                }
            }            
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
