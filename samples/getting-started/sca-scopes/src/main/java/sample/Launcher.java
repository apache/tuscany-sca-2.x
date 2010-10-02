package sample;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class Launcher {
	
    public static void main(String[] args) {
        Node node = startRuntime();         
        Client client = node.getService(Client.class, "Client");
        client.run();                                           
        stopRuntime(node);
    }

    /**
     * Starts a Tuscany node with the predefined contribution.
     *
     * @return the running node
     */
    private static Node startRuntime() {
        String location = ContributionLocationHelper.getContributionLocation("scopes.composite");
        Node node = NodeFactory.newInstance().createNode("scopes.composite", new Contribution("c1", location));
        node.start();
        return node;
    }

    /**
     * Stops a Tuscany node.
     *
     * @param node the node to stop
     */
    private static void stopRuntime(Node node) {
        node.stop();
    }

}
