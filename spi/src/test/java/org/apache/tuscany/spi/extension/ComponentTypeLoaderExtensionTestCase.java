package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.component.CompositeComponent;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoaderExtensionTestCase extends MockObjectTestCase {

    public void testRegistrationDeregistration() throws Exception {
        Mock mock = mock(LoaderRegistry.class);
        mock.expects(once()).method("registerLoader");
        mock.expects(once()).method("unregisterLoader");
        LoaderRegistry registry = (LoaderRegistry) mock.proxy();
        Extension loader = new Extension();
        loader.setLoaderRegistry(registry);
        loader.start();
        loader.stop();
    }


    private class Extension extends ComponentTypeLoaderExtension {

        protected Class getImplementationClass() {
            return null;
        }

        public void load(CompositeComponent parent, Implementation implementation,
                         DeploymentContext deploymentContext) throws LoaderException {

        }
    }
}
