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
package org.apache.tuscany.sca.test.contribution;


import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.junit.Assert;

/*
 * 
 * Contribution ClassLoading integration tests
 */

public class SupplyChain {
    
    public static final int SUPPLYCHAIN = 0;
    public static final int SUPPLYCHAIN_ILLEGAL_1 = 1;
    public static final int SUPPLYCHAIN_ILLEGAL_2 = 2;
    public static final int SUPPLYCHAIN_SELFCONTAINED = 3;
    public static final int SUPPLYCHAIN_SPLITPACKAGE = 4;
    
    private String folderName = "../contribution-classes/target/classes";

    private String customerJarName = "Customer";
    private String retailerJarName = "Retailer";
    private String warehouseJarName = "Warehouse";
    private String shipperJarName = "Shipper";
    private String supplychainJarName = "SupplyChain";
    private String illegalSupplyChain1JarName = "IllegalSupplyChain1";
    private String illegalSupplyChain2JarName = "IllegalSupplyChain2";
    private String illegalCustomerJarName = "IllegalCustomer";
    private String completeSupplychainJarName = "CompleteSupplyChain";
    private String customerInterfaceJarName = "CustomerInterface";
    private String customerImplJarName = "CustomerImpl";
    
    
    private  EmbeddedSCADomain domain;
    private ContributionService contributionService;
    private int supplyChainVersion;
        
    private Hashtable<String, Contribution> contributions = new Hashtable<String, Contribution>();
    
    private URL customerContribURL;
    private URL retailerContribURL;
    private URL warehouseContribURL;
    private URL shipperContribURL;
    private URL supplyChainContribURL;
    private URL illegalSupplyChain1ContribURL;
    private URL illegalSupplyChain2ContribURL;
    private URL illegalCustomerContribURL;
    private URL completeSupplyChainContribURL;
    private URL customerInterfaceContribURL;
    private URL customerImplContribURL;
    
    public SupplyChain() throws Exception {
        
        customerContribURL = new File(folderName + "/" + customerJarName + ".jar").toURI().toURL();
        retailerContribURL = new File(folderName + "/" + retailerJarName + ".jar").toURI().toURL();
        warehouseContribURL = new File(folderName + "/" + warehouseJarName + ".jar").toURI().toURL();
        shipperContribURL = new File(folderName + "/" + shipperJarName + ".jar").toURI().toURL();
        supplyChainContribURL = new File(folderName + "/" + supplychainJarName + ".jar").toURI().toURL();
        illegalSupplyChain1ContribURL = new File(folderName + "/" + illegalSupplyChain1JarName + ".jar").toURI().toURL();
        illegalSupplyChain2ContribURL = new File(folderName + "/" + illegalSupplyChain2JarName + ".jar").toURI().toURL();
        illegalCustomerContribURL = new File(folderName + "/" + illegalCustomerJarName + ".jar").toURI().toURL();
        completeSupplyChainContribURL = new File(folderName + "/" + completeSupplychainJarName + ".jar").toURI().toURL();
        customerInterfaceContribURL = new File(folderName + "/" + customerInterfaceJarName + ".jar").toURI().toURL();
        customerImplContribURL = new File(folderName + "/" + customerImplJarName + ".jar").toURI().toURL();
    }
    
    public void setUp(ClassLoader parentClassLoader) throws Exception  {
        this.setUp(parentClassLoader, SUPPLYCHAIN);
    }
    
    public void setUp(ClassLoader parentClassLoader, int supplyChainVersion) throws Exception {
        
        this.supplyChainVersion = supplyChainVersion;
        
        Thread.currentThread().setContextClassLoader(parentClassLoader);
        
        //Create an embedded SCA domain
        domain = new EmbeddedSCADomain(parentClassLoader, "http://localhost");

        //Start the domain
        domain.start();

        this.contributionService = domain.getContributionService();
        
        addContributions(supplyChainVersion);
    }
        
    protected void addContributions(int supplyChainVersion) throws Exception {
            
        Contribution contribution;
        
        if (supplyChainVersion != SUPPLYCHAIN_SELFCONTAINED) {
            contribution = contributionService.contribute("Shipper", shipperContribURL, true);
            contributions.put("Shipper", contribution);
            contribution = contributionService.contribute("Warehouse", warehouseContribURL, true);
            contributions.put("Warehouse", contribution);
            contribution = contributionService.contribute("Retailer", retailerContribURL, true);
            contributions.put("Retailer", contribution);
        }
        
        switch (supplyChainVersion) {
        case SUPPLYCHAIN:
            contribution = contributionService.contribute("Customer", customerContribURL, true);
            contributions.put("Customer", contribution);
            
            contribution = contributionService.contribute("SupplyChain", supplyChainContribURL, true);
            contributions.put("SupplyChain", contribution);
            break;
        case SUPPLYCHAIN_ILLEGAL_1:
            contribution = contributionService.contribute("Customer", customerContribURL, true);
            contributions.put("Customer", contribution);
            
            contribution = contributionService.contribute("SupplyChain", illegalSupplyChain1ContribURL, true);
            contributions.put("SupplyChain", contribution);
            break;
        case SUPPLYCHAIN_ILLEGAL_2:
            contribution = contributionService.contribute("Customer", illegalCustomerContribURL, true);
            contributions.put("Customer", contribution);
            
            contribution = contributionService.contribute("SupplyChain", illegalSupplyChain2ContribURL, true);
            contributions.put("SupplyChain", contribution);
            break;
        case SUPPLYCHAIN_SELFCONTAINED:
            contribution = contributionService.contribute("SupplyChain", completeSupplyChainContribURL, true);
            contributions.put("SupplyChain", contribution);
            break;
        case SUPPLYCHAIN_SPLITPACKAGE:
            contribution = contributionService.contribute("Customer", customerInterfaceContribURL, true);
            contributions.put("Customer", contribution);
            
            contribution = contributionService.contribute("CustomerImpl", customerImplContribURL, true);
            contributions.put("CustomerImpl", contribution);
            
            contribution = contributionService.contribute("SupplyChain", supplyChainContribURL, true);
            contributions.put("SupplyChain", contribution);
            break;
        }
        
        // SUPPLYCHAIN_ILLEGAL_1 should throw an exception when the composite is resolved, and hence
        // should not get this far.
        Assert.assertTrue(supplyChainVersion != SUPPLYCHAIN_ILLEGAL_1);
            
        
        for (Contribution c : contributions.values()) {

            for (Composite deployable : c.getDeployables()) {
                domain.getDomainComposite().getIncludes().add(deployable);
                domain.buildComposite(deployable);
            }
            
        }
            
        // Start Components from my composite
        for (Composite deployable : contributions.get("SupplyChain").getDeployables() ) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }
    }
   
    public void tearDown() throws Exception {
        
        if (domain == null)
            return;
        
        for (String contributionURI : contributions.keySet()) {
            contributionService.remove(contributionURI);
        }


        if (contributions.get("SupplyChain") != null) {
            // Stop Components from my composite
            for (Composite deployable : contributions.get("SupplyChain").getDeployables() ) {
                domain.getCompositeActivator().stop(deployable);
                domain.getCompositeActivator().deactivate(deployable);
            }
        }

        domain.stop();

        domain.close();
    }

    public Contribution getContribution(String uri) {
        if (supplyChainVersion == SUPPLYCHAIN_SELFCONTAINED)
            return contributions.get("SupplyChain");
        else
            return contributions.get(uri);
    }
    
    public Object getCustomer(Class<?> customerClass) {
        return domain.getService(customerClass, "CustomerComponent");
    }
      
    public URL[] getContributionURLs() {
        return new URL[] {
                customerContribURL,
                retailerContribURL,
                warehouseContribURL,
                shipperContribURL,
                supplyChainContribURL
        };
    }
    
    
}
