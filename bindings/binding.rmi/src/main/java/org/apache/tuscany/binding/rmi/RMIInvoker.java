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

import org.apache.tuscany.host.rmi.RMIHost;
import org.apache.tuscany.host.rmi.RMIHostException;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Invoke an RMI reference.
 *
 * @version $Rev$ $Date$
 */
public class RMIInvoker implements TargetInvoker {
    private Method remoteMethod;
    private String host;
    private String port;
    private String svcName;
    private RMIHost rmiHost;
    private Remote proxy;

    /*@Constructor({"rmiHost", "host", "port", "svnName", "remoteMethod"})
     public RMIInvoker(@Autowire
     RMIHost rmiHost, @Autowire
     String host, @Autowire
     String port, @Autowire
     String svcName, @Autowire
     Method remoteMethod) {
     // assert remoteMethod.isAccessible();
     this.remoteMethod = remoteMethod;
     this.host = host;
     this.port = port;
     this.svcName = svcName;
     this.rmiHost = rmiHost;
     }*/

    public RMIInvoker(RMIHost rmiHost, String host, String port, String svcName, Method remoteMethod) {
        // assert remoteMethod.isAccessible();
        this.remoteMethod = remoteMethod;
        this.host = host;
        this.port = port;
        this.svcName = svcName;
        this.rmiHost = rmiHost;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        try {
            if (proxy == null) {
                proxy = rmiHost.findService(host, port, svcName);
                // proxy = Naming.lookup(serviceURI);
            }
            return remoteMethod.invoke(proxy, (Object[]) payload);
        } catch (RMIHostException e) {
            // the method we are passed must be accessible
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            // the method we are passed must be accessible
            throw new AssertionError(e);
        }

    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isOptimizable() {
        return false;
    }

    public boolean isCacheable() {
        return false;
    }

    public void setCacheable(boolean cacheable) {
    }
}
