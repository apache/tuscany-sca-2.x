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

import org.apache.tuscany.spi.wire.WireInvocationHandler;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class RemoteMethodHandler implements MethodInterceptor {
    public static final String FINALIZE_METHOD = "finalize";

    private WireInvocationHandler wireHandler;

    private Class compSvcIntf;

    public RemoteMethodHandler(WireInvocationHandler handler, Class intf) {
        this.wireHandler = handler;
        compSvcIntf = intf;
    }

    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // since incoming method signatures have 'remotemethod invocation' it will not match with the
        // wired component's method signatures. Hence need to pull in the corresponding method from the
        // component's service contract interface to make this invocation.
        return wireHandler.invoke(compSvcIntf.getMethod(method.getName(),
            (Class[]) method.getParameterTypes()),
            args);
    }

}
