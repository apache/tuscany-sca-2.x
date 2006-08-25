package org.apache.tuscany.binding.rmi; 

import helloworld.HelloWorldRmiService;
import helloworld.HelloWorldService;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class BindingTestCase extends SCATestCase {
    HelloWorldService helloWorldService;

    HelloWorldRmiService helloWorldRmiService;

    protected void setUp() throws Exception {
        //addExtension("rmi.binding",
        //             getClass().getClassLoader().getResource("META-INF/sca/rmi_extension.scdl"));

        //super.setUp();
        //System.out.println("Hit Enter");
        //System.in.read();

        //CompositeContext context = CurrentCompositeContext.getContext();
        //helloWorldRmiService = context.locateService(HelloWorldRmiService.class,
        //                                             "HelloWorldRmiServiceComponent");
                                                             
    }

    public void testRmiService() {
       // System.out.println(helloWorldRmiService.sayRmiHello("Tuscany World!"));
    }

    protected void tearDown() throws Exception {
        //super.tearDown();
    }

}
