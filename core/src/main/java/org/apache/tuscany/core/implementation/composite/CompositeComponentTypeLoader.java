package org.apache.tuscany.core.implementation.composite;

import java.net.URL;

import org.apache.tuscany.core.deployer.ChildDeploymentContext;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * Loads a composite component type
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentTypeLoader extends ComponentTypeLoaderExtension<CompositeImplementation> {
    public CompositeComponentTypeLoader() {
    }

    public CompositeComponentTypeLoader(LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    protected Class<CompositeImplementation> getImplementationClass() {
        return CompositeImplementation.class;
    }

    public void load(CompositeComponent<?> parent, CompositeImplementation implementation,
                     DeploymentContext deploymentContext)
        throws LoaderException {
        URL scdlLocation = implementation.getScdlLocation();
        ClassLoader cl = new CompositeClassLoader(implementation.getClassLoader());
        deploymentContext = new ChildDeploymentContext(deploymentContext, cl, scdlLocation);
        CompositeComponentType componentType = loadFromSidefile(parent, scdlLocation, deploymentContext);
        implementation.setComponentType(componentType);
    }

    protected CompositeComponentType loadFromSidefile(CompositeComponent<?> parent,
                                                      URL url,
                                                      DeploymentContext deploymentContext)
        throws LoaderException {
        return loaderRegistry.load(parent, url, CompositeComponentType.class, deploymentContext);
    }
}
