/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.binding.rmi;

import org.apache.tuscany.rmi.RMIHost;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;


/**
 * 
 * RMIBindingProvider
 *
 * @version $Rev$ $Date$
 */
public class RMIBindingProviderFactory implements BindingProviderFactory<RMIBinding> {

    private RMIHost rmiHost;
    private MessageFactory messageFactory;
    
    public RMIBindingProviderFactory(MessageFactory messageFactory, RMIHost rmiHost) {
        this.rmiHost = rmiHost;
        this.messageFactory = messageFactory;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, RMIBinding binding) {
        return new RMIBindingProvider(component, reference, binding, rmiHost);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, RMIBinding binding) {
        return new RMIBindingProvider(component, service, binding, messageFactory, rmiHost);
    }
    
    public Class<RMIBinding> getModelType() {
        return RMIBinding.class;
    }
}
