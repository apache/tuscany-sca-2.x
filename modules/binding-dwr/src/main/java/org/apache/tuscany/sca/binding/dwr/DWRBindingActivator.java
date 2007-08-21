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

package org.apache.tuscany.sca.binding.dwr;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.BindingActivator;
import org.apache.tuscany.sca.spi.ComponentLifecycle;
import org.apache.tuscany.sca.spi.InvokerFactory;

public class DWRBindingActivator implements BindingActivator<DWRBinding>{

    private ServletHost servletHost;
    
    public DWRBindingActivator(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public Class<DWRBinding> getBindingClass() {
        return DWRBinding.class;
    }

    public InvokerFactory createInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, Binding b, DWRBinding ab) {
        return new DWRInvokerFactory(rc, rcr, b, ab, servletHost);
    }

    public ComponentLifecycle createService(RuntimeComponent rc, RuntimeComponentService rcs, Binding b, DWRBinding ab) {
        return new DWRService(rc, rcs, b, ab, servletHost);
    }

}
