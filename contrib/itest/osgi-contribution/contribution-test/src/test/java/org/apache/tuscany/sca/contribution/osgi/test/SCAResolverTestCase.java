/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.contribution.osgi.test;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.osgi.OSGiTestUtil;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/*
 * 
 * Supplychain using OSGi contributions, using default SCA resolver
 * 
 *    This test uses two sets of four contributions where each set contains a bundle corresponding
 *    to the four components of supplychain (customer, retailer, warehouse and shipper).
 *    Tests classes from the bundle used in <implementation.java/>, and the bundles themselves
 *    used as the bundle in <implementation.osgi/>.
 * 
 * Notes:
 *    All OSGi bundle references should be resolvable using pure OSGi bundle mechanisms. No
 *    dummy bundles will be created to resolve combinations of OSGi and non-OSGi contributions
 *    
 *    Notes:
 *    All OSGi bundle references should be resolvable using pure OSGi bundle mechanisms. No
 *    dummy bundles will be created to resolve combinations of OSGi and non-OSGi contributions
 *    
 *    All dependent OSGi contribution bundles should be installed before the referring contribution.
 *    Hence dependencies should be a tree and cannot contain cycles.
 *    
 *    For Jar files contained within OSGi bundles, Bundle-Classpath should be
 *    set so that standard OSGi class resolution is sufficient to resolve classes containing in
 *    nested jars or bundles.
 */

public class SCAResolverTestCase extends TestCase {


    protected  EmbeddedSCADomain domain;
    
    private SupplyChain supplyChainV1;
    private SupplyChain supplyChainV2;
    
    
    protected String customerJarName = "Customer";
    protected String retailerJarName = "Retailer";
    protected String warehouseJarName = "Warehouse";
    protected String shipperJarName = "Shipper";
    
    
    
    @Override
    protected void setUp() throws Exception {
    
        setUpOSGi();
        setUpSCA();
    }
        
    protected void setUpOSGi() throws Exception {
       OSGiTestUtil.setUpFelixTestRuntime();
    }
    
    protected void setUpSCA() throws Exception {
        //Create a test embedded SCA domain
        ClassLoader cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();
        supplyChainV1 = new SupplyChain("../contribution-classes/target/classes",
                contributionService, "V1");
        supplyChainV1.setUpSCA();

        supplyChainV2 = new SupplyChain("../contribution-classes-v2/target/classes",
                contributionService, "V2");
        supplyChainV2.setUpSCA();
    }

	

	@Override
    public void tearDown() throws Exception {


        supplyChainV1.tearDownSCA();
        supplyChainV2.tearDownSCA();
        
        domain.stop();

        domain.close();
        
        OSGiTestUtil.shutdownOSGiRuntime();
	}

    
    @SuppressWarnings("unchecked")
    public void test() throws Exception {
        
        ClassReference customerClassRef = new ClassReference("supplychain.customer.Customer");
        customerClassRef = supplyChainV1.customerContribution.getModelResolver().resolveModel(ClassReference.class, customerClassRef);
        Class customerClass = customerClassRef.getJavaClass();
        
        Object customer = 
            domain.getService(customerClass, "CustomerComponent");
                
        Method m = customerClass.getMethod("purchaseGoods");
        m.invoke(customer);
        
        System.out.println("Sleeping ...");
        Thread.sleep(1000);
        
        ClassReference customerClassRefV2 = new ClassReference("supplychain.customer.Customer");
        customerClassRefV2 = supplyChainV2.customerContribution.getModelResolver().resolveModel(ClassReference.class, customerClassRefV2);
        Class customerClassV2 = customerClassRefV2.getJavaClass();
        
        Object customerV2 = 
            domain.getService(customerClassV2, "CustomerComponentV2");
                
        Method mV2 = customerClassV2.getMethod("purchaseGoods");
        mV2.invoke(customerV2);
        
        System.out.println("Sleeping ...");
        Thread.sleep(2000);
        
        System.out.println("Test complete");
        
    }
    
    private class SupplyChain {
        
        private String folderName;
        private ContributionService contributionService;
        private String version;
        
        private Contribution customerContribution;
        private Contribution retailerContribution;
        private Contribution warehouseContribution;
        private Contribution shipperContribution;
        
        
        public SupplyChain(String folderName, ContributionService contributionService, String version) {
            this.folderName = folderName;
            this.contributionService = contributionService;
            this.version = version;
        }
        
        protected void setUpSCA() throws Exception {
            File customerLocation = new File(folderName + "/" + customerJarName + version + ".jar");
            URL customerContribURL = customerLocation.toURL();
            File retailerLocation = new File(folderName + "/" + retailerJarName + version + ".jar");
            URL retailerContribURL = retailerLocation.toURL();
            File warehouseLocation = new File(folderName + "/" + warehouseJarName + version + ".jar");
            URL warehouseContribURL = warehouseLocation.toURL();
            File shipperLocation = new File(folderName + "/" + shipperJarName + version + ".jar");
            URL shipperContribURL = shipperLocation.toURL();
            
            shipperContribution = contributionService.contribute("Shipper"
                    + version, shipperContribURL, true);
            warehouseContribution = contributionService.contribute("Warehouse"
                    + version, warehouseContribURL, true);
            retailerContribution = contributionService.contribute("Retailer"
                    + version, retailerContribURL, true);
            customerContribution = contributionService.contribute("Customer"
                    + version, customerContribURL, true);
            

            for (Composite deployable : customerContribution.getDeployables()) {
                domain.getDomainComposite().getIncludes().add(deployable);
                domain.buildComposite(deployable);
            }
            

            for (Composite deployable : retailerContribution.getDeployables() ) {
                domain.getDomainComposite().getIncludes().add(deployable);
                domain.buildComposite(deployable);
            }
            
            for (Composite deployable : warehouseContribution.getDeployables() ) {
                domain.getDomainComposite().getIncludes().add(deployable);
                domain.buildComposite(deployable);
            }
            
            for (Composite deployable : shipperContribution.getDeployables() ) {
                domain.getDomainComposite().getIncludes().add(deployable);
                domain.buildComposite(deployable);
            }
            
            // Start Components from my composite
            for (Composite deployable : customerContribution.getDeployables() ) {
                domain.getCompositeActivator().activate(deployable);
                domain.getCompositeActivator().start(deployable);
            }
        }

        public void tearDownSCA() throws Exception {
            // Remove the contribution from the in-memory repository
            contributionService.remove("Customer" + version);
            contributionService.remove("Retailer" + version);
            contributionService.remove("Warehouse" + version);
            contributionService.remove("Shipper" + version);


            // Stop Components from my composite
            for (Composite deployable : customerContribution.getDeployables() ) {
                domain.getCompositeActivator().stop(deployable);
                domain.getCompositeActivator().deactivate(deployable);
            }

        }
        
    }
    
}
