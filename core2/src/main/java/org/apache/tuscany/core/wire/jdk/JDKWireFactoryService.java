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
package org.apache.tuscany.core.wire.jdk;

import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireFactoryService;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A system service that creates JDK dynamic proxy-based wires
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
@Service(interfaces = {WireFactoryService.class})
public class JDKWireFactoryService implements WireFactoryService {

    public JDKWireFactoryService() {
    }

    @Init(eager = true)
    public void init() {
    }

    public InboundWire createServiceWire() {
        return new JDKInboundWire();
    }

    public OutboundWire createReferenceWire() {
        return new JDKOutboundWire();
    }

    public boolean isProxy(Object object) {
        if (object == null) {
            return false;
        } else {
            return Proxy.isProxyClass(object.getClass());
        }
    }

    public InvocationHandler getHandler(Object proxy) {
        if (proxy == null) {
            return null;
        } else {
            return Proxy.getInvocationHandler(proxy);
        }
    }

}
