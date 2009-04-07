

import itest.HelloworldService;
import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Node;
import org.oasisopen.sca.client.SCAClient;
import org.oasisopen.sca.client.SCAClientFactory;



/**
 * Test SCADomain.newInstance and invocation of a service.
 * 
 * @version $Rev$ $Date$
 */
public class SCAClientTestCase extends TestCase {

    private SCAClient scaClient;
    
    @Override
    protected void setUp() throws Exception {
        scaClient = SCAClientFactory.newInstance();
    }

    public void testInvoke() throws Exception {
        HelloworldService service = scaClient.getService(HelloworldService.class, "HelloworldComponent", null);
        assertEquals("Hello petra", service.sayHello("petra"));
    }

    @Override
    protected void tearDown() throws Exception {
        ((Node)scaClient).stop();
    }

}
