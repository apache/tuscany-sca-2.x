package launch;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;

public class LaunchAmazonCart {

	public static void main(String[] args) throws Exception {
     
	System.out.println("Starting ...");
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCANode node = nodeFactory.createSCANode(null, "http://localhost:9999");
        
        URL contribution = SCAContributionUtil.findContributionFromClass(LaunchAmazonCart.class);
        node.addContribution("http://amazonCart", contribution);
        
        node.addToDomainLevelComposite(new QName("http://amazonCart", "amazonCart"));
        node.start();

        System.out.println("amazoncart.composite ready for big business !!!");
        System.in.read();
        
        System.out.println("Stopping ...");
        node.stop();
        node.destroy();
        System.out.println();
    }
	
}
