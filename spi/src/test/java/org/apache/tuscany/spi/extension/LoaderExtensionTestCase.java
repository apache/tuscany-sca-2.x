package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

/**
 * @version $Rev$ $Date$
 */
public class LoaderExtensionTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testRegistrationDeregistration() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.registerLoader(isA(QName.class), isA(Extension.class));
        expectLastCall();
        registry.unregisterLoader(isA(QName.class), isA(Extension.class));
        expectLastCall();
        EasyMock.replay(registry);
        Extension loader = new Extension(registry);
        loader.start();
        loader.stop();
    }


    private static class Extension extends LoaderExtension {

        public Extension(LoaderRegistry registry) {
            super(registry);
        }

        public QName getXMLType() {
            return new QName("");
        }

        public ModelObject load(CompositeComponent parent,
                                XMLStreamReader reader,
                                DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
            throw new AssertionError();
        }
    }
}
