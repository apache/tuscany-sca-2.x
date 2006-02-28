/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.axis.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.axis.MessageContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Base implementation class for web service entry points beans.
 */
public class WebServiceEntryPointBean implements ServiceLifecycle {
    private AggregateContext moduleContext;
    private EntryPointContext entryPointContext;
    private InvocationHandler invocationHandler;
    private Object proxy;

    /**
     * Constructor.
     */
    public WebServiceEntryPointBean() {
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#init(java.lang.Object)
     */
    public void init(Object context) throws ServiceException {
        if (context instanceof ServletEndpointContext) {

            // Get the export name (the target service name) from the message context
            MessageContext messageContext = (MessageContext) (((ServletEndpointContext) context).getMessageContext());

            // Initialize
            initialize(messageContext.getTargetService());
        }
    }

    /**
     * @see javax.xml.rpc.server.ServiceLifecycle#destroy()
     */
    public void destroy() {
    }

    /**
     * Initialize.
     */
    protected void initialize(String entryPointName) {

        // Get the current aggregate context
        moduleContext = (AggregateContext) CurrentModuleContext.getContext();
        
        // Get the current entry point context
        entryPointContext = (EntryPointContext)moduleContext.getContext(entryPointName);
        if (entryPointContext == null) {
            throw new ServiceRuntimeException("Entry point not found: " + entryPointName);
        }

        // Get the service interface
        Aggregate aggregate=moduleContext.getAggregate();
        EntryPoint entryPoint=aggregate.getEntryPoint(entryPointName);
        Class serviceInterface=entryPoint.getConfiguredService().getService().getServiceContract().getInterface();
        
        // Get the invocation handler for the entry point
        invocationHandler=(InvocationHandler)entryPointContext.getInstance(null);

        // Create a dynamic proxy
        // The proxy implements the service interface and delegates to the invocation handler
        proxy=Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, invocationHandler);
    }

    /**
     * Returns the dynamic proxy
     * @return
     */
    protected Object getProxy() {
        return proxy;
    }
    
}
