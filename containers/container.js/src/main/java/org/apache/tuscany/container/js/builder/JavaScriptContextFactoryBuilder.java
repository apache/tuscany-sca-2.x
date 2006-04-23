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

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.config.JavaScriptContextFactory;
import org.apache.tuscany.container.js.rhino.RhinoE4XScript;
import org.apache.tuscany.container.js.rhino.RhinoScript;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.Init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds {@link org.apache.tuscany.container.js.config.JavaScriptContextFactory}s from a JavaScript component type
 *
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaScriptContextFactoryBuilder implements ContextFactoryBuilder {

    private ContextFactoryBuilderRegistry builderRegistry;
    private WireFactoryService wireFactoryService;

    /**
     * Constructs a new instance
     *
     * @param wireFactoryService the system service responsible for creating wire factories
     */
    public JavaScriptContextFactoryBuilder(WireFactoryService wireFactoryService) {
        this.wireFactoryService = wireFactoryService;
    }

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
     * Sets the system service used to construct wire factories
     */
    @Autowire
    public void setWireFactoryService(WireFactoryService wireFactoryService) {
        this.wireFactoryService = wireFactoryService;
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
        for (org.apache.tuscany.model.assembly.Property property : impl.getComponentInfo().getProperties()) {
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
    private void addTargetInvocationChains(AtomicComponent component, JavaScriptContextFactory contextFactory) {
        for (ConfiguredService configuredService : component.getConfiguredServices()) {
            Service service = configuredService.getPort();
            TargetWireFactory wireFactory = wireFactoryService.createTargetFactory(configuredService);
            contextFactory.addTargetWireFactory(service.getName(), wireFactory);
        }
    }

    private void addComponentReferences(AtomicComponent component, JavaScriptContextFactory contextFactory) {
        List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
        if (configuredReferences != null) {
            for (ConfiguredReference reference : configuredReferences) {
                String refName = reference.getPort().getName();
                // iterate through the targets
                List<SourceWireFactory> wirefactories = wireFactoryService.createSourceFactory(reference);
                for (SourceWireFactory wireFactory : wirefactories) {
                    contextFactory.addSourceWireFactory(refName, wireFactory);
                }

            }
        }
    }

}
