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
package org.apache.tuscany.binding.rmi;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class RMIReference<T> extends ReferenceExtension<T> {
    private final String uri;

    public RMIReference(String name, 
                        CompositeComponent<?> parent, 
                        WireService wireService, 
                        String uri, 
                        Class<T> service) {
        super(name, service, parent, wireService);
        setInterface(service);
        this.uri = uri;
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        try {
            Remote proxy = getProxy();
            Method remoteMethod = proxy.getClass().getMethod(operation.getName(),
                                                             operation.getParameterTypes());
            return new RMIInvoker(proxy, remoteMethod);
        } catch (NoSuchMethodException e) {
            throw new NoRemoteMethodException(operation.toString(), e);
        }
    }

    protected Remote getProxy() {
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
    }
}
