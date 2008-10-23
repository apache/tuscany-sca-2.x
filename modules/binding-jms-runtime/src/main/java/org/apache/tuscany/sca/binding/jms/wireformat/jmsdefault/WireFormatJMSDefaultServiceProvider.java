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

import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultServiceProvider implements WireFormatProvider {
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private Binding binding;
    private JMSResourceFactory jmsResourceFactory;

    public WireFormatJMSDefaultServiceProvider(RuntimeComponent component, RuntimeComponentService service, Binding binding, JMSResourceFactory jmsResourceFactory) {
        super();
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.jmsResourceFactory = jmsResourceFactory;
    }

    /**
     */
    public Interceptor createInterceptor() {
        return new WireFormatJMSDefaultServiceInterceptor((JMSBinding)binding,
                                                          jmsResourceFactory,
                                                          service.getRuntimeWire(binding));
    }

    /**
     */
    public String getPhase() {
        return Phase.SERVICE_BINDING_WIREFORMAT;
    }

}
