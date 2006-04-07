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
package org.apache.tuscany.container.js.context;

import org.apache.tuscany.container.js.rhino.RhinoScript;
import org.apache.tuscany.core.context.*;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.osoa.sca.ServiceRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaScriptComponentContext extends AbstractContext implements AtomicContext {

    private Map<String, Class> services;

    private RhinoScript rhinoInvoker;

    private Map<String, Object> properties;

    private List<ProxyFactory> sourceProxyFactories;

    private Map<String, ProxyFactory> targetProxyFactories;

    private Object instance;

    public JavaScriptComponentContext(String name, Map<String, Class> services, Map<String, Object> properties,
            List sourceProxyFactories, Map<String, ProxyFactory> targetProxyFactories, RhinoScript invoker) {
        super(name);
        assert (services != null) : "No service interface mapping specified";
        assert (properties != null) : "No properties specified";
        this.services = services;
        this.properties = properties;
        this.rhinoInvoker = invoker;
        this.sourceProxyFactories = sourceProxyFactories;
        this.targetProxyFactories = targetProxyFactories;
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getInstance(qName, true);
    }

    public void init() throws TargetException{
        getInstance(null,false);
    }
    
    private synchronized Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        String portName=qName.getPortName();
        ProxyFactory targetFactory;
        if (portName!=null) {
            targetFactory = targetProxyFactories.get(portName);
        }  else {
            //FIXME The port name is null here, either locateService needs more information (the expected interface) to
            // select the correct port, or we need to return a factory that matches the whole set of services exposed by
            // the component.
            targetFactory = targetProxyFactories.values().iterator().next();
        }
        if (targetFactory == null) {
            TargetException e = new TargetException("Target service not found");
            e.setIdentifier(qName.getPortName());
            e.addContextName(getName());
            throw e;
        }
        try {
            Object proxy = targetFactory.createProxy(); //createProxy(new Class[] { iface });
            notifyListeners(notify);
            return proxy;
        } catch (ProxyCreationException e) {
            TargetException te = new TargetException("Error returning target", e);
            e.setIdentifier(qName.getPortName());
            e.addContextName(getName());
            throw te;
        }
    }

    public Object getImplementationInstance() throws TargetException {
        rhinoInvoker.updateScriptScope(createServiceReferences()); // create references
        rhinoInvoker.updateScriptScope(properties); // create prop values
        return rhinoInvoker;
    }

    private void notifyListeners(boolean notify) {
        if (notify) {
            for (RuntimeEventListener listener : listeners) {
                listener.onEvent(EventContext.CONTEXT_CREATED,this);
            }
        }
    }

    /**
     * Creates a map containing any ServiceReferences
     */
    private Map createServiceReferences() {
        try {
            Map<String, Object> context = new HashMap<String, Object>();
            for (ProxyFactory proxyFactory : sourceProxyFactories) {
                context.put(proxyFactory.getProxyConfiguration().getReferenceName(), proxyFactory.createProxy());
            }
            return context;
		} catch (ProxyCreationException e) {
			throw new ServiceRuntimeException(e);
		}
    }

    public boolean isEagerInit() {
        return false;
    }

    public boolean isDestroyable() {
        return false;
    }

    public void start() throws CoreRuntimeException {
    }

    public void stop() throws CoreRuntimeException {
    }

}
