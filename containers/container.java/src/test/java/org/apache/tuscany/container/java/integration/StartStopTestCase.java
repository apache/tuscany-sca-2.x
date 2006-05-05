package org.apache.tuscany.container.java.integration;

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;
import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.CurrentModuleContext;

/**
 * @version $Rev$ $Date$
 */
public class StartStopTestCase extends TestCase {
    private ClassLoader oldCL;

    public void testHelloWorld() throws Exception {
        TuscanyRuntime tuscany = new TuscanyRuntime("test", null);
        tuscany.start();
        ModuleContext moduleContext = CurrentModuleContext.getContext();
        assertNotNull(moduleContext);

        HelloWorldService helloworldService = (HelloWorldService) moduleContext.locateService("HelloWorld");
        assertNotNull(helloworldService);

        String value = helloworldService .getGreetings("World");
        assertEquals("Hello World", value);
        tuscany.stop();
        tuscany = new TuscanyRuntime("test", null);
        tuscany.start();
        moduleContext = CurrentModuleContext.getContext();
        assertNotNull(moduleContext);
        helloworldService = (HelloWorldService) moduleContext.locateService("HelloWorld");
        assertNotNull(helloworldService);
        value = helloworldService .getGreetings("World");
        assertEquals("Hello World", value);
        tuscany.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getResource("/helloworldmc/");
        ClassLoader cl = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(oldCL);
        super.tearDown();
    }
}
