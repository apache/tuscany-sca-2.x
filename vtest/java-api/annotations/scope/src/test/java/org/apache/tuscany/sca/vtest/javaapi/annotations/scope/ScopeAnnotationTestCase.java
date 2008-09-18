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

package org.apache.tuscany.sca.vtest.javaapi.annotations.scope;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class tests the Scope annotation described in section 1.2.4
 * including 1.8.7, 1.8.8, 1.8.9, 1.8.10, 1.8.11, and 1.8.16<br>
 * <p>
 * Check following:<br>
 * <li>Number of service instances be created</li>
 * <li>Init method be called and where all property and reference injection is
 *     complete.</li>
 * <li>Destroy method be called</li>
 * <li>State be preserved in request and conversation scopes but not others</li>
 */
public class ScopeAnnotationTestCase {

    protected static String compositeName = "scope.composite";

    protected static int numDThread = 5;
    protected static int numFThread = 5;
    protected static int numHThread = 5;
	
    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
    
    /**
     * Line 259:<br>
     * <li>STATELESS</li>
     * <p>
     * Line 284:<br>
     * For stateless components, there is no implied correlation between
     * service requests.<br>
     * <p>
     * Line 1589 to 1591:<br>
     * The default value is 'STATELESS'. For 'STATELESS' implementations,
     * a different implementation instance may be used to service each request.
     * Implementation instances may be newly created or be drawn from a pool
     * of instances.<br>
     * <p>
     * BComponent is not defined '@Scope'<br>
     * CComponent is defined '@Scope("STATELESS")'<br>
     * <p>
     */
    @Test
    public void atScope1() throws Exception {
        System.out.println("atScope1");
        BThread b1 = new BThread("ThreadB1");
        BThread b2 = new BThread("ThreadB2");
        CThread c1 = new CThread("ThreadC1");
        CThread c2 = new CThread("ThreadC2");
        
        b1.start();
        b2.start();
        c1.start();
        c2.start();
        b1.join();
        b2.join();
        c1.join();
        c2.join();
        
        System.out.println("");

        Assert.assertEquals("None", b1.failedReason);
        Assert.assertEquals("None", b2.failedReason);
        Assert.assertEquals("None", c1.failedReason);
        Assert.assertEquals("None", c2.failedReason);
    }
    
    /**
     * Line 260:<br>
     * <li>REQUEST</li>
     * <p>
     * Lines 286 to 289:<br>
     * The lifecycle of request scope extends from the point a request on a
     * remotable interface enters the SCA runtime and a thread processes that
     * request until the thread completes synchronously processing the request.
     * During that time, all service requests will be delegated to the same
     * implementation instance of a request-scoped component.<br>
     * <p>
     */
    @Test
    public void atScope2() throws Exception {
        System.out.println("atScope2");
        ArrayList<DThread> d = new ArrayList<DThread>();
        for (int i = 0; i < numDThread; i++)
        	d.add(new DThread("ThreadD"+i));
        for (int i = 0; i < numDThread; i++)
        	d.get(i).start();
        for (int i = 0; i < numDThread; i++)
        	d.get(i).join();
        System.out.println("");
        for (int i = 0; i < numDThread; i++)
        	Assert.assertEquals("None", d.get(i).failedReason);
    }
    
    /**
     * Line 262:<br>
     * <li>COMPOSITE</li>
     * <p>
     * Lines 296 to 298:<br>
     * All service requests are dispatched to the same implementation instance
     * for the lifetime of the containing composite. The lifetime of the
     * containing composite is defined as the time it becomes active in the
     * runtime to the time it is deactivated, either normally or abnormally.<br>
     * <p>
     * Following lines should be found at System out<br>
     * atScope3 - Cleaning up<br>
     * FService1->destroyFService
     */
    @Test
    public void atScope3() throws Exception {
        System.out.println("atScope3");
        FService fService = ServiceFinder.getService(FService.class, "FComponent");
        String serviceName = fService.getName();
        boolean isInitReady   = fService.isInitReady();

        ArrayList<FThread> f = new ArrayList<FThread>();
        for (int i = 0; i < numFThread; i++)
        	f.add(new FThread("ThreadF"+i));
        for (int i = 0; i < numFThread; i++)
        	f.get(i).start();
        for (int i = 0; i < numFThread; i++)
        	f.get(i).join();
        for (int i = 0; i < numFThread; i++) {
        	Assert.assertEquals(serviceName, f.get(i).serviceName);
        }
        
        int instanceCounter   = fService.getInstanceCounter();
        int initCalledCounter = fService.getInitCalledCounter();
        int destroyCalledCounter = fService.getDestroyCalledCounter();
        System.out.println("");
        
        Assert.assertTrue(isInitReady);
        Assert.assertEquals(1, instanceCounter);
        Assert.assertEquals(1, initCalledCounter);
        Assert.assertEquals(0, destroyCalledCounter);
    }
    
    /**
     * Lines 299 to 302:<br>
     * A composite scoped implementation may also specify eager initialization
     * using the "@EagerInit" annotation. When marked for eager initialization,
     * the composite scoped instance will be created when its containing
     * component is started. If a method is marked with the "@Init" annotation,
     * it will be called when the instance is created.<p>
     * Section 1.8.9 "@EagerInit"<br>
     * <p>
     * GService is defined as eager initialization. It will be initialized
     * when calls SCADomain.newInstance(compositeName), so the 
     * initCalledCounter is 1.
     */
    @Test
    public void atScope4() throws Exception {
        System.out.println("atScope4");

        GService gService = ServiceFinder.getService(GService.class, "GComponent");
        int initCalledCounter    = gService.getInitCalledCounter();
        int destroyCalledCounter = gService.getDestroyCalledCounter();
        
        System.out.println("initCalledCounter="+ initCalledCounter);
        System.out.println("destroyCalledCounter="+ destroyCalledCounter);
        System.out.println("");

        Assert.assertEquals(1, initCalledCounter);
        Assert.assertEquals(0, destroyCalledCounter);
    }
    
    /**
     * Line 261:<br>
     * <li>CONVERSATION</li>
     * <p>
     * Lines 305 to 310:<br>
     * A conversation is defined as a series of correlated interactions between
     * a client and a target service. A conversational scope starts when the
     * first service request is dispatched to an implementation instance
     * offering a conversational service. A conversational scope completes
     * after an end operation defined by the service contract is called and
     * completes processing or the conversation expires. A conversation may be
     * long-running and the SCA runtime may choose to passivate implementation
     * instances. If this occurs, the runtime must guarantee implementation
     * instance state is preserved.
     */
    @Test
    public void atScope5() throws Exception {
        System.out.println("atScope5");

        ArrayList<HThread> g = new ArrayList<HThread>();
        for (int i = 0; i < numHThread; i++)
        	g.add(new HThread("ThreadH"+i));
        for (int i = 0; i < numHThread; i++)
        	g.get(i).start();
        for (int i = 0; i < numHThread; i++)
        	g.get(i).join();
        
        HService hService = ServiceFinder.getService(HService.class, "HComponent");
        String failedReason = hService.testCounters(numHThread);
        System.out.println("");

        for (int i = 0; i < numHThread; i++)
        	Assert.assertEquals("None", g.get(i).failedReason);
        Assert.assertEquals("None", failedReason);
    }

    /** Lines 290 to 293:<br>
    * There are times when a local request scoped service is called without
    * there being a remotable service earlier in the call stack, such as when
    * a local service is called from a non-SCA entity. In these cases, a
    * remote request is always considered to be present, but the lifetime of
    * the request is implementation dependent. For example, a timer event
    * could be treated as a remote request..<br>
    * <p>
    * When the composite runs, composite scope service JService kicks off a
    * timer by the @Init method.  When the timer expires, JService invokes a
    * method that calls an operation on the reference to a stateless scope
    * sevice KService. KService calls a request scope service LService
    * multiple times. The results of the calls to LService should be set up to
    * differ depending on whether the same instance is called each time -
    * without the interface being declared Conversational.<br>
    * <p>
    */
    @Test
    public void atScope6() throws Exception {
        System.out.println("atScope6");

        JService jService = ServiceFinder.getService(JService.class, "JComponent");
		jService.getName();
		Thread.sleep(2000);
		String failedReason = jService.getFailedReason();
        System.out.println("");

        Assert.assertEquals("", failedReason);
    }
    
    /**
     * Same as atScope6 but the timer triggers KService multiple times.<br>
     */
    @Test
    @Ignore("TUSCANY-2256")
    public void atScope7() throws Exception {
        System.out.println("atScope7");

        MService mService = ServiceFinder.getService(MService.class, "MComponent");
        mService.getName();
		Thread.sleep(6000);
		String failedReason = mService.getFailedReason();
        System.out.println("");

        Assert.assertEquals("", failedReason);
    }
    
    private  class BThread extends Thread {
    	private String name = null;
    	public String failedReason = "Unknown";
    	
    	public BThread(String name) {
    		super();
    		this.name = name;
    		failedReason = "Unknown";
    	}
    	
    	@Override
    	public void run() {
    		BService bService = ServiceFinder.getService(BService.class, "BComponent");
    		bService.setCurrentState(name + "-state-1");
    		System.out.println(name + "->" + bService.getName());
    		if (!bService.isInitReady()) {
    			failedReason = "InitNotReady";
    			return;
    		}
    		if (bService.getCurrentState() != null) {
    			failedReason = "CurrentStateNotNull";
    			return;
    		}
    		int counter = bService.getInstanceCounter();
    		if (counter < 2) {
    			failedReason = "OnlyOneInstance - " + counter;
    			return;
    		}
    		counter = bService.getInitCalledCounter();
    		if (counter < 2) {
    			failedReason = "InitBeCalledOnce - " + counter;
    			return;
    		}
    		counter = bService.getDestroyCalledCounter();
    		if (counter < 2) {
    			failedReason = "DestroyBeCalledOnce - " + counter;
    			return;
    		}
    		failedReason = "None";
        }
      }

    private  class CThread extends Thread {
    	private String name = null;
    	public String failedReason = "Unknown";
    	
    	public CThread(String name) {
    		super();
    		this.name = name;
    		failedReason = "Unknown";
    	}
    	
    	@Override
    	public void run() {
    		CService cService = ServiceFinder.getService(CService.class, "CComponent");
    		cService.setCurrentState(name + "-state-1");
    		System.out.println(name + "->" + cService.getName());
    		if (!cService.isInitReady()) {
    			failedReason = "InitNotReady";
    			return;
    		}
    		if (cService.getCurrentState() != null) {
    			failedReason = "CurrentStateNotNull";
    			return;
    		}
    		int counter = cService.getInstanceCounter();
    		if (counter < 2) {
    			failedReason = "OnlyOneInstance - " + counter;
    			return;
    		}
    		counter = cService.getInitCalledCounter();
    		if (counter < 2) {
    			failedReason = "InitBeCalledOnce - " + counter;
    			return;
    		}
    		counter = cService.getDestroyCalledCounter();
    		if (counter < 2) {
    			failedReason = "DestroyBeCalledOnce - " + counter;
    			return;
    		}
    		failedReason = "None";
        }
    }
    
    private  class DThread extends Thread {
    	
    	private String name = null;
    	public String failedReason = "Unknown";
    	
    	public DThread(String name) {
    		super();
    		this.name = name;
    		failedReason = "Unknown";
    	}
    	
    	@Override
    	public void run() {
    		DService dService = ServiceFinder.getService(DService.class, "DComponent");
    		String serviceName = dService.getName();
    		System.out.println(name + "->" + serviceName);
    		
    		for (int i = 0; i < 10; i++) {
    			String newState = name + "-state-" + i;
    			dService.setCurrentState(newState);
    			String currentState = dService.getCurrentState();
    			if (!currentState.equals(serviceName + "-" + newState)) {
    				failedReason = "CurrentStateLost - " + currentState;
    				return;
    			}
    		}
    		
    		if (!dService.isInitReady()) {
    			failedReason = "InitNotReady";
    			return;
    		}

    		int counter = dService.getInstanceCounter();
    		if (counter > numDThread) {
    			failedReason = "TooMuchInstance - " + counter;
    			return;
    		}
    		
    		counter = dService.getInitCalledCounter();
    		if (counter > numDThread) {
    			failedReason = "InitBeCalledTooMuch - " + counter;
    			return;
    		}
    		
    		// JIRA T-2215
    		/* Don't know when destroy method be called
    		counter = dService.getDestroyCalledCounter();
    		if (counter > numDThreads) {
    			failedReason = "DestroyBeCalledTooMuch - " + counter;
    			return;
    		}
    		*/
    		failedReason = "None";
        }
    }
    
    private  class FThread extends Thread {
    	
    	private String name = null;

    	public String serviceName = null;
    	
    	public FThread(String name) {
    		super();
    		this.name = name;
    	}
    	
    	@Override
    	public void run() {
    		FService fService = ServiceFinder.getService(FService.class, "FComponent");
    		serviceName = fService.getName();
    		System.out.println(name + "->" + serviceName);

    		for (int i = 0; i < 10; i++) {
    			String newState = name + "-state-" + i;
    			fService.setCurrentState(newState);
    			fService.getCurrentState();
    		}
        }
    }
    
    private  class HThread extends Thread {
    	
    	public String failedReason = "Unknown";
    	
    	public HThread(String name) {
    		super();
    		failedReason = "Unknown";
    	}
    	
    	@Override
    	public void run() {
    		HService hService = ServiceFinder.getService(HService.class, "HComponent");
    		failedReason = hService.test();
        }
    }

}


