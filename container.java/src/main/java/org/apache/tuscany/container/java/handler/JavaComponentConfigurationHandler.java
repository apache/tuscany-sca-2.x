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
package org.apache.tuscany.container.java.handler;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.OperationType;
import org.apache.tuscany.model.types.java.JavaOperationType;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * Configures an invocation pipeline for Java-based components
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentConfigurationHandler extends AdapterImpl implements MessageHandler {
    /**
     * Constructor.
     */
    public JavaComponentConfigurationHandler() {
        super();
    }

    public boolean processMessage(Message message) {
        // Get the endpoint reference of the target service and the service model element
        EndpointReference endpointReference = message.getEndpointReference();
        ConfiguredPort portEndpoint = endpointReference.getConfiguredPort();

        // Return immediately if the target is not a service
        if (!(portEndpoint instanceof ConfiguredService))
            return true;

        ConfiguredService serviceEndpoint = (ConfiguredService) portEndpoint;
        Part part = serviceEndpoint.getPart();
        if (!(part instanceof Component))
            return true;
        Component component = (Component) part;

        // Return immediately if the target is not an java component
        ComponentImplementation implementation = component.getComponentImplementation();
        if (!(implementation instanceof JavaImplementation))
            return true;

        // Get the proxy configuration
        ProxyConfiguration proxyConfiguration=(ProxyConfiguration)message.getBody();
        Map<OperationType, InvocationConfiguration>invocationConfigurations=proxyConfiguration.getInvocationConfigurations();
        Map<Integer,ScopeContext> scopeContainers = proxyConfiguration.getScopeContainers();

        // Get the business interface
        Interface targetInterface = serviceEndpoint.getService().getInterfaceContract();
        InterfaceType targetInterfaceType = targetInterface.getInterfaceType();
        ScopeEnum scope = targetInterface.getScope();
        String serviceAddress = serviceEndpoint.getPart().getName(); // assemblyFactory.createServiceURI(null, serviceEndpoint).getAddress();

        // Add our invoker to the invocation configurations
        for (InvocationConfiguration invocationConfiguration : invocationConfigurations.values()) {
            OperationType targetOperationType=targetInterfaceType.getOperationType(invocationConfiguration.getOperationType().getName());
            Method method = ((JavaOperationType) targetOperationType).getJavaMethod();
            TargetInvoker invoker = new ScopedJavaComponentInvoker(serviceAddress, method, scopeContainers.get(scope.getValue()));
            invocationConfiguration.setTargetInvoker(invoker);
        }

        return false;
    }
}
