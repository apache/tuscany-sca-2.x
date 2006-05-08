package org.apache.tuscany.tomcat.integration;

import java.io.File;

import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;

import org.apache.tuscany.tomcat.TuscanyValve;

/**
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"ClassLoader2Instantiation"})
public class TomcatStandaloneTestCase extends AbstractTomcatTest {
    protected File app2;

    public void testRuntimeIntegration() throws Exception {
        StandardContext ctx = new StandardContext();

        // caution: this sets the parent of the webapp loader to the test classloader so it can find TestServlet
        // anything that relies on the TCCL may not work correctly
        ClassLoader cl = TestServlet.class.getClassLoader();
        ctx.setParentClassLoader(cl);

        ctx.addLifecycleListener(new ContextConfig());
        ctx.setName("/testContext");
        ctx.setDocBase(app2.getAbsolutePath());

// Doesn't work, see TUSCANY-328        
//        host.addChild(ctx);
//        boolean found = false;
//        for (Valve valve: ctx.getPipeline().getValves()) {
//            if (valve instanceof TuscanyValve) {
//                found = true;
//                break;
//            }
//        }
//        assertFalse("TuscanyValve in pipeline", found);

//        request.setContext(ctx);
//        request.setWrapper((Wrapper) ctx.findChild("TestServlet"));
//        host.invoke(request, response);

//        host.removeChild(ctx);
    }

    protected void setUp() throws Exception {
        super.setUp();
        app2 = new File(getClass().getResource("/app2").toURI());
        File baseDir = new File(app2, "../../tomcat").getCanonicalFile();
        setupTomcat(baseDir, new StandardHost());
        engine.start();
    }

    protected void tearDown() throws Exception {
        engine.stop();
        super.tearDown();
    }

}
