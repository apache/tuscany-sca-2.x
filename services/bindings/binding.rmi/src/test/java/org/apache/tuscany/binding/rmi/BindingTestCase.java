package org.apache.tuscany.binding.rmi;

import helloworld.HelloWorldRmiService;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

// TODO: renamed to XXX as it doesn't work for me
public class BindingTestCase extends SCATestCase {
    private HelloWorldRmiService helloWorldRmiService;
 
    public void testRmiService() {
        System.out.println(helloWorldRmiService.sayRmiHello("Tuscany World!"));
        assertEquals("Hello from the RMI Service to - Tuscany World! thro the RMI Reference",
                helloWorldRmiService.sayRmiHello("Tuscany World!"));
    }

    protected void setUp() throws Exception {
        addExtension("rmi.binding",
                     getClass().getClassLoader().getResource("META-INF/sca/rmi_extension.scdl"));
        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/default.scdl"));

        super.setUp();

        CompositeContext context = CurrentCompositeContext.getContext();
        helloWorldRmiService = context.locateService(HelloWorldRmiService.class,
                                                     "HelloWorldRmiServiceComponent");
    }


    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
