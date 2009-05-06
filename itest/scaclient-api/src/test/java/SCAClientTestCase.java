import java.net.URI;

import itest.HelloworldService;
import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * Test SCADomain.newInstance and invocation of a service.
 * 
 * @version $Rev$ $Date$
 */
public class SCAClientTestCase extends TestCase {

    private Node node;

    @Override
    protected void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode();
        node.start();
    }

    public void testInvoke() throws Exception {
        HelloworldService service = SCAClientFactory.newInstance().getService(HelloworldService.class, "HelloworldComponent", URI.create("default"));
        assertEquals("Hello petra", service.sayHello("petra"));
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
