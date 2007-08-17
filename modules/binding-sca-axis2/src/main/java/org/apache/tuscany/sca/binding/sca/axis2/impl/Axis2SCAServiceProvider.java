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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceInMessageReceiver;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceInOutSyncMessageReceiver;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceProvider;
import org.apache.tuscany.sca.binding.axis2.Axis2ServiceServlet;
import org.apache.tuscany.sca.binding.axis2.TuscanyAxisConfigurator;
import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.core.runtime.EndpointReferenceImpl;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
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
    
    private AbstractContract contract;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private ConfigurationContext configContext;    
    
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

        this.contract = service;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        
        this.binding = binding;
    }
    
    /**
     * Return the sca binding as wires will be registered against this rather
     * than against the wsBinding that the Axis2SCAServiceProvider is 
     * expecting
     * 
     * @return the binding
     */
    protected Binding getBinding(){
        return binding;
    }    
}
