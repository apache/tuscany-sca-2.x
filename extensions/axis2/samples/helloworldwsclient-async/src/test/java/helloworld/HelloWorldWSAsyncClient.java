package helloworld;

import junit.framework.Assert;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * Test case for helloworld web service client
 */
public class HelloWorldWSAsyncClient extends SCATestCase {

    private HelloWorldLocal helloWorldLocal;

    @Override
    protected void setUp() throws Exception {
        try {
            setApplicationSCDL(HelloWorldCallback.class, "META-INF/sca/default.scdl");
            ClassLoader classLoader = getClass().getClassLoader();
            addExtension("test.extensions", classLoader.getResource("META-INF/tuscany/test-extensions.scdl"));

            super.setUp();
            CompositeContext compositeContext = CurrentCompositeContext.getContext();
            helloWorldLocal =
                compositeContext.locateService(HelloWorldLocal.class, "HelloWorldServiceComponent");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testWSClient() throws Exception {
        try {
            String msg = helloWorldLocal.getGreetings("John");
            Assert.assertEquals(msg, "Hola John");

            // Sleep for 2 seconds to wait the callback to happen
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
