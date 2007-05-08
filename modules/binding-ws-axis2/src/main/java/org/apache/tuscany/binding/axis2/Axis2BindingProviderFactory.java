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
package org.apache.tuscany.binding.axis2;

import org.apache.tuscany.binding.ws.impl.WebServiceBindingImpl;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.http.ServletHost;
import org.apache.tuscany.provider.BindingProviderFactory;
import org.apache.tuscany.provider.ReferenceBindingProvider;
import org.apache.tuscany.provider.ServiceBindingProvider;

/**
 * Axis2BindingProviderFactory
 *
 * @version $Rev$ $Date$
 */

public class Axis2BindingProviderFactory extends WebServiceBindingImpl implements BindingProviderFactory {

    private ServletHost servletHost;

    public Axis2BindingProviderFactory(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference) {
        return new Axis2ReferenceBindingProvider(component, reference, this);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service) {
        return new Axis2ServiceBindingProvider(component, service, this, servletHost);
    }
}
