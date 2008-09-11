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

package org.apache.tuscany.sca.binding.sca.jms;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.provider.JMSBindingReferenceBindingProvider;
import org.apache.tuscany.sca.binding.jms.provider.JMSBindingServiceBindingProvider;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * The factory for the JMS based implementation of the distributed sca binding
 */
public class JMSSCABindingProviderFactory implements BindingProviderFactory<DistributedSCABinding> {
    
    private WorkScheduler workScheduler;
    private ExtensionPointRegistry extensionPoints;

    public JMSSCABindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        workScheduler = utilities.getUtility(WorkScheduler.class);
        assert workScheduler != null;
    }    

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   DistributedSCABinding binding) {
        JMSBinding jmsBinding = createBinding(binding);

        // FIXME: CREATE_NEVER doesn't work as the dynamically created JNDI destinations 
        // aren't replicated around the broker cluster. Maybe it needs an AMQ specific
        // impl of the Tuscany JMSResourceFactory which uses use physical destinations 
        // instead of JNDI
        // jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_NEVER);

        return new JMSBindingReferenceBindingProvider(component, reference, jmsBinding, extensionPoints);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               DistributedSCABinding binding) {
        JMSBinding jmsBinding = createBinding(binding);
        jmsBinding.setDestinationCreate(JMSBindingConstants.CREATE_ALWAYS);
        return new JMSBindingServiceBindingProvider(component, service, binding.getSCABinding(), jmsBinding, workScheduler, extensionPoints);
    }

    private JMSBinding createBinding(DistributedSCABinding binding) {
        JMSBinding b = new JMSBinding();
        b.setInitialContextFactoryName("org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        b.setJndiURL("vm://localhost"); // TODO: plug in jndi url from definitions.xml
        b.setRequestMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
        b.setResponseMessageProcessorName(JMSBindingConstants.OBJECT_MP_CLASSNAME);
        if (binding.getSCABinding().getURI().startsWith("/")) {
            b.setDestinationName(binding.getSCABinding().getURI().substring(1));
        } else {
            b.setDestinationName(binding.getSCABinding().getURI());
        }
        return b;
    }

    public Class<DistributedSCABinding> getModelType() {
        return DistributedSCABinding.class;
    }  
}
