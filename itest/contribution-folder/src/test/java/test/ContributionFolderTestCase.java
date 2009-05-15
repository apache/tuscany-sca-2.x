package test;

import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

import calculator.CalculatorService;

/**
 * Test SCADomain.newInstance and invocation of a service.
 *
 * @version $Rev: 608205 $ $Date: 2008-01-02 20:29:05 +0000 (Wed, 02 Jan 2008) $
 */
public class ContributionFolderTestCase extends TestCase {

    private Node node;

    @Override
    protected void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode(new Contribution("foo", "src/test/resources/repository"));
        node.start();
    }

    public void testInvoke() throws Exception {
        CalculatorService service = node.getService(CalculatorService.class, "CalculatorServiceComponent");
        assertEquals(3.0, service.add(1, 2));
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
