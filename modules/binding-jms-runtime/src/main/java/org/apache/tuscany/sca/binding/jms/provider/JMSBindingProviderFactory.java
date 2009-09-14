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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.host.DefaultJMSHostExtensionPointImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.jms.JMSHostExtensionPoint;
import org.apache.tuscany.sca.host.jms.JMSServiceListenerFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A factory from creating the JMS binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingProviderFactory implements BindingProviderFactory<JMSBinding> {

    private ExtensionPointRegistry extensionPoints;
    private JMSResourceFactoryExtensionPoint jmsRFEP;
    private JMSServiceListenerFactory serviceListenerFactory;

    public JMSBindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        jmsRFEP = (JMSResourceFactoryExtensionPoint)extensionPoints.getExtensionPoint(JMSResourceFactoryExtensionPoint.class);
        if (jmsRFEP == null) {
            jmsRFEP = new DefaultJMSResourceFactoryExtensionPoint();
            extensionPoints.addExtensionPoint(jmsRFEP);
        }

        JMSHostExtensionPoint jmsHostExtensionPoint = (JMSHostExtensionPoint)extensionPoints.getExtensionPoint(JMSHostExtensionPoint.class);
        if (jmsHostExtensionPoint == null) {
            jmsHostExtensionPoint = new DefaultJMSHostExtensionPointImpl(extensionPoints);
            extensionPoints.addExtensionPoint(jmsHostExtensionPoint);
        }
        serviceListenerFactory = jmsHostExtensionPoint.getJMSServiceListenerFactory();
    }

    public ReferenceBindingProvider createReferenceBindingProvider(EndpointReference endpointReference) {
        JMSResourceFactory jmsRF = jmsRFEP.createJMSResourceFactory((JMSBinding)endpointReference.getBinding());
        return new JMSBindingReferenceBindingProvider((RuntimeComponent)endpointReference.getComponent(), (RuntimeComponentReference) endpointReference.getReference(), (JMSBinding)endpointReference.getBinding(), extensionPoints, jmsRF);
    }

    public ServiceBindingProvider createServiceBindingProvider(Endpoint endpoint) {
        JMSBinding binding = (JMSBinding)endpoint.getBinding();
        JMSResourceFactory jmsRF = jmsRFEP.createJMSResourceFactory(binding);
        return new JMSBindingServiceBindingProvider((RuntimeComponent)endpoint.getComponent(), (RuntimeComponentService)endpoint.getService(), binding, binding, serviceListenerFactory, extensionPoints, jmsRF);
    }

    public Class<JMSBinding> getModelType() {
        return JMSBinding.class;
    }

}
