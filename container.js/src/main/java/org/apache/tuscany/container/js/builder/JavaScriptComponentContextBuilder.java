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
package org.apache.tuscany.container.js.builder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptImplementationImpl;
import org.apache.tuscany.container.js.config.JavaScriptComponentRuntimeConfiguration;
import org.apache.tuscany.container.js.rhino.RhinoScript;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ModelInitException;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Builds {@link org.apache.tuscany.container.js.config.JavaScriptComponentRuntimeConfiguration}s from a JavaScript
 * component type
 * 
 * @version $Rev$ $Date$
 */
public class JavaScriptComponentContextBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

    private ProxyFactoryFactory factory;

    private MessageFactory msgFactory;

    private RuntimeConfigurationBuilder referenceBuilder;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JavaScriptComponentContextBuilder() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.factory = factory;
    }

    /**
     * Sets the factory used to construct invocation messages
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.msgFactory = msgFactory;
    }

    /**
     * Sets a builder responsible for creating source-side and target-side invocation chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and invocation chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setReferenceBuilder(RuntimeConfigurationBuilder builder) {
        this.referenceBuilder = builder;
    }

    public void build(AssemblyModelObject modelObject, AggregateContext context) throws BuilderException {
        if (modelObject instanceof SimpleComponent) {
            SimpleComponent component = (SimpleComponent) modelObject;
            ComponentImplementation impl = component.getComponentImplementation();
            if (impl instanceof JavaScriptImplementation) {
                Scope scope = ((JavaScriptImplementation) impl).getComponentType().getServices().get(0).getServiceContract()
                        .getScope();
                Map<String, Class> services = new HashMap();
                for (Service service : ((JavaScriptImplementation) impl).getComponentType().getServices()) {
                    services.put(service.getName(), service.getServiceContract().getInterface());
                }
                Map<String, Object> properties = new HashMap();
                // TODO support properties
                String script = null;
                if (impl instanceof JavaScriptImplementationImpl) { // fixme
                    try {
                        script = ((JavaScriptImplementationImpl) impl).getScript();
                    } catch (ModelInitException e) {
                        throw new BuilderConfigException(e);
                    }
                }

                RhinoScript invoker = createRhinoInvoker(component.getName(), script, properties);
                JavaScriptComponentRuntimeConfiguration config = new JavaScriptComponentRuntimeConfiguration(component.getName(),
                        scope, services, properties, invoker);

                // create target-side invocation chains for each service offered by the implementation
                for (ConfiguredService configuredService : component.getConfiguredServices()) {
                    Service service = configuredService.getService();
                    ServiceContract contract = service.getServiceContract();
                    Map<Method, InvocationConfiguration> iConfigMap = new HashMap();
                    ProxyFactory proxyFactory = factory.createProxyFactory();
                    for (Method method : contract.getInterface().getMethods()) {
                        InvocationConfiguration iConfig = new InvocationConfiguration(method);
                        iConfigMap.put(method, iConfig);
                    }
                    QualifiedName qName = new QualifiedName(component.getName() + "/" + service.getName());
                    ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, msgFactory);
                    proxyFactory.setBusinessInterface(contract.getInterface());
                    proxyFactory.setProxyConfiguration(pConfiguration);
                    configuredService.setProxyFactory(proxyFactory);
                    if (referenceBuilder != null) {
                        // invoke the reference builder to handle target-side metadata
                        referenceBuilder.build(configuredService, context);
                    }
                    // add tail interceptor
                    for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
                        iConfig.addTargetInterceptor(new InvokerInterceptor());
                    }
                    config.addTargetProxyFactory(service.getName(), proxyFactory);
                }

                // handle references
                List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
                if (configuredReferences != null) {
                    for (ConfiguredReference reference : configuredReferences) {
                        ProxyFactory proxyFactory = factory.createProxyFactory();
                        ServiceContract interfaze = reference.getReference().getServiceContract();
                        Map<Method, InvocationConfiguration> iConfigMap = new HashMap();
                        for (Method method : interfaze.getInterface().getMethods()) {
                            InvocationConfiguration iConfig = new InvocationConfiguration(method);
                            iConfigMap.put(method, iConfig);
                        }
                        String targetCompName = reference.getTargetConfiguredServices().get(0).getAggregatePart().getName();
                        String targetSerivceName = reference.getTargetConfiguredServices().get(0).getService().getName();

                        QualifiedName qName = new QualifiedName(targetCompName + "/" + targetSerivceName);

                        // QualifiedName qName = new QualifiedName(reference.getPart().getName() + "/"
                        // + reference.getPort().getName());
                        ProxyConfiguration pConfiguration = new ProxyConfiguration(qName, iConfigMap, null, msgFactory);
                        proxyFactory.setBusinessInterface(interfaze.getInterface());
                        proxyFactory.setProxyConfiguration(pConfiguration);
                        reference.setProxyFactory(proxyFactory);
                        if (referenceBuilder != null) {
                            // invoke the reference builder to handle metadata associated with the reference
                            referenceBuilder.build(reference, context);
                        }
                        config.addSourceProxyFactory(reference.getReference().getName(), proxyFactory);
                    }
                }
                component.getComponentImplementation().setRuntimeConfiguration(config);
            }
        }
    }

    /**
     * Creates a representation of the JavaScript implementation script that is used to perform invocations
     * 
     * @param name
     * @param script the Script source
     * @param properties configured properties for the component
     * @return
     */
    private RhinoScript createRhinoInvoker(String name, String script, Map properties) {
        RhinoScript ri = new RhinoScript(name, script, properties);
        return ri;
    }
}
