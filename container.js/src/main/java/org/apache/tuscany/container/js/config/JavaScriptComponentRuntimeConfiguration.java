/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.js.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.context.JavaScriptComponentContext;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.SimpleComponent;

public class JavaScriptComponentRuntimeConfiguration implements RuntimeConfiguration<SimpleComponentContext> {

	private int scope;
    private SimpleComponent component;
    private JavaScriptImplementation implementation;
    
	public JavaScriptComponentRuntimeConfiguration(SimpleComponent component, JavaScriptImplementation implementation) {
		this.component = component;
		this.implementation = implementation;
        this.scope = implementation.getServices().get(0).getInterfaceContract().getScope().getValue();
	}

	public SimpleComponentContext createInstanceContext() throws ContextCreationException {
        return new JavaScriptComponentContext(component, implementation);
	}

	public int getScope() {
		return scope;
	}

	public String getName() {
		return component.getName();
	}

    private Map<String, ProxyFactory> targetProxyFactories = new HashMap<String, ProxyFactory>();

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        targetProxyFactories.put(serviceName, factory);
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return targetProxyFactories.get(serviceName);
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return targetProxyFactories;
    }

    private Map<String, ProxyFactory> sourceProxyFactories = new HashMap<String, ProxyFactory>();

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        sourceProxyFactories.put(referenceName, factory);
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        return sourceProxyFactories.get(referenceName);
    }

    public Map<String, ProxyFactory> getSourceProxyFactories() {
        return sourceProxyFactories;
    }

    public void prepare(){
        
    }
        
}
