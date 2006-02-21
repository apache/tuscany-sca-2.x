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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.injection.ReferenceProxyTargetFactory;
import org.apache.tuscany.container.js.rhino.RhinoInvoker;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.SimpleComponent;

public class JavaScriptComponentContext extends AbstractContext implements
		SimpleComponentContext {

	private SimpleComponent component;
	private JavaScriptImplementation implementation;

	public JavaScriptComponentContext(SimpleComponent component,
			JavaScriptImplementation implementation) {
		super(component.getName());
		this.component = component;
		this.implementation = implementation;
	}

	public Object getInstance(QualifiedName qName) throws TargetException {
		return getInstance(qName, true);
	}

	public synchronized Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
		//TODO: should this cache the instance?
		ConfiguredService service = component.getConfiguredServices().get(0);
		String iface = service.getPort().getServiceContract().getInterface();
		Class[] ifaces;
		try {
			ifaces = new Class[] { implementation.getResourceLoader().loadClass(iface) };
		} catch (ClassNotFoundException e) {
			throw new TargetException(qName.getPartName() + ": ClassNotFoundException creating interface: " + iface);
		}
		Object proxy = createProxy(ifaces);

        notifyListeners(notify);
		
		return proxy;
	}

	private Object createProxy(Class[] ifaces) {

		final RhinoInvoker rhinoInvoker = implementation.getRhinoInvoker().copy();
		rhinoInvoker.updateScriptScope(createPropertyValues());

		InvocationHandler ih = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) {
				return rhinoInvoker.invoke(method.getName(), args, method.getReturnType(), createInvocationContext());
			}
		};

		Object proxy = Proxy.newProxyInstance(ifaces[0].getClassLoader(), ifaces, ih);

		return proxy;
	}
	
	private void notifyListeners(boolean notify) {
		if (notify) {
            for (Iterator iter = contextListener.iterator(); iter.hasNext();) {
                LifecycleEventListener listener = (LifecycleEventListener) iter.next();
                listener.onInstanceCreate(this);
            }
        }
	}

	/**
	 * Creates a map containing any properties and their values
	 */
	private Map createPropertyValues() {
		Map<String,Object> context = new HashMap<String,Object>();
		List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
        if (configuredProperties != null) {
            for (ConfiguredProperty property : configuredProperties) {
            	context.put(property.getProperty().getName(), property.getValue());
            }
        }
        return context;
	}

	/**
	 * Creates a map containing any ServiceReferences
	 */
	private Map createInvocationContext() {
		Map<String,Object> context = new HashMap<String,Object>();
        List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
        if (configuredReferences != null) {
            for (ConfiguredReference reference : configuredReferences) {
                ReferenceProxyTargetFactory rptf = new ReferenceProxyTargetFactory(reference);
                String refName = reference.getReference().getName();
                context.put(refName, rptf.getInstance());
            }
        }
        return context;
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
