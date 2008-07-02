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

package org.apache.tuscany.sca.contribution.java.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.java.JavaImportExportFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test ContributionClassLoader.
 *
 */
public class ContributionClassLoaderTestCase  {
    
    private ContributionFactory contributionFactory;
    private JavaImportExportFactory javaImportExportFactory;
    
    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        javaImportExportFactory = modelFactories.getFactory(JavaImportExportFactory.class);
    }
    
    private Contribution createContribution(String fileName) throws MalformedURLException {
        Contribution contrib = contributionFactory.createContribution();
        File contribDir = new File(fileName);        
        contrib.setLocation(contribDir.toURI().toURL().toString());
        ClassLoader contextClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });           
        contrib.setClassLoader(new ContributionClassLoader(contrib, contextClassLoader));
        return contrib;
    }
    
   
    @Test
    public void testClassLoadingFromContribution() throws ClassNotFoundException, MalformedURLException {
        
        Contribution contribA = createContribution("target/test-classes");
        Contribution contribB = createContribution("target");
        Contribution contribC = createContribution("target/test-classes/deployables/sample-calculator.jar");
        
        // Class present in contribution, also in parent. Class is loaded from parent
        Class<?> testClassA = contribA.getClassLoader().loadClass(this.getClass().getName());        
        Assert.assertNotNull(testClassA);
        Assert.assertSame(this.getClass(), testClassA);
        
        // Class not present in contribution, but present in parent ClassLoader
        Class<?> testClassB = contribB.getClassLoader().loadClass(this.getClass().getName());
        Assert.assertNotNull(testClassB);
        Assert.assertSame(this.getClass(), testClassB);
        
        // Class present in contribution, but not in parent
        Class<?> testClassC = contribC.getClassLoader().loadClass("calculator.AddService");        
        Assert.assertNotNull(testClassC);
        
        // Class not present in contribution or in parent
        try {
            contribA.getClassLoader().loadClass("NonExistent");
            
            Assert.assertTrue("ClassNotFoundException not thrown as expected", false);
            
        } catch (ClassNotFoundException e) {
        }
        
        
        
    }
    
    @Test
    public void testResourceLoadingFromContribution() throws ClassNotFoundException, MalformedURLException {
        
        Contribution contribA = createContribution("target/test-classes");
        Contribution contribB = createContribution("target");
        Contribution contribC = createContribution("target/test-classes/deployables/sample-calculator.jar");
        
        // Resource present in contribution, and in parent
        URL resA = contribA.getClassLoader().getResource("deployables/sample-calculator.jar");
        Assert.assertNotNull(resA);
        
        // Resource not present in contribution, but present in parent ClassLoader
        URL resB = contribB.getClassLoader().getResource("deployables/sample-calculator.jar");
        Assert.assertNotNull(resB);
        
        // Resource present in contribution, but not in parent
        URL resC = contribC.getClassLoader().getResource("calculator/AddService.class");
        Assert.assertNotNull(resC);        
        
        // Load Java class as resource from parent
        String classResName = this.getClass().getName().replaceAll("\\.", "/") + ".class";
        URL classResA = contribA.getClassLoader().getResource(classResName);
        Assert.assertNotNull(classResA);
               
        // Non-existent resource
        URL res = contribA.getClassLoader().getResource("deployables/NonExistent");
        Assert.assertNull(res);
        
    }
    
    private static String getPackageName(Class<?> cls) {
        String name = cls.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? "" : name.substring(0, index);
    }

    @Test
    public void testClassLoadingFromImportedContribution() throws ClassNotFoundException, MalformedURLException {
        
        Contribution contribA = createContribution("target/test-classes");
        Contribution contribB = createContribution("target");
        Contribution contribC = createContribution("target/test-classes/deployables/sample-calculator.jar");
        ArrayList<Contribution> exportContribList = new ArrayList<Contribution>();
        exportContribList.add(contribA);
        exportContribList.add(contribC);
        
        JavaImport import_ = javaImportExportFactory.createJavaImport();
        import_.setPackage(getPackageName(getClass()));
        import_.setModelResolver(new JavaImportModelResolver(exportContribList, null));
        contribB.getImports().add(import_);
        import_ = javaImportExportFactory.createJavaImport();
        import_.setPackage("calculator");
        import_.setModelResolver(new JavaImportModelResolver(exportContribList, null));
        contribB.getImports().add(import_);
        
        JavaExport export = javaImportExportFactory.createJavaExport();
        export.setPackage(getPackageName(getClass()));
        contribA.getExports().add(export);
        export = javaImportExportFactory.createJavaExport();
        export.setPackage("calculator");
        contribC.getExports().add(export);        
        
        // Load class from parent, class is also present in imported contribution. Class should
        // be loaded from parent
        Class<?> testClassB = contribB.getClassLoader().loadClass(this.getClass().getName());        
        Assert.assertNotNull(testClassB);
        Assert.assertSame(this.getClass(), testClassB);
        
        // Load class from parent, class is also present in parent. Class should be loaded
        // from parent.
        Class<?> testClassA = contribA.getClassLoader().loadClass(this.getClass().getName());        
        Assert.assertNotNull(testClassA);
        Assert.assertSame(this.getClass(), testClassA);
        
        // Imported class should be the same as the one loaded by the exporting contribution
        Assert.assertSame(testClassA, testClassB);
        
        // Load class from imported contribution, class is not present in parent
        Class<?> testClassB1 = contribB.getClassLoader().loadClass("calculator.AddService");
        Assert.assertNotNull(testClassB1);
        
        // Imported class should be the same as the one loaded by the exporting contribution
        Class<?> testClassC = contribC.getClassLoader().loadClass("calculator.AddService");
        Assert.assertNotNull(testClassC);        
        Assert.assertSame(testClassC, testClassB1);
        

        // Try to load class from package which is not explicitly imported - should throw ClassNotFoundException
        try {
            contribA.getClassLoader().loadClass("calculator.AddService");
            
            Assert.assertTrue("ClassNotFoundException not thrown as expected", false);
            
        } catch (ClassNotFoundException e) {
        }
        
        // Try to load non-existent class from imported package - should throw ClassNotFoundException
        try {
            contribB.getClassLoader().loadClass(getPackageName(getClass()) + ".NonExistentClass");
            
            Assert.assertTrue("ClassNotFoundException not thrown as expected", false);
            
        } catch (ClassNotFoundException e) {
        }
        
    }

    @Test
    public void testResourceLoadingFromImportedContribution() throws ClassNotFoundException, MalformedURLException {
        
        Contribution contribA = createContribution("target/test-classes");
        Contribution contribB = createContribution("target");
        Contribution contribC = createContribution("target/test-classes/deployables/sample-calculator.jar");
        
        ArrayList<Contribution> exportContribList = new ArrayList<Contribution>();
        exportContribList.add(contribA);
        exportContribList.add(contribC);
        
        JavaImport import_ = javaImportExportFactory.createJavaImport();
        import_.setPackage(getPackageName(getClass()));
        import_.setModelResolver(new JavaImportModelResolver(exportContribList, null));
        contribB.getImports().add(import_);
        JavaImport import1_ = javaImportExportFactory.createJavaImport();
        import1_.setPackage("calculator");
        import1_.setModelResolver(new JavaImportModelResolver(exportContribList, null));
        contribB.getImports().add(import1_);
        
        JavaExport export = javaImportExportFactory.createJavaExport();
        export.setPackage(getPackageName(getClass()));
        contribA.getExports().add(export);
        JavaExport export1 = javaImportExportFactory.createJavaExport();
        export1.setPackage("calculator");
        contribC.getExports().add(export1);

        
        // Load resource from parent
        URL resB = contribB.getClassLoader().getResource("deployables/sample-calculator.jar"); 
        Assert.assertNotNull(resB);
        
        // Load Java class as resource from imported contribution with JavaImport
        String classResName = this.getClass().getName().replaceAll("\\.", "/") + ".class";               
        URL classResB = contribB.getClassLoader().getResource(classResName);
        Assert.assertNotNull(classResB);
        
        // Load Java class as resource from imported contribution with JavaImport
        URL classResB1 = contribB.getClassLoader().getResource("calculator/AddService.class");
        Assert.assertNotNull(classResB1);
        
        // Try to load resource not explicitly imported by contribution
        URL classResA1 = contribA.getClassLoader().getResource("calculator/AddService.class");
        Assert.assertNull(classResA1);
        
        
    }

}
