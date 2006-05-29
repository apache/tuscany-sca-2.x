package org.apache.tuscany.container.spring;

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
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.ServiceWire;
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
        CompositeComponentType componentType = component.getImplementation().getComponentType();
        for (Service service : componentType.getServices().values()) {
            if (service instanceof BoundService) {
                // call back into deployment context to handle building of services
                ServiceContext<?> childContext = (ServiceContext) builderRegistry.build(parent,
                        (BoundService<? extends Binding>) service,
                        deploymentContext);
                // wire service to bean invokers
                ServiceWire<?> wire = childContext.getWire();
                for (ServiceInvocationChain chain : wire.getInvocationChains().values()) {
                    String beanName = wire.getTargetName().getPartName();
                    chain.setTargetInvoker(context.createTargetInvoker(beanName, chain.getMethod()));
                }
                context.registerContext(childContext);
            }
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
