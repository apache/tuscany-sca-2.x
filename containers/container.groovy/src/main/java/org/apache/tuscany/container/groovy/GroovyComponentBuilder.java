package org.apache.tuscany.container.groovy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Extension point for creating {@link GroovyAtomicComponent}s from an assembly configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    public Component<?> build(CompositeComponent<?> parent,
                              ComponentDefinition<GroovyImplementation> componentDefinition,
                              DeploymentContext deploymentContext)
        throws BuilderConfigException {

        String name = componentDefinition.getName();
        GroovyImplementation implementation = componentDefinition.getImplementation();
        GroovyComponentType componentType = implementation.getComponentType();

        int initLevel = componentType.getInitLevel();

        // get list of services provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // get the scope container for this component's scope
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(componentType.getLifecycleScope());

        // get the Groovy classloader for this deployment context
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) deploymentContext.getExtension("groovy.classloader");
        if (groovyClassLoader == null) {
            groovyClassLoader = new GroovyClassLoader(deploymentContext.getClassLoader());
            deploymentContext.putExtension("groovy.classloader", groovyClassLoader);
        }

        // create the implementation class for the script
        Class<? extends GroovyObject> groovyClass;
        try {
            String script = implementation.getScript();
            // REVIEW JFM can we cache the class?
            groovyClass = groovyClassLoader.parseClass(script);
        } catch (CompilationFailedException e) {
            BuilderConfigException bce = new BuilderConfigException(e);
            bce.setIdentifier(name);
            throw bce;
        }
        // TODO deal with init and destroy

        // TODO set up injectors
        //List<PropertyInjector> injectors = Collections.emptyList();

        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName(name);
        configuration.setGroovyClass(groovyClass);
        configuration.setParent(parent);
        configuration.setScopeContainer(scopeContainer);
        configuration.setWireService(wireService);
        configuration.setWorkContext(workContext);
        configuration.setInitLevel(initLevel);
        configuration.setServices(services);
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);

        // handle properties
        for (Property<?> property : componentType.getProperties().values()) {
            ObjectFactory<?> factory = property.getDefaultValueFactory();
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }

//        for (ServiceDefinition service : componentType.getServices().values()) {
//            // TODO handle callbacks
//             Callback callback = service. getCallbackReference();
//             if (callback != null) {
//                // Only if there is a callback reference in the service
//                configuration.addCallbackSite(callback.getName(), callback.getMember());
//             }
//             component.addInboundWire(createWire(service));
//        }

        // handle references
        for (ReferenceTarget referenceTarget : componentDefinition.getReferenceTargets().values()) {
            Map<String, ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition referenceDefinition = references.get(referenceTarget.getReferenceName());
            OutboundWire wire = createWire(referenceTarget, referenceDefinition);
            component.addOutboundWire(wire);
        }
        return component;
    }

    @SuppressWarnings("unchecked")
    private OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
        //TODO multiplicity
        if (reference.getTargets().size() != 1) {
            throw new UnsupportedOperationException();
        }
        Class<?> interfaze = def.getServiceContract().getInterfaceClass();
        OutboundWire wire = wireService.createOutboundWire();
        wire.setTargetName(new QualifiedName(reference.getTargets().get(0).toString()));
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(reference.getReferenceName());
        for (Method method : interfaze.getMethods()) {
            //TODO handle policy
            OutboundInvocationChain chain = wireService.createOutboundChain(method);
            wire.addInvocationChain(method, chain);
        }
        // TODO handle callback
//        ServiceContract contract = def.getServiceContract();
//        Class<?> callbackInterface = contract.getCallbackClass();
//        if (callbackInterface != null) {
//            wire.setCallbackInterface(callbackInterface);
//            for (Method callbackMethod : callbackInterface.getMethods()) {
//                InboundInvocationChain callbackTargetChain = wireService.createInboundChain(callbackMethod);
//                OutboundInvocationChain callbackSourceChain = wireService.createOutboundChain(callbackMethod);
//                // TODO handle policy
//                //TODO statement below could be cleaner
//                callbackTargetChain.addInterceptor(new InvokerInterceptor());
//                wire.addTargetCallbackInvocationChain(callbackMethod, callbackTargetChain);
//                wire.addSourceCallbackInvocationChain(callbackMethod, callbackSourceChain);
//            }
//        }
        return wire;
    }

    @SuppressWarnings("unchecked")
    private InboundWire createWire(ServiceDefinition service) {
        Class<?> interfaze = service.getServiceContract().getInterfaceClass();
        InboundWire wire = wireService.createInboundWire();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(service.getName());
        for (Method method : interfaze.getMethods()) {
            InboundInvocationChain chain = wireService.createInboundChain(method);
            // TODO handle policy
            //TODO statement below could be cleaner
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(method, chain);
        }
        //TODO handle callback
//         ServiceContract contract = service.getServiceContract();
//         Class<?> callbackInterface = contract.getCallbackClass();
//         if (callbackInterface != null) {
//             wire.setCallbackReferenceName(service.getCallbackReference().getName());
//         }
        return wire;
    }


}
