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
package org.apache.tuscany.binding.jsonrpc;

import javax.xml.namespace.QName;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceBinding extends ServiceBindingExtension {
    public static final QName BINDING_JSON = new QName(XML_NAMESPACE_1_0, "binding.jsonrpc");

    private static int servletRegistrationCount = 0;

    private ServletHost servletHost;

    private WireService wireService;

    public static final String SCRIPT_GETTER_SERVICE_MAPPING = "/SCA/scripts";

    public JSONRPCServiceBinding(String theName, CompositeComponent parent, WireService wireService,
                                 ServletHost servletHost) {

        super(theName, parent);

        this.servletHost = servletHost;
        this.wireService = wireService;
    }

    public synchronized void start() {
        super.start();
        JSONRPCEntryPointServlet servlet;
        // FIXME this should not have to create a proxy but should instead dispatch directly down an invocation chain
        Class<?> aClass = getInboundWire().getServiceContract().getInterfaceClass();
        Object instance =
            wireService.createProxy(aClass, getInboundWire());
        servlet = new JSONRPCEntryPointServlet(getName(), aClass, instance);

        // register the servlet based on the service name
        servletHost.registerMapping("/" + getName(), servlet);

        // if the script getter servlet is not already registered then register it
        if ((servletRegistrationCount == 0) && (!servletHost.isMappingRegistered("/SCA/scripts"))) {
            servletHost.registerMapping(SCRIPT_GETTER_SERVICE_MAPPING, new ScriptGetterServlet());
        }

        // increase the registered servlet count
        servletRegistrationCount++;
    }

    @Destroy
    public synchronized void stop() {
        // unregister the service servlet
        servletHost.unregisterMapping("/" + getName());

        // decrement the registered servlet count
        servletRegistrationCount--;

        // if this was the last servlet, we can now unregister the script getter servlet
        if (servletRegistrationCount == 0) {
            servletHost.unregisterMapping(SCRIPT_GETTER_SERVICE_MAPPING);
        }

        super.stop();
    }

    public QName getBindingType() {
        return BINDING_JSON;
    }
}
