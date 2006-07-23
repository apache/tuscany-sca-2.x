package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Implementation;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoaderExtensionTestCase extends TestCase {

    public void testRegistrationDeregistration() throws Exception {
        Extension loader = new Extension();
        LoaderRegistry registry = createMock(LoaderRegistry.class);
        registry.registerLoader(eq(Implementation.class), eq(loader));
        registry.unregisterLoader(eq(Implementation.class));
        EasyMock.replay(registry);
        loader.setLoaderRegistry(registry);
        loader.start();
        loader.stop();
    }


    private class Extension extends ComponentTypeLoaderExtension<Implementation> {

        protected Class<Implementation> getImplementationClass() {
            return Implementation.class;
        }

        public void load(CompositeComponent parent, Implementation implementation,
                         DeploymentContext deploymentContext) throws LoaderException {

        }
    }
}
