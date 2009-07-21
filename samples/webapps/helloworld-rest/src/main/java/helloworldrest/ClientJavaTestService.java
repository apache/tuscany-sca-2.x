package helloworldrest;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/*
 * To test, simply run the program
 * Access the service by invoking the getName() method of HelloWorldService
 */

public class ClientJavaTestService {

    /**
     * @param args
     */
    public static void main(String[] args) {
        NodeFactory factory = NodeFactory.newInstance();
        Node node = factory.createNode("rest.composite", ClientJavaTestService.class.getClassLoader()).start();
        HelloWorldService helloService = node.getService(HelloWorldService.class, "HelloWorldRESTServiceComponent");

        //HelloWorldService helloService = new HelloWorldServiceImpl();
        System.out.println("### Message from REST service " + helloService.getName());

        node.stop();
        node.destroy();
        factory.destroy();
    }

}
