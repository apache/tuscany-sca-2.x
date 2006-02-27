/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.axis.handler;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.model.assembly.ExternalService;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.ServiceUnavailableException;

/**
 * A mock client for a transport binding
 * 
 * @version $Rev$ $Date$
 */
public class ExternalWebServiceClient {
    
    
    private WebServicePortMetaData portMetaData;
    private Service jaxrpcService;
    private Map<Method, Call> calls=new MethodHashMap();

    /**
     * Constructs a new ExternalWebServiceClient.
     * @param externalService
     * @param wsBinding
     */
    public ExternalWebServiceClient(ExternalService externalService, WebServiceBinding wsBinding) {
        
        // Create a port metadata info object to hold the port information
        portMetaData = new WebServicePortMetaData(wsBinding.getWSDLDefinition(), wsBinding.getWSDLPort(), wsBinding.getURI(), false);

        // Create a JAX-RPC service
        QName wsdlServiceName = portMetaData.getService().getQName();
        try {
            jaxrpcService = ServiceFactory.newInstance().createService(wsdlServiceName);
        } catch (ServiceException e) {
            throw new ServiceUnavailableException(e);
        }

        // Create JAX-RPC calls for all the methods on the service contract
        Set<Method> methods=JavaIntrospectionHelper.getAllUniqueMethods(externalService.getConfiguredService().getService().getServiceContract().getInterface());
        for (Method method : methods) {
            Call call=createCall(method);
            calls.put(method, call);
        }
        
    }

    /**
     * Create a JAX-RPC call for the given method.
     * @param method
     * @return
     */
    private Call createCall(Method method) {
        
        // Create a JAX RPC call object
        QName portName = portMetaData.getPortName();
        Call call;
        try {
            call = (Call) jaxrpcService.createCall(portName, method.getName());
        } catch (ServiceException e) {
            throw new IllegalArgumentException(e);
        }

        // Set the target endpoint address
        String endpoint = portMetaData.getEndpoint();
        if (endpoint != null) {
            String originalEndpoint = call.getTargetEndpointAddress();
            if (!endpoint.equals(originalEndpoint))
                call.setTargetEndpointAddress(endpoint);
        }
        
        return call;
    }

    /**
     * Invoke an operation on the external Web service.
     * @param method
     * @param args
     * @return
     */
    public Object invoke(Method method,  Object[] args) {
        Call call=calls.get(method);
        try {
            return call.invoke(args);
        } catch (RemoteException e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
