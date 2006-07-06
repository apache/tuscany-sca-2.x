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
package org.apache.tuscany.binding.rmi.builder;

import java.lang.reflect.Method;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tuscany.binding.rmi.assembly.RMIBinding;
import org.apache.tuscany.binding.rmi.config.RMIExternalServiceContextFactory;
import org.apache.tuscany.binding.rmi.externalservice.RMIServiceInvoker;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.extension.ExternalServiceBuilderSupport;
import org.apache.tuscany.core.extension.ExternalServiceContextFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.ExternalService;
import org.osoa.sca.annotations.Scope;

/**
 * Creates a <code>ContextFactory</code> for an external service configured with the {@link RMIBinding}
 */
@Scope("MODULE") 
public class ExternalRMIBuilder extends ExternalServiceBuilderSupport<RMIBinding> 
{
    public static final String PROTOCOL_PREFIX = "//";
    public static final String COLON = ":";
    public static final String SLASH = "/";
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.core.extension.ExternalServiceBuilderSupport#createExternalServiceContextFactory(org.apache.tuscany.model.assembly.ExternalService)
     */
    @Override
    protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) 
    {
        
        String uri = null;
        Class serviceInterface = externalService.getConfiguredService().getPort().
                                    getServiceContract().getInterface();
        RMIBinding wsBinding = (RMIBinding) externalService.getBindings().get(0);
        uri = PROTOCOL_PREFIX + wsBinding.getRMIHostName() +
              ((wsBinding.getRMIPort() == null || wsBinding.getRMIPort().length() <= 0) ? "" : (COLON + wsBinding.getRMIPort())) +
              SLASH + wsBinding.getRMIServerName();
                        
        Remote serviceClient = createServiceClient(uri);

        ClassLoader cl = wsBinding.getResourceLoader().getClassLoader();
            
        try
        {
            Map<String, Method> methods = createServiceMethods(serviceInterface);
    
            RMIServiceInvoker rmiClient = new RMIServiceInvoker(serviceClient, methods);
            return new RMIExternalServiceContextFactory(externalService.getName(), new SingletonObjectFactory<RMIServiceInvoker>(rmiClient));
        }
        catch (Exception e) 
        {
            BuilderConfigException bce = new BuilderConfigException("Exception creating service context factory for RMI service bindings ", e);
            bce.addContextName(uri);
            throw bce;
        }

        
    }

    /**
     * Create an Axis2 ServiceClient configured for the externalService
     */
    protected Remote createServiceClient(String uri) 
    {
        Remote serviceClient;
        try 
        {
            serviceClient = Naming.lookup(uri);

        } 
        catch (Exception e) 
        {
            BuilderConfigException bce = new BuilderConfigException("Exception creating proxy for RMI service", e);
            bce.addContextName(uri);
            throw bce;
        }

        return serviceClient;
    }

    /**
     * Create and configure an Axis2OperationInvoker for each operation in the externalService
     */
    protected Map<String, Method> createServiceMethods(Class sc) 
    {
        Map<String, Method> methodMap = new Hashtable<String, Method>();
        Method[] serviceMethods = sc.getMethods();
        StringBuffer sb = new StringBuffer(); 
        Class[] argTypes;
                
        for ( int count = 0 ; count < serviceMethods.length ; ++count )
        {
            //for each method get the arg types for the method
            //append the argtypes simple names to the name of the method and use it as key
            sb.append(serviceMethods[count].getName());
            argTypes = serviceMethods[count].getParameterTypes();
            
            for ( int argCount = 0 ; argCount < argTypes.length ; ++argCount )
            {
                sb.append(argTypes[argCount].getSimpleName());
            }
            
            methodMap.put(sb.toString(), serviceMethods[count]);
        }
        
        return methodMap;
    }

}
