package itest;



import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.Test;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class MyClientTestCase {

    @Test
    public void testCalculator() throws Exception {       
        
        SCANode serviceNode = SCANodeFactory.newInstance().createSCANode("MyService.composite", new SCAContribution("bla2", "../service/target/itest-binding-sca-jms-service.jar"));
        
        SCANode clientNode = SCANodeFactory.newInstance().createSCANodeFromClassLoader("MyClient.composite", null);
        try {

            serviceNode.start();
            clientNode.start();

            MyService service = ((SCAClient)clientNode).getService(MyService.class, "MyClientComponent");

            Assert.assertEquals("Hi Hello petra", service.sayHello("petra"));

        } finally {
            clientNode.stop();
            serviceNode.stop();
        }
    }
}
