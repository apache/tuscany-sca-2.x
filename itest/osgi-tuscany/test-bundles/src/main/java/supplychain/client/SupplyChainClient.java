package supplychain.client;


import java.io.File;
import java.net.URL;
import java.util.Hashtable;


import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



import supplychain.customer.Customer;

/**
 * SupplyChain test client
 */
public class SupplyChainClient implements BundleActivator {

    private EmbeddedSCADomain scaDomain;
    private Customer customer;
    
    

    public void start(BundleContext context) throws Exception {
        
        context.registerService(SupplyChainClient.class.getName(), this, new Hashtable());
        
    }

    public void stop(BundleContext context) throws Exception {
        
    }

    protected void setUp(String contributionJarName) throws Exception {
        
        scaDomain = new EmbeddedSCADomain(EmbeddedSCADomain.class.getClassLoader(), "http://localhost");
        scaDomain.start();
        ContributionService contributionService = scaDomain.getContributionService();
        String folderName = "../test-bundles/target/"; 
        String supplychainJarName = contributionJarName;
        URL supplyChainURL = new File(folderName + supplychainJarName).toURI().toURL();
        
        Contribution contribution = contributionService.contribute("SupplyChain", supplyChainURL, false);
        for (Composite deployable : contribution.getDeployables() ) {
            scaDomain.getDomainComposite().getIncludes().add(deployable);
            scaDomain.buildComposite(deployable);
        }
        
        for (Composite deployable : contribution.getDeployables() ) {
            scaDomain.getCompositeActivator().activate(deployable);
            scaDomain.getCompositeActivator().start(deployable);
        }
        customer = scaDomain.getService(Customer.class, "CustomerComponent");
    }
    
    protected void tearDown() throws Exception {
        if (scaDomain != null) {
            scaDomain.close();
            scaDomain = null;
        }
    }


    public void runTest(String contributionJarName) throws Exception {
        
        try {
            setUp(contributionJarName);
            customer.purchaseGoods();
            int retries = 10;
            int outstandingCount = 1;
            while (retries-- > 0) {

                outstandingCount = customer.outstandingOrderCount();
                if (outstandingCount == 0)
                    break;
                else
                    Thread.sleep(100);
            }
            if (outstandingCount != 0)
                throw new RuntimeException("Orders not processed on time");
            
        } finally {
            
            tearDown();
        }    
        
        
    }
    
    
}
