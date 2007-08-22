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

package org.apache.tuscany.sca.binding.sca.axis2.impl;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A specialization of the Axis2BindingProvider that just switches in the SCABinding model
 * element when it is required. The SCABinding is required as the service binding provider
 * finds the service wire based in the binding 
 * 
 * @version $Rev: 563772 $ $Date: 2007-08-08 07:50:49 +0100 (Wed, 08 Aug 2007) $
 */
public class Axis2SCAServiceProvider extends Axis2ServiceProvider {

    private SCABinding binding;
    
    /**
     * Switch in the fake ws binding
     * 
     * @param component
     * @param service
     * @param binding
     * @param wsBinding
     * @param servletHost
     * @param messageFactory
     */
    public Axis2SCAServiceProvider(RuntimeComponent component,
                                   RuntimeComponentService service,
                                   SCABinding binding,
                                   WebServiceBinding wsBinding,
                                   ServletHost servletHost,
                                   MessageFactory messageFactory)  {
        
        super(component, 
                service, 
                wsBinding, 
                servletHost,
                messageFactory);

        this.binding = binding;
    }
    
    /**
     * Return the sca binding as wires will be registered against this rather
     * than against the wsBinding that the Axis2SCAServiceProvider is 
     * expecting
     * 
     * @return the binding
     */
    @Override
    protected Binding getBinding(){
        return binding;
    }    
}
