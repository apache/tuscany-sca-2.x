/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * @version $Rev$ $Date$
 */
public class RMIService extends ServiceExtension {
    private final String uri;
    private final Class<? extends Remote> service;

    public RMIService(String name, CompositeComponent parent, WireService wireService, String uri, Class<? extends Remote> service) {
        super(name, parent, wireService);
        this.uri = uri;
        this.service = service;
    }

    public void start() {
        super.start();
        Remote rmiProxy = createProxy();

        try {
            Naming.bind(uri, rmiProxy);
        } catch (AlreadyBoundException e) {
            throw new NoRemoteServiceException(e);
        } catch (RemoteException e) {
            throw new NoRemoteServiceException(e);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public void stop() {
        try {
            Naming.unbind(uri);
        } catch (NotBoundException e) {
            // ignore
        } catch (RemoteException e) {
            throw new NoRemoteServiceException(e);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        super.stop();
    }

    protected Remote createProxy() {
        InvocationHandler handler = new RMIInvocationHandler(getHandler());
        return service.cast(Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, handler));
    }

    private static class RMIInvocationHandler implements InvocationHandler {
        private final WireInvocationHandler wireHandler;

        public RMIInvocationHandler(WireInvocationHandler wireHandler) {
            this.wireHandler = wireHandler;
        }

        public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
            return wireHandler.invoke(method, objects);
        }
    }
}
