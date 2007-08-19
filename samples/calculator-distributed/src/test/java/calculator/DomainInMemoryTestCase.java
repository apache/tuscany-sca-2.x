package calculator;


import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.domain.impl.DistributedSCADomainMemoryImpl;
import org.apache.tuscany.sca.distributed.node.impl.EmbeddedNode;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.CalculatorService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain
 */
public class DomainInMemoryTestCase {
    
    private static String DEFULT_DOMAIN_NAME = "mydomain";

    private static DistributedSCADomain distributedDomain;
    private static EmbeddedNode nodeA;
    private static SCADomain domainA;
    private static EmbeddedNode nodeB;
    private static SCADomain domainB;
    private static EmbeddedNode nodeC;
    private static SCADomain domainC;    
    private static CalculatorService calculatorServiceA;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up distributed nodes");
        
        File currentDirectory = new File (".");
        URL contributionURL = null;

        // Create the distributed domain representation
        distributedDomain = new DistributedSCADomainMemoryImpl(DEFULT_DOMAIN_NAME);
        
        // create the node that runs the 
        // calculator component
        nodeA = new EmbeddedNode("nodeA");
        domainA = nodeA.attachDomain(distributedDomain); 
        contributionURL = Thread.currentThread().getContextClassLoader().getResource("nodeA/");
        nodeA.addContribution(DEFULT_DOMAIN_NAME, contributionURL);

        // create the node that runs the 
        // add component
        nodeB = new EmbeddedNode("nodeB");
        domainB = nodeB.attachDomain(distributedDomain);
        contributionURL = Thread.currentThread().getContextClassLoader().getResource("nodeB/");
        nodeB.addContribution(DEFULT_DOMAIN_NAME, contributionURL);         
 
        // create the node that runs the 
        // subtract component      
        nodeC = new EmbeddedNode("nodeC");
        domainC = nodeC.attachDomain(distributedDomain);
        contributionURL = Thread.currentThread().getContextClassLoader().getResource("nodeC/");
        nodeC.addContribution(DEFULT_DOMAIN_NAME, contributionURL);  
     
        
        // start all of the nodes
        nodeA.start();
        nodeB.start();
        nodeC.start();
        
        // get a reference to the calculator service from domainA
        // which will be running this component
        calculatorServiceA = domainA.getService(CalculatorService.class, "CalculatorServiceComponent");     
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        nodeA.stop();
        nodeB.stop();    
        nodeC.stop();
    }

    @Test
    public void testCalculator() throws Exception {       
        
        // Calculate
        Assert.assertEquals(calculatorServiceA.add(3, 2), 5.0);
        Assert.assertEquals(calculatorServiceA.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorServiceA.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorServiceA.divide(3, 2), 1.5);

        
    }
}
