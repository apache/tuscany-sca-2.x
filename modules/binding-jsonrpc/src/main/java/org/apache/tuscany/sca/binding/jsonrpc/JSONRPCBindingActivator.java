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

package org.apache.tuscany.sca.binding.jsonrpc;

import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.BindingActivator;
import org.apache.tuscany.sca.spi.InvokerFactory;
import org.apache.tuscany.sca.spi.ComponentLifecycle;
import org.osoa.sca.ServiceRuntimeException;

public class JSONRPCBindingActivator implements BindingActivator<JSONRPCBinding> {

    protected ServletHost servletHost;

    protected static final QName JSONRPC_BINDING_QN = new QName(SCA_NS, "binding.jsonrpc");

    public JSONRPCBindingActivator(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public QName getSCDLQName() {
        return JSONRPC_BINDING_QN;
    }

    public Class<JSONRPCBinding> getBindingClass() {
        return JSONRPCBinding.class;
    }

    public InvokerFactory createInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, JSONRPCBinding binding) {
        throw new ServiceRuntimeException("SCA reference support not yet implemented");
    }

    public ComponentLifecycle createService(RuntimeComponent rc, RuntimeComponentService rcs, JSONRPCBinding binding) {
        return new JSONRPCService(rc, rcs, binding, servletHost);
    }

}
