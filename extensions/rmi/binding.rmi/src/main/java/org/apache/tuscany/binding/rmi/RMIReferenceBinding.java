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

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.host.rmi.RMIHost;

/**
 * @version $Rev$ $Date$
 */
public class RMIReferenceBinding extends ReferenceBindingExtension {
    private static final QName BINDING_RMI = new QName(
        "http://tuscany.apache.org/xmlns/binding/rmi/1.0-SNAPSHOT", "binding.rmi");

    private final String host;

    private final String port;

    private final String svcName;

    private RMIHost rmiHost;

    public RMIReferenceBinding(String name,
                               CompositeComponent parent,
                               RMIHost rmiHost,
                               String host,
                               String port,
                               String svcName) {
        super(name, parent);
        this.host = host;
        this.port = port;
        this.svcName = svcName;
        this.rmiHost = rmiHost;
    }

    public QName getBindingType() {
        return BINDING_RMI;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        try {
            /*Remote proxy = getProxy();
             Method remoteMethod = proxy.getClass().getMethod(operation.getName(),
             (Class[]) operation.getParameterTypes());
             return new RMIInvoker(proxy, remoteMethod);
             */
            Method method = findMethod(operation, contract.getInterfaceClass().getMethods());
            Method remoteMethod =
                getInboundWire().getServiceContract().getInterfaceClass().getMethod(operation.getName(), (Class[]) method.getParameterTypes());
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
