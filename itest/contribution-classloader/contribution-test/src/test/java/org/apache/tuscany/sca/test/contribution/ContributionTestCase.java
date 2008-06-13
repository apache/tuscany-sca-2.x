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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * 
 * Contribution ClassLoading integration tests
 */

public class ContributionTestCase {

    private SupplyChain supplyChain;
    
    
    @Before
    public void setUp() throws Exception {
        supplyChain = new SupplyChain();
    }
	

	@After
    public void tearDown() throws Exception {

        supplyChain.tearDown();
        
	}

   
    /**
     * Test static ClassLoading for classes that are visible from contribution
     * 
     * @throws Exception
     */
    public void staticClassLoadingTestForVisibleClasses() throws Exception {
        
        Contribution customerContribution = supplyChain.getContribution("Customer");
        ClassReference customerClassRef = new ClassReference("supplychain.customer.Customer");
        customerClassRef = customerContribution.getModelResolver().resolveModel(ClassReference.class, customerClassRef);
        Class<?> customerClass = customerClassRef.getJavaClass();
        
        Class customerClassFromContribution = customerContribution.getClassLoader().loadClass("supplychain.customer.Customer");
        Assert.assertEquals(customerClass, customerClassFromContribution);
        
        Object customer = supplyChain.getCustomer(customerClass);
                
        Method m = customerClass.getMethod("purchaseGoods");
        m.invoke(customer);
        

        m = customerClass.getMethod("outstandingOrderCount");
        
        int retries = 10;
        int outstandingCount = 1;
        while (retries-- > 0) {
        
            outstandingCount = (int)(Integer)m.invoke(customer);
            if (outstandingCount == 0)
                break;
            else
                Thread.sleep(100);
        }
        Assert.assertEquals(0, outstandingCount);
        
        
    }
    

    /**
     * Test dynamic ClassLoading for classes that are visible from contribution
     * 
     * @throws Exception
     */
    public void dynamicClassLoadingTestForVisibleClasses() throws Exception {
        
        Contribution customerContribution = supplyChain.getContribution("Customer");
        Contribution retailerContribution = supplyChain.getContribution("Retailer");
        Contribution warehouseContribution = supplyChain.getContribution("Warehouse");
        Contribution shipperContribution = supplyChain.getContribution("Shipper");
        Contribution supplyChainContribution = supplyChain.getContribution("SupplyChain");
        
        ClassReference customerClassRef = new ClassReference("supplychain.customer.Customer");
        customerClassRef = customerContribution.getModelResolver().resolveModel(ClassReference.class, customerClassRef);
        Class customerClass = customerClassRef.getJavaClass();
        
        Class customerClassFromContribution = customerContribution.getClassLoader().loadClass("supplychain.customer.Customer");
        Assert.assertEquals(customerClass, customerClassFromContribution);
        
        Object customer = supplyChain.getCustomer(customerClass);
        Assert.assertTrue(customerClass.isInstance(customer));
        
        ClassReference retailerClassRef = new ClassReference("supplychain.retailer.Retailer");
        retailerClassRef = retailerContribution.getModelResolver().resolveModel(ClassReference.class, retailerClassRef);
        Class retailerClass = retailerClassRef.getJavaClass();
        
        Class retailerClassFromContribution = retailerContribution.getClassLoader().loadClass("supplychain.retailer.Retailer");
        Assert.assertEquals(retailerClass, retailerClassFromContribution);
        
        Class retailerClassFromCustomer = customerContribution.getClassLoader().loadClass("supplychain.retailer.Retailer");
        Assert.assertEquals(retailerClass, retailerClassFromCustomer);
        
        ClassReference warehouseClassRef = new ClassReference("supplychain.warehouse.Warehouse");
        warehouseClassRef = warehouseContribution.getModelResolver().resolveModel(ClassReference.class, warehouseClassRef);
        Class warehouseClass = warehouseClassRef.getJavaClass();
        
        Class warehouseClassFromContribution = warehouseContribution.getClassLoader().loadClass("supplychain.warehouse.Warehouse");
        Assert.assertEquals(warehouseClass, warehouseClassFromContribution);
        
        Class warehouseClassFromRetailer = retailerContribution.getClassLoader().loadClass("supplychain.warehouse.Warehouse");
        Assert.assertEquals(warehouseClass, warehouseClassFromRetailer);
        
        ClassReference shipperClassRef = new ClassReference("supplychain.shipper.Shipper");
        shipperClassRef = shipperContribution.getModelResolver().resolveModel(ClassReference.class, shipperClassRef);
        Class shipperClass = shipperClassRef.getJavaClass();
        
        Class shipperClassFromContribution = shipperContribution.getClassLoader().loadClass("supplychain.shipper.Shipper");
        Assert.assertEquals(shipperClass, shipperClassFromContribution);
        
        Class shipperClassFromWarehouse = shipperContribution.getClassLoader().loadClass("supplychain.shipper.Shipper");
        Assert.assertEquals(shipperClass, shipperClassFromWarehouse);
        
        Class customerClassFromShipper = shipperContribution.getClassLoader().loadClass("supplychain.customer.Customer");
        Assert.assertEquals(customerClass, customerClassFromShipper);
        
        Class customerClassFromSupplyChain = supplyChainContribution.getClassLoader().loadClass("supplychain.customer.Customer");
        Assert.assertEquals(customerClass, customerClassFromSupplyChain);
        Class retailerClassFromSupplyChain = supplyChainContribution.getClassLoader().loadClass("supplychain.retailer.Retailer");
        Assert.assertEquals(retailerClass, retailerClassFromSupplyChain);
        Class warehouseClassFromSupplyChain = supplyChainContribution.getClassLoader().loadClass("supplychain.warehouse.Warehouse");
        Assert.assertEquals(warehouseClass, warehouseClassFromSupplyChain);
        Class shipperClassFromSupplyChain = supplyChainContribution.getClassLoader().loadClass("supplychain.shipper.Shipper");
        Assert.assertEquals(shipperClass, shipperClassFromSupplyChain);
        
    }
    
    /**
     * Test dynamic ClassLoading for classes that are visible from contribution
     * 
     * @throws Exception
     */
    public void dynamicClassLoadingTestForNonImportedClasses() throws Exception {
        
        Contribution customerContribution = supplyChain.getContribution("Customer");
        Contribution shipperContribution = supplyChain.getContribution("Shipper");
        
        Class customerClass = customerContribution.getClassLoader().loadClass("supplychain.customer.Customer"); 
        Class shipperClass = shipperContribution.getClassLoader().loadClass("supplychain.shipper.Shipper");
        
        try {
            customerClass.getClassLoader().loadClass("supplychain.warehouse.Warehouse");
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        try {
            customerClass.getClassLoader().loadClass("supplychain.shipper.JavaShipperComponentImpl");
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        
        try {
            Class.forName("supplychain.warehouse.Warehouse", true, customerClass.getClassLoader());
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("supplychain.shipper.JavaShipperComponentImpl", true, customerClass.getClassLoader());
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        
        try {
            shipperClass.getClassLoader().loadClass("supplychain.warehouse.JavaWarehouseComponentImpl");
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        try {
            shipperClass.getClassLoader().loadClass("supplychain.retailer.Retailer");
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        
        try {
            Class.forName("supplychain.warehouse.JavaWarehouseComponentImpl", true, shipperClass.getClassLoader());
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("supplychain.retailer.Retailer", true, shipperClass.getClassLoader());
            Assert.fail("Non-imported class loaded incorrectly");
        } catch (ClassNotFoundException e) {
        }
        
        
    }
    

    /**
     * This test ensures that classes from imported packages can be statically loaded 
     * from other contributions even though the classes are not on CLASSPATH or on the 
     * parent ClassLoader, or the thread context ClassLoader.
     * 
     * @throws Exception
     */
    @Test
    public void testValidStaticClassLoading() throws Exception {
               
        supplyChain.setUp(this.getClass().getClassLoader());
        
        staticClassLoadingTestForVisibleClasses();
    }
    
    /**
     * This test ensures that all imported classes are loaded from the exporting contributions 
     * rather than the parent classLoader. If any of the interface classes were incorrectly loaded 
     * through the parent ClassLoader, LinkageError should result.
     * 
     * @throws Exception
     */
    @Test
    public void testValidStaticClassLoadingWithContributionsInParentClassLoader() throws Exception {
        
        
        URLClassLoader parentClassLoader = new URLClassLoader(
                supplyChain.getContributionURLs(),
                this.getClass().getClassLoader());

        supplyChain.setUp(parentClassLoader);
        
        staticClassLoadingTestForVisibleClasses();
    }
    
    
    /**
     * This test ensures that classes from imported packages can be dynamically loaded from
     * other contributions even though the classes are not on CLASSPATH or on the 
     * parent ClassLoader, or the thread context ClassLoader.
     * 
     * @throws Exception
     */
    @Test
    public void testValidDynamicClassLoading() throws Exception {
               
        supplyChain.setUp(this.getClass().getClassLoader());
        
        dynamicClassLoadingTestForVisibleClasses();
    }
    
    /**
     * This test ensures that all imported classes are dynamically loaded from the exporting 
     * contributions rather  than the parent classLoader. If any of the interface classes were 
     * incorrectly loaded through the parent, NoClassDefFoundError or LinkageError should result.
     * 
     * @throws Exception
     */
    @Test
    public void testValidDynamicClassLoadingWithContributionsInParentClassLoader() throws Exception {
        
        
        URLClassLoader parentClassLoader = new URLClassLoader(
                supplyChain.getContributionURLs(),
                this.getClass().getClassLoader());

        supplyChain.setUp(parentClassLoader);
        
        dynamicClassLoadingTestForVisibleClasses();
    }
    
    @Test
    public void testIllegalStaticClassLoading1() throws Exception {
        // FIXME we have commented this code as we are not throwing exceptions anymore
    	// need to deal with monitor logs to catch the errors.
        
    	/*try {
            supplyChain.setUp(this.getClass().getClassLoader(), SupplyChain.SUPPLYCHAIN_ILLEGAL_1);
        
            Assert.fail("Composite containing unresolved references resolved incorrectly");
        } catch (ContributionResolveException e) {
        }*/
    }
    
    @Test
    public void testIllegalStaticClassLoading2() throws Exception {
          
        supplyChain.setUp(this.getClass().getClassLoader(), SupplyChain.SUPPLYCHAIN_ILLEGAL_2);
        
        Contribution customerContribution = supplyChain.getContribution("Customer");
        ClassReference customerClassRef = new ClassReference("supplychain.customer.Customer");
        customerClassRef = customerContribution.getModelResolver().resolveModel(ClassReference.class, customerClassRef);
        Class<?> customerClass = customerClassRef.getJavaClass();
        
        Object customer = supplyChain.getCustomer(customerClass);
                
        try {
            Method m = customerClass.getMethod("purchaseGoods");
            m.invoke(customer);
            
            Assert.fail("Classloading exception not thrown as expected");
        } catch (InvocationTargetException e) {
            
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof NoClassDefFoundError);
            Assert.assertTrue(cause.getMessage().indexOf("JavaWarehouseComponentImpl") > -1);        }
        
    }
    
    /**
     * This test ensures that classes from imported packages can be dynamically loaded from
     * other contributions even though the classes are not on CLASSPATH or on the 
     * parent ClassLoader, or the thread context ClassLoader.
     * 
     * @throws Exception
     */
    @Test
    public void testIllegalDynamicClassLoading() throws Exception {
               
        supplyChain.setUp(this.getClass().getClassLoader());
        
        dynamicClassLoadingTestForNonImportedClasses();
    }
    
    
    /**
     * Self-contained contribution containing composites, componentType files and implementations
     * should not require import/export statements for these files to find each other or for Tuscany
     * to load these files.
     * @throws Exception
     */
    @Test
    public void testSelfContainedContribution() throws Exception {
               
        supplyChain.setUp(this.getClass().getClassLoader(), SupplyChain.SUPPLYCHAIN_SELFCONTAINED);
        
        staticClassLoadingTestForVisibleClasses();
    }
    
    @Test
    public void testContributionsWithSplitPackage() throws Exception {
               
        supplyChain.setUp(this.getClass().getClassLoader(), SupplyChain.SUPPLYCHAIN_SPLITPACKAGE);
        
        staticClassLoadingTestForVisibleClasses();
        
        dynamicClassLoadingTestForNonImportedClasses();
    }
}
