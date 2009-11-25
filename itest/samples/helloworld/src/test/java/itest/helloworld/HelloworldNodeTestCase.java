package itest.helloworld;


import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelloworldNodeTestCase {

    Node node;
    
    @Before
    public void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode(null, new String[]{"../../../samples/helloworld/target/sample-helloworld.jar"}).start();
    }

    @Test
    public void testHelloworld() {
        Helloworld service = node.getService(Helloworld.class, "HelloworldComponent");
        assertEquals("Hello petra", service.sayHello("petra"));
    }

    @After
    public void stop() {
        node.stop();
    }
}
