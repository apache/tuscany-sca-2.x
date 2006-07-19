/**
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
package org.apache.tuscany.binding.rmi.externalservice;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Map;

import org.apache.tuscany.core.extension.ExternalServiceInvoker;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Axis2ServiceInvoker uses Axis2 to invoke a remote web service
 */
public class RMIServiceInvoker implements ExternalServiceInvoker 
{
    private Remote serviceClient;

    private Map<String, Method> operationInvokers;

    public RMIServiceInvoker(Remote serviceClient, Map<String, Method> operationInvokers) {
        this.serviceClient = serviceClient;
        this.operationInvokers = operationInvokers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.axis2.handler.ExternalServiceInvoker#invoke(java.lang.String, java.lang.Object[])
     */
    public Object invoke(String methodName, Object[] args) {
        try 
        {
            StringBuffer sb = new StringBuffer(methodName);
            Method  serviceMethod = operationInvokers.get(getUniqueMethodName(sb, args));
            return serviceMethod.invoke(serviceClient, args);
            
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    public String getUniqueMethodName(StringBuffer sb, Object[] args)
    {
        for ( int count = 0 ; count < args.length ; ++count )
        {
            sb.append(args[count].getClass().getSimpleName());
        }
        
        return sb.toString();
    }

}
