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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;

import org.apache.tuscany.rmi.RMIHostException;
import org.apache.tuscany.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Invoke an RMI reference.
 *
 * @version $Rev$ $Date$
 */
public class RMIInvoker extends TargetInvokerExtension {
    private Method remoteMethod;
    private String host;
    private String port;
    private String svcName;
    private RMIHostExtensionPoint rmiHost;
    private Remote proxy;

    public RMIInvoker(RMIHostExtensionPoint rmiHost, String host, String port, String svcName, Method remoteMethod) {
        this.remoteMethod = remoteMethod;
        this.host = host;
        this.port = port;
        this.svcName = svcName;
        this.rmiHost = rmiHost;
    }

    public Object invokeTarget(final Object payload, 
                               final short sequence, 
                               WorkContext workContext) throws InvocationTargetException {
        try {
            if (proxy == null) {
                proxy = rmiHost.findService(host, port, svcName);
                // proxy = Naming.lookup(serviceURI);
            }
            
            remoteMethod = proxy.getClass().getMethod(remoteMethod.getName(), remoteMethod.getParameterTypes());
            
            if (payload != null && !payload.getClass().isArray()) {
                return remoteMethod.invoke(proxy, payload);
            } else {
                return remoteMethod.invoke(proxy, (Object[])payload);
                
            }
        } catch (RMIHostException e) {
            // the method we are passed must be accessible
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            // the method we are passed must be accessible
            throw new AssertionError(e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }

    }

}
