package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;

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


    private class Extension extends LoaderExtension {

        protected QName getXMLType() {
            return null;
        }

        public ModelObject load(XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
            return null;
        }
    }
}
