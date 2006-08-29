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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.host.RMIHost;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class RMIReference<T> extends ReferenceExtension<T> {
    private final String host;

    private final String port;

    private final String svcName;

    private RMIHost rmiHost;

    public RMIReference(String name,
                        CompositeComponent<?> parent,
                        WireService wireService,
                        RMIHost rmiHost,
                        String host,
                        String port,
                        String svcName,
                        Class<T> service) {
        super(name, service, parent, wireService);
        this.host = host;
        this.port = port;
        this.svcName = svcName;
        this.rmiHost = rmiHost;
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        try {
            /*Remote proxy = getProxy();
             Method remoteMethod = proxy.getClass().getMethod(operation.getName(),
             (Class[]) operation.getParameterTypes());
             return new RMIInvoker(proxy, remoteMethod);
             */
            Method remoteMethod =
                getInterface().getMethod(operation.getName(), (Class[]) operation.getParameterTypes());
            return new RMIInvoker(rmiHost, host, port, svcName, remoteMethod);
        } catch (NoSuchMethodException e) {
            throw new NoRemoteMethodException(operation.toString(), e);
        }
    }

    /*protected Remote getProxy() {
    try {
    // todo do we need to cache this result?
    return Naming.lookup(uri);
    } catch (NotBoundException e) {
    throw new NoRemoteServiceException(uri);
    } catch (MalformedURLException e) {
    throw new NoRemoteServiceException(uri);
    } catch (RemoteException e) {
    throw new NoRemoteServiceException(uri);
    }
    }*/
}
