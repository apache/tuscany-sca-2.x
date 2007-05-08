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

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.provider.BindingProviderFactory;
import org.apache.tuscany.provider.ReferenceBindingProvider;
import org.apache.tuscany.provider.ServiceBindingProvider;
import org.apache.tuscany.rmi.RMIHost;


/**
 * 
 * RMIBindingProvider
 *
 * @version $Rev$ $Date$
 */
public class RMIBindingProviderFactory extends RMIBindingImpl implements BindingProviderFactory {

    private RMIHost rmiHost;
    
    public RMIBindingProviderFactory(RMIHost rmiHost) {
        this.rmiHost = rmiHost;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference) {
        return new RMIBindingProvider(component, reference, this, rmiHost);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service) {
        return new RMIBindingProvider(component, service, this, rmiHost);
    }
}
