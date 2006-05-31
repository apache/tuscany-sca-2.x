package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Creates a {@link SpringCompositeContext} from an assembly model
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilder extends ComponentBuilderExtension<SpringImplementation> {


    public ComponentContext build(CompositeContext parent, Component<SpringImplementation> component,
                                  DeploymentContext deploymentContext) throws BuilderConfigException {
        String name = component.getName();
        GenericApplicationContext applicationContext = component.getImplementation().getApplicationContext();
        SpringCompositeContext context = new SpringCompositeContext(name, applicationContext, parent);
        CompositeComponentType<BoundService, BoundReference, ? extends Property> componentType = component.getImplementation().getComponentType();
        for (BoundService service : componentType.getServices().values()) {
            // call back into deployment context to handle building of services
            ServiceContext<?> serviceContext = (ServiceContext) builderRegistry.build(parent,
                    service,
                    deploymentContext);
            // wire service to bean invokers
            InboundWire<?> wire = serviceContext.getInboundWire();
            QualifiedName targetName = new QualifiedName(service.getTarget().getPath());
            for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
                chain.setTargetInvoker(context.createTargetInvoker(targetName.getPartName(), chain.getMethod()));
            }
            context.registerContext(serviceContext);
        }
        // TODO is this correct?
        for (ReferenceTarget target : component.getReferenceTargets().values()) {
            Reference reference = target.getReference();
            if (reference instanceof BoundReference) {
                // call back into deployment context to handle building of references
                context.registerContext(builderRegistry.build(parent, (BoundReference<? extends Binding>)
                        reference, deploymentContext));
            }
        }
        return context;
    }

    protected Class<SpringImplementation> getImplementationType() {
        return SpringImplementation.class;
    }
}
