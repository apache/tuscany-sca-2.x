package org.apache.tuscany.binding.rmi;

import org.apache.tuscany.test.SCATestCase;

public class BindingTestCase extends SCATestCase {
    //private HelloWorldService helloWorldService;
    //private HelloWorldRmiService helloWorldRmiService;

    public void testRmiService() {
        // System.out.println(helloWorldRmiService.sayRmiHello("Tuscany World!"));
    }

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


    protected void tearDown() throws Exception {
//        super.tearDown();
    }

}
