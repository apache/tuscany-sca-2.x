package itest.helloworld;


import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.domain.node.DomainNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;

public class HelloworldDomainNodeTestCase {

    DomainNode node;
    
    @Before
    public void setUp() throws Exception {
        node = new DomainNode("../../../samples/helloworld/target/sample-helloworld.jar");
    }

    @Test
    public void testHelloworld() throws NoSuchServiceException {
        assertEquals(1, node.getServiceNames().size());
        assertEquals("HelloworldComponent/Helloworld", node.getServiceNames().get(0));
        Helloworld service = node.getService(Helloworld.class, "HelloworldComponent");
        assertEquals("Hello petra", service.sayHello("petra"));
    }

    @After
    public void stop() {
        node.stop();
    }
}
