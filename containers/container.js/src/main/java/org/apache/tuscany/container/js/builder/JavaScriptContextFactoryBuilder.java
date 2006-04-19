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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.config.JavaScriptContextFactory;
import org.apache.tuscany.container.js.rhino.RhinoE4XScript;
import org.apache.tuscany.container.js.rhino.RhinoScript;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Init;

import commonj.sdo.helper.TypeHelper;

/**
 * Builds {@link org.apache.tuscany.container.js.config.JavaScriptContextFactory}s from a JavaScript
 * component type
 * 
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaScriptContextFactoryBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private ProxyFactoryFactory factory;

    private MessageFactory msgFactory;

    private ContextFactoryBuilder referenceBuilder;

    public JavaScriptContextFactoryBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.factory = factory;
    }

    /**
     * Sets the factory used to construct wire messages
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.msgFactory = msgFactory;
    }

    /**
     * Sets a builder responsible for creating source-side and target-side wire chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and wire chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setReferenceBuilder(ContextFactoryBuilder builder) {
        this.referenceBuilder = builder;
    }

    public void build(AssemblyObject modelObject) throws BuilderException {
        if (modelObject instanceof AtomicComponent) {
            AtomicComponent component = (AtomicComponent) modelObject;
            Implementation impl = component.getImplementation();
            if (impl instanceof JavaScriptImplementation) {
                buildJavaScriptComponent(component, (JavaScriptImplementation) impl);
            }
        }
    }

	private void buildJavaScriptComponent(AtomicComponent component, JavaScriptImplementation impl) {

        Scope scope = impl.getComponentInfo().getServices().get(0).getServiceContract().getScope();

        Map<String, Class> services = new HashMap<String, Class>();
		for (Service service : impl.getComponentInfo().getServices()) {
		    services.put(service.getName(), service.getServiceContract().getInterface());
		}

        Map<String, Object> defaultProperties = new HashMap<String, Object>();
        for (org.apache.tuscany.model.assembly.Property property: impl.getComponentInfo().getProperties()) {
            defaultProperties.put(property.getName(), property.getDefaultValue());
        }

		String script = impl.getScript();
        ClassLoader cl = impl.getResourceLoader().getClassLoader();
        
        RhinoScript invoker;
        if ("e4x".equalsIgnoreCase(impl.getStyle())) {  // TODO is constant "e4x" somewhere?
            TypeHelper typeHelper = component.getComposite().getAssemblyContext().getTypeHelper();
            invoker = new RhinoE4XScript(component.getName(), script, defaultProperties, cl, typeHelper);
        } else {
            invoker = new RhinoScript(component.getName(), script, defaultProperties, cl);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
        if (configuredProperties != null) {
            for (ConfiguredProperty property : configuredProperties) {
                properties.put(property.getProperty().getName(), property.getValue());
            }
        }

        JavaScriptContextFactory contextFactory = new JavaScriptContextFactory(component.getName(),
		        scope, services, properties, invoker);

		addTargetInvocationChains(component, contextFactory);
		addComponentReferences(component, contextFactory);
		component.setContextFactory(contextFactory);
	}

    /**
     * Add target-side wire chains for each service offered by the implementation
     */
	private void addTargetInvocationChains(AtomicComponent component, JavaScriptContextFactory config) {
		for (ConfiguredService configuredService : component.getConfiguredServices()) {
		    Service service = configuredService.getPort();
		    ServiceContract contract = service.getServiceContract();
		    Map<Method, TargetInvocationConfiguration> iConfigMap = new MethodHashMap<TargetInvocationConfiguration>();
		    TargetWireFactory proxyFactory = factory.createTargetWireFactory();
		    for (Method method : contract.getInterface().getMethods()) {
		        TargetInvocationConfiguration iConfig = new TargetInvocationConfiguration(method);
		        iConfigMap.put(method, iConfig);
		    }
            QualifiedName qName = new QualifiedName(component.getName() + QualifiedName.NAME_SEPARATOR
                    + service.getName());
            WireTargetConfiguration wireConfiguration = new WireTargetConfiguration(qName, iConfigMap, contract.getInterface()
                    .getClassLoader(), msgFactory);
		    proxyFactory.setBusinessInterface(contract.getInterface());
		    proxyFactory.setConfiguration(wireConfiguration);
		    configuredService.setProxyFactory(proxyFactory);
		    if (referenceBuilder != null) {
		        // invoke the reference builder to handle target-side metadata
		        referenceBuilder.build(configuredService);
		    }
		    // add tail interceptor
		    for (TargetInvocationConfiguration iConfig : iConfigMap.values()) {
		        iConfig.addInterceptor(new InvokerInterceptor());
		    }
		    config.addTargetProxyFactory(service.getName(), proxyFactory);
		}
	}

	private void addComponentReferences(AtomicComponent component, JavaScriptContextFactory config) {
		List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
		if (configuredReferences != null) {
		    for (ConfiguredReference reference : configuredReferences) {
		        SourceWireFactory proxyFactory = factory.createSourceWireFactory();
		        ServiceContract interfaze = reference.getPort().getServiceContract();
		        Map<Method, SourceInvocationConfiguration> iConfigMap = new MethodHashMap<SourceInvocationConfiguration>();
		        for (Method method : interfaze.getInterface().getMethods()) {
		            SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
		            iConfigMap.put(method, iConfig);
		        }
		        String targetCompName = reference.getTargetConfiguredServices().get(0).getPart().getName();
		        String targetSerivceName = reference.getTargetConfiguredServices().get(0).getPort().getName();

		        QualifiedName qName = new QualifiedName(targetCompName + '/' + targetSerivceName);
                WireSourceConfiguration pConfiguration = new WireSourceConfiguration(reference.getPort().getName(), qName,
                        iConfigMap, interfaze.getInterface().getClassLoader(), msgFactory);
		        proxyFactory.setBusinessInterface(interfaze.getInterface());
		        proxyFactory.setConfiguration(pConfiguration);
                //FIXME multiplicity support
                reference.getTargetConfiguredServices().get(0).setProxyFactory(proxyFactory);
		        if (referenceBuilder != null) {
		            // invoke the reference builder to handle metadata associated with the reference
		            referenceBuilder.build(reference);
		        }
		        config.addSourceProxyFactory(reference.getPort().getName(), proxyFactory);
		    }
		}
	}

}
