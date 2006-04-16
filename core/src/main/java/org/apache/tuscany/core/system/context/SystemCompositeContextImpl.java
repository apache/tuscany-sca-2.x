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
package org.apache.tuscany.core.system.context;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.AutowireResolutionException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeStrategy;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.impl.AbstractCompositeContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.system.config.SystemObjectContextFactory;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;


/**
 * Implements an composite context for system components. By default a system context uses the scopes specified by
 * {@link org.apache.tuscany.core.system.context.SystemScopeStrategy}. In addition, it implements an autowire policy
 * where entry points configured with a {@link org.apache.tuscany.core.system.assembly.SystemBinding} are matched
 * according to their exposed interface. A system context may contain child composite contexts but an entry point in a
 * child context will only be outwardly accessible if there is an entry point that exposes it configured in the
 * top-level system context.
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeContextImpl extends AbstractCompositeContext implements SystemCompositeContext {
    public SystemCompositeContextImpl() {
        super();
        eventContext = new EventContextImpl();
        scopeStrategy = new SystemScopeStrategy();
    }

    public SystemCompositeContextImpl(String name,
                                      CompositeContext parent,
                                      AutowireContext autowire,
                                      ScopeStrategy strategy,
                                      EventContext ctx,
                                      ConfigurationContext configCtx
    ) {
        super(name, parent, strategy, ctx, configCtx);
        setAutowireContext(autowire);
        scopeIndex = new ConcurrentHashMap<String, ScopeContext>();
    }

    public void registerJavaObject(String componentName, Class<?> service, Object instance) throws ConfigurationException {
        SystemObjectContextFactory configuration = new SystemObjectContextFactory(componentName, instance);
        registerConfiguration(configuration);
        ScopeContext scope = scopeContexts.get(configuration.getScope());
        registerAutowireInternal(service, componentName, scope);
    }

    // FIXME These should be removed and configured
    private static final MessageFactory messageFactory = new MessageFactoryImpl();

    private static final ProxyFactoryFactory proxyFactoryFactory = new JDKProxyFactoryFactory();

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (CompositeContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        } else if (MessageFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(messageFactory);
        } else if (ProxyFactoryFactory.class.equals(instanceInterface)) {
            return instanceInterface.cast(proxyFactoryFactory);
        } else {
            return super.resolveInstance(instanceInterface);
        }
    }
}
