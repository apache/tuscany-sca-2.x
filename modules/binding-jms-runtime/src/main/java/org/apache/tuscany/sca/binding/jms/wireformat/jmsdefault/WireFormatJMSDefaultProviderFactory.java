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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultProviderFactory implements WireFormatProviderFactory<WireFormatJMSDefault> {
    private ExtensionPointRegistry registry;
    private JMSResourceFactoryExtensionPoint jmsRFEP;
    
    public WireFormatJMSDefaultProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
        jmsRFEP = (JMSResourceFactoryExtensionPoint)registry.getExtensionPoint(JMSResourceFactoryExtensionPoint.class);

    }

    /**
     */
    public WireFormatProvider createReferenceWireFormatProvider(RuntimeComponent component,
                                                        RuntimeComponentReference reference,
                                                        Binding binding) {
        return new WireFormatJMSDefaultReferenceProvider(component, reference, binding);
    }

    /**
      */
    public WireFormatProvider createServiceWireFormatProvider(RuntimeComponent component,
                                                              RuntimeComponentService service,
                                                              Binding binding) {
        JMSResourceFactory jmsRF = jmsRFEP.createJMSResourceFactory((JMSBinding)binding);
        return new WireFormatJMSDefaultServiceProvider(component, service, binding, jmsRF);
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class getModelType() {
        // TODO Auto-generated method stub
        return null;
    }

}
