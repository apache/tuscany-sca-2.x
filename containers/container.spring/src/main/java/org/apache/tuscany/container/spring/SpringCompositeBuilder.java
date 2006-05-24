package org.apache.tuscany.container.spring;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
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

/**
 * Creates a {@link SpringCompositeContext} from an assembly model
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilder extends ComponentBuilderExtension<SpringCompositeImplementation> {

    public ComponentContext build(CompositeContext parent, Component<SpringCompositeImplementation> component,
                                  DeploymentContext deploymentContext) throws BuilderConfigException {
        String name = component.getName();
        URL url;
        try {
            url = new URL(component.getImplementation().getContextPath());
        } catch (MalformedURLException e) {
            throw new BuilderConfigException(e);
        }
        SpringCompositeContext context = new SpringCompositeContext(name, url, parent);
        CompositeComponentType componentType = component.getImplementation().getComponentType();
        for (Service service : componentType.getServices().values()) {
            if (service instanceof BoundService) {
                // call back into deployment context to handle building of services
                context.registerContext(builderRegistry.build(parent, (BoundService<? extends Binding>) service,
                        deploymentContext));
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

    protected Class<SpringCompositeImplementation> getImplementationType() {
        return SpringCompositeImplementation.class;
    }
}
