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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault;




import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 *
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultReferenceInterceptor implements Interceptor {

    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private String correlationScheme;
    private WireFormat requestWireFormat;
    private WireFormat responseWireFormat;

    public WireFormatJMSDefaultReferenceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        this.correlationScheme = jmsBinding.getCorrelationScheme();
        
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSDefault){
            this.requestWireFormat = jmsBinding.getRequestWireFormat();
        }
        
        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSDefault){
            this.responseWireFormat = jmsBinding.getResponseWireFormat();
        }
    }

    public Message invoke(Message msg) {
        if (requestWireFormat != null){
            msg = invokeRequest(msg);
        }
        
        msg = getNext().invoke(msg);
        
        if (responseWireFormat != null){
            msg = invokeRequest(msg);
        }
        
        return msg;
    }
    
    public Message invokeRequest(Message msg) {
        // TODO binding interceptor iface TBD
        return null;
    }
    
    public Message invokeResponse(Message msg) {
        // TODO binding interceptor iface TBD
        return null;
    }    

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
