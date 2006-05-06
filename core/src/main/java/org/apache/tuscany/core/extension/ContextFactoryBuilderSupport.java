package org.apache.tuscany.core.extension;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.Init;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A runtime extension point for component types. Subclasses must be genericized according to the model implementation type they
 * handle, i.e. a subclass of {@link Implementation}, and implement {@link #createContextFactory}.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 * @see org.apache.tuscany.core.builder.ContextFactory
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ContextFactoryBuilderSupport<T extends Implementation> implements ContextFactoryBuilder {

    protected ContextFactoryBuilderRegistry builderRegistry;

    protected WireFactoryService wireFactoryService;

    protected Class implementationClass;

    /**
     * Default constructor
     */
    public ContextFactoryBuilderSupport() {
        // reflect the generic type of the subclass
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            implementationClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            throw new AssertionError("Subclasses of " + ContextFactoryBuilderSupport.class.getName() + " must be genericized");
        }
    }
    
    /**
     * Constructs a new instance
     *
     * @param wireFactoryService the system service responsible for creating wire factories
     */
    public ContextFactoryBuilderSupport(WireFactoryService wireFactoryService) {
        this();
        this.wireFactoryService = wireFactoryService;
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
        if (!(modelObject instanceof Component)) {
            return;
        }
        Component nonGenricComponent = (Component) modelObject;
        if (!implementationClass.isAssignableFrom(nonGenricComponent.getImplementation().getClass())) {
            return;
        }
        Component<T> component = (Component<T>) modelObject;
        List<Service> services = component.getImplementation().getComponentInfo().getServices();
        Scope previous = null;
        Scope scope = Scope.INSTANCE;
        for (Service service : services) {
            // calculate and validate the scope of the component; ensure that all service scopes are the same unless stateless
            Scope current = service.getServiceContract().getScope();
            if (previous != null && current != null && current != previous
                    && (current != Scope.INSTANCE && previous != Scope.INSTANCE)) {
                BuilderException e = new BuilderConfigException("Incompatible scopes specified for services on component");
                e.setIdentifier(component.getName());
                throw e;
            }
            if (scope != null && current != Scope.INSTANCE) {
                scope = current;
            }
        }
        ContextFactory contextFactory;
        try {
            contextFactory = createContextFactory(component.getName(), component.getImplementation(), scope);
            // create target-side wire invocation chains for each service offered by the implementation
            for (ConfiguredService configuredService : component.getConfiguredServices()) {
                Service service = configuredService.getPort();
                TargetWireFactory wireFactory = wireFactoryService.createTargetFactory(configuredService);
                contextFactory.addTargetWireFactory(service.getName(), wireFactory);
            }
            // handle properties
            List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();
            if (configuredProperties != null) {
                for (ConfiguredProperty property : configuredProperties) {
                    contextFactory.addProperty(property.getName(), property.getValue());
                }
            }
            // handle references and source side reference chains
            List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
            if (configuredReferences != null) {
                for (ConfiguredReference reference : configuredReferences) {
                    if (reference.getPort().getMultiplicity() == Multiplicity.ZERO_N || reference.getPort().getMultiplicity() == Multiplicity.ZERO_ONE){
                        if (reference.getTargetConfiguredServices().size() < 1 && reference.getTargets().size() <1 ){
                            continue; // not required, not configured fix TUSCANY-299 
                        }
                    }
                    List<SourceWireFactory> wireFactories = wireFactoryService.createSourceFactory(reference);
                    String refName = reference.getPort().getName();
                    Class refClass = reference.getPort().getServiceContract().getInterface();
                    boolean multiplicity = reference.getPort().getMultiplicity() == Multiplicity.ONE_N
                            || reference.getPort().getMultiplicity() == Multiplicity.ZERO_N;
                    contextFactory.addSourceWireFactories(refName, refClass, wireFactories, multiplicity);
                }
            }
            component.setContextFactory(contextFactory);
        } catch (BuilderException e) {
            e.addContextName(component.getName());
            throw e;
        }
    }

    /**
     * Subclasses must implement, returning a context factory appropriate to the component implementation
     *
     * @param componentName  the name of the component
     * @param implementation the component implementation
     * @param scope          the component implementation scope
     */
    protected abstract ContextFactory createContextFactory(String componentName, T implementation, Scope scope);

}
