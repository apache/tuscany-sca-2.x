package itest;



import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.Test;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class MyServiceTestCase {

    @Test
    public void testCalculator() throws Exception {       
        
        SCANode node = SCANodeFactory.newInstance().createSCANodeFromClassLoader("MyService.composite", null);
        try {

            node.start();

            MyService service = ((SCAClient)node).getService(MyService.class, "MyServiceComponent");

            Assert.assertEquals("Hello petra", service.sayHello("petra"));

        } finally {
            node.stop();
        }
    }
}
