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
package org.apache.tuscany.binding.axis.config;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.binding.axis.context.ExternalWebServiceContext;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * A RuntimeConfiguration that handles External Web Services.
 * 
 * @version $Rev: 380065 $ $Date: 2006-02-22 23:50:11 -0800 (Wed, 22 Feb 2006) $
 */
public class ExternalWebServiceRuntimeConfiguration implements RuntimeConfiguration<ExternalServiceContext> {

    private String name;

    /**
     * Creates the runtime configuration
     * 
     * @param name the SCDL name of the external service the context refers to
     */
    public ExternalWebServiceRuntimeConfiguration(String name) {
        assert (name != null) : "Name was null";
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public ExternalServiceContext createInstanceContext() throws ContextCreationException {
        PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
        return new ExternalWebServiceContext(name, objectFactory, objectFactory);
    }

    private Map<String, ProxyFactory> targetProxyFactories = new HashMap();

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        targetProxyFactories.put(serviceName, factory);
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return targetProxyFactories.get(serviceName);
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return targetProxyFactories;
    }

    private Map<String, ProxyFactory> sourceProxyFactories = new HashMap();

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        sourceProxyFactories.put(referenceName, factory);
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        return sourceProxyFactories.get(referenceName);
    }

    public Map<String, ProxyFactory> getSourceProxyFactories() {
        return sourceProxyFactories;
    }

    public void prepare() {
    }
  
}
