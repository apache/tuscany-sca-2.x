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
package org.apache.tuscany.sca.test.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.ref.WeakReference;

import javax.management.MBeanServer;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceFactoryImpl;
import org.apache.tuscany.sca.test.performance.client.BeanA;
import org.apache.tuscany.sca.test.performance.client.Helloworld;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.management.HotSpotDiagnosticMXBean;

/*
 * A test looking at memory and runtime performance. 
 * TODO - only a couple of memory tests to start with
 */
public class PerformanceTestCase {
    
    public boolean writeHeapDump = true;

    private static BufferedWriter resultsFile = null;
    
    private TuscanyRuntime runtime;
    private Node node;
    private ExtensionPointRegistry extensionPointRegistry;
    private long lastTotal = 0;
    private long lastFree = 0;
    private long lastUsed = 0;
    
    @BeforeClass
    public static void setUp() throws Exception {
        FileWriter fileWriter = new FileWriter("out.txt");
        resultsFile = new BufferedWriter(fileWriter);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        resultsFile.flush();
        resultsFile.close();
    }

    @Test
    public void testInstallUninstall() {
        dumpHeapStart("testInstallUninstall");
        
        printRuntimeStats("createRuntime");
        
        createRuntime();
        
        printRuntimeStats("createNode");
        
        createNode();
        
        callInstallUninstallRepeatedly(10);

        dumpHeapEnd("testInstallUninstall_postUninstall");
        
        checkCacheStatus();
        
        printRuntimeStats("stopNode");
        
        stopNode();
        
        printRuntimeStats("destroyNode");
        
        destroyNode();
        
        printRuntimeStats("stopRuntime");
        
        stopRuntime();
        
        printRuntimeStats("destroyRuntime");
        
        destroyRuntime();
        
        printRuntimeStats("End");
        
        dumpHeapEnd("testInstallUninstall_postStop");
    }
    

    @Test
    public void testCall() {
        dumpHeapStart("TestCall");
        
        printRuntimeStats("createRuntime");
        
        createRuntime();
        
        printRuntimeStats("createNode");
        
        createNode();
        
        printRuntimeStats("installContribution");
        
        installContribution();
        
        printRuntimeStats("startNode");
        
        startComposite();
        
        printRuntimeStats("callServiceRepeatedly 10000 times");
        
        callServiceRepeatedly(10000);
        
        printRuntimeStats("stopCompositeAndUninstallUnused");
        
        stopCompositeAndUninstallUnused();
        
        //printRuntimeStats("uninstallContribution");
        
        //uninstallContribution();
        dumpHeapEnd("TestCall_postUninstall");
        
        printRuntimeStats("stopNode");
        
        stopNode();
        
        printRuntimeStats("destroyNode");
        
        destroyNode();
        
        printRuntimeStats("stopRuntime");
        
        stopRuntime();
        
        printRuntimeStats("destroyRuntime");
        
        destroyRuntime();
        
        printRuntimeStats("End");
        
        dumpHeapEnd("TestCall_postStop");
    }
    
    public void checkCacheStatus(){
        UtilityExtensionPoint utilityExtensionPoint = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        JAXBContextHelper jaxbContextHelper = utilityExtensionPoint.getUtility(JAXBContextHelper.class);
        
        Assert.assertEquals("JAXBContextCache > 1", 1, jaxbContextHelper.getJAXBContextCache().getCache().keySet().size());
        
        FactoryExtensionPoint factoryExtensionPoint = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaInterfaceFactory = factoryExtensionPoint.getFactory(JavaInterfaceFactory.class);
        Assert.assertEquals("JavaInterfaceFactoryImpl.normalCache > 1", 1, ((JavaInterfaceFactoryImpl)javaInterfaceFactory).getNormalCache().keySet().size());
        
    }
    
    // ============================================================
    
    public void callInstallUninstallRepeatedly(int repeatCount) {
        for (int i =0; i < repeatCount; i++){
            printRuntimeStats("install/unistall contribution");
            
            installContribution();
            
            startComposite();
            
            callService();       
            
            stopCompositeAndUninstallUnused();
            
            //uninstallContribution();
        }
    }
    
    public void callServiceRepeatedly(int repeatCount) {
        for (int i =0; i < repeatCount; i++){
            callService();
        }
    }
    
    // ============================================================
    
    public void dumpHeapStart(String name){
        if (writeHeapDump){
            dumpHeap("heap_start_" + name + ".bin");
        }
    }
    
    public void dumpHeapEnd(String name){
        if (writeHeapDump){
            dumpHeap("heap_stop_" + name + ".bin");
            
            System.out.println("You can watch a JVM run using \n" +
                               " jconsole \n" + 
                               "You can manually dump the heap using \n" +
                               " jmap -dump:file=heap_stop_" + name + ".bin 345" +
                               "Where 345 is the process id from jconsole \n" +
                               "The program dumps the heap at the start and end. You can look at them using \n" +
                               " jhat -J-Xmx512m heap_start.bin\n" +
                               " jhat -J-Xmx512m heap_stop_" + name + ".bin\n" +
                               "Then point your browser at\n" +
                               " http://localhost:7000/");
        } else {
            System.out.println("NO HEAP DUMPS WRITTEN");
        }
    }
    
    public void createRuntime() {
        runtime = TuscanyRuntime.newInstance();
    }
    
    public void createNode() {
        node = runtime.createNode("default");
        extensionPointRegistry = ((NodeImpl)node).getExtensionPointRegistry();
    }
    
    public void installContribution() {
        try {
            node.installContribution("performance", "../performance-contribution1/target/itest-performance-contribution1-2.0-SNAPSHOT.jar", null, null);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void startComposite() {
        try {
            node.startComposite("performance", "PerformanceTest.composite");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void stopCompositeAndUninstallUnused(){
        try { 
            node.stopCompositeAndUninstallUnused("performance", "PerformanceTest.composite");
        } catch (Exception ex){
            ex.printStackTrace();
        }            
    }
    
    public void uninstallContribution(){
        try {
            node.installContribution("performance");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void stopNode(){
        node.stop();
    }
    
    public void destroyNode() {
        node = null;
    }
    
    public void stopRuntime() {
        runtime.stop();
    }
    
    public void destroyRuntime() {
        runtime = null;
    }
    
    public void callService() {
        try {
            Helloworld helloWorldClient = node.getService(Helloworld.class, "HelloWorldClientComponent");
            
            assertNotNull(helloWorldClient);  
            
            BeanA beanA = new BeanA();
            beanA.setField1("Smith");
            beanA.setField2(13);
            
            assertEquals("Hello Hello Jane Smith", helloWorldClient.sayHello("Jane", beanA));
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }
    
    public void printRuntimeStats(String step){
        tryToGC();
        
        Runtime runtime = Runtime.getRuntime();
        
        long used  = runtime.totalMemory() - runtime.freeMemory();
        long free  = runtime.freeMemory();
        long total = runtime.totalMemory();
        
        String stats = "U " + used  + "[" + (used  - lastUsed ) + "] \t" +
                       "F " + free  + "[" + (free  - lastFree ) + "] \t" +
                       "T " + total + "[" + (total - lastTotal) + "] \t" +
                       step + "\n";
            
        try {
            resultsFile.write(stats);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            
        System.out.print(stats);
        
        lastTotal = total;
        lastFree  = free;
        lastUsed  = used;
    }
    
    /** 
     * We can't rely on GC doing anything sensible
     * but I'm just playing here to see if I can 
     * force it to GC
     */
    public void tryToGC(){

        Runtime runtime = Runtime.getRuntime();
        
/*        
        // force OOME
        StringBuffer sb = new StringBuffer();
        try {
            while(true)
            {
                sb.append("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
        } catch (OutOfMemoryError ex){
            // we are going to GC now
            System.out.println("OOME");
        }
        sb = null;
       
        
        for (int i = 0; i < 10; i++){
            Object obj = new Object();
            WeakReference ref = new WeakReference<Object>(obj);
            obj = null;
            while(ref.get() != null) {
                runtime.runFinalization();
                runtime.gc();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
*/         
    }
    
    public void dumpHeap(String heapName){
        String vendor = System.getProperty("java.vendor");
        if (vendor.contains("Sun")){
            dumpHeapSun(heapName);
        }
    }
    

    public void dumpHeapSun(String heapName){
        final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        final MBeanServer platformBean = ManagementFactory.getPlatformMBeanServer();
        
        HotSpotDiagnosticMXBean bean = null;
        
        try {
            bean = ManagementFactory.newPlatformMXBeanProxy(platformBean,
                                                            "com.sun.management:type=HotSpotDiagnostic", 
                                                            HotSpotDiagnosticMXBean.class);
            deleteFile(heapName);
            bean.dumpHeap(heapName, false);
        } catch(Exception ex){
            ex.printStackTrace();
        } 
    }
    
    public void dumpHeapIBM(){
        
    }
    
    public void deleteFile(String filename){
        File theFile = new File(filename);
        theFile.delete();
    }
    
    public void waitForInput() {
        System.out.println("Press a key to end");
        try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Continuing");
    }  
    
    // final MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
    // com.sun.management.OperatingSystemMXBean if java.lang.management.OperationSystemMXBean
}
