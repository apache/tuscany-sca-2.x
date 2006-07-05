package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * @version $Rev$ $Date$
 */
public class LoaderExtensionTestCase extends MockObjectTestCase {

    public void testRegistrationDeregistration() throws Exception {
        Mock mock = mock(LoaderRegistry.class);
        mock.expects(once()).method("registerLoader");
        mock.expects(once()).method("unregisterLoader");
        LoaderRegistry registry = (LoaderRegistry) mock.proxy();
        LoaderExtensionTestCase.Extension loader = new LoaderExtensionTestCase.Extension();
        loader.setRegistry(registry);
        loader.start();
        loader.stop();
    }


    private static class Extension extends LoaderExtension {

        public QName getXMLType() {
            return null;
        }

        public ModelObject load(CompositeComponent parent,
                                XMLStreamReader reader,
                                DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
            return null;
        }
    }
}
