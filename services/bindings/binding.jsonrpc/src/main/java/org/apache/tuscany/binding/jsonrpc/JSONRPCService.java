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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Destroy;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCService extends ServiceExtension {
    private ServletHost servletHost;
    private static int servletRegistrationCount = 0;

    public JSONRPCService(String theName, Class<?> interfaze, CompositeComponent parent, WireService wireService, ServletHost servletHost) {

        super(theName, interfaze, parent, wireService);

        this.servletHost = servletHost;
    }

    public synchronized void start() {
        super.start();

        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet(getName(), this.getServiceInstance());
        servletHost.registerMapping("/" + getName(), servlet);
        if((servletRegistrationCount == 0) && (!servletHost.isMappingRegistered("/SCA/scripts"))) {            
            servletHost.registerMapping("/SCA/scripts", new ScriptGetterServlet());            
        }
        servletRegistrationCount++;
    }

    @Destroy
    public synchronized void stop() {
        servletHost.unregisterMapping("/" + getName());
        servletRegistrationCount--;
        if(servletRegistrationCount == 0)
        {
            servletHost.unregisterMapping("/SCA/scripts");            
        }

        super.stop();
    }

}
