package org.apache.tuscany.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.launcher.LauncherImpl;
import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * @version $Rev$ $Date$
 */
public class Bootstrapper extends TestCase {
    protected CompositeComponent<?> component;
    private CompositeContextImpl context;
    private Map<String, URL> extensions = new HashMap<String, URL>();
    private String applicationSCDL = LauncherImpl.METAINF_APPLICATION_SCDL_PATH;

    protected void setUp() throws Exception {
        super.setUp();
        ClassLoader cl = getClass().getClassLoader();
        TestLauncher launcher = new TestLauncher();
        launcher.setApplicationLoader(cl);
        CompositeComponent<?> composite = launcher.bootRuntime(cl.getResource(LauncherImpl.METAINF_SYSTEM_SCDL_PATH),
            new NullMonitorFactory());

        for (String extensionName : extensions.keySet()) {
            deployExtension(composite, extensionName, extensions.get(extensionName));
        }

        URL applicationScdlURL = cl.getResource(applicationSCDL);
        if (applicationScdlURL == null) {
            throw new RuntimeException("application SCDL not found: " + applicationSCDL);
        }
        component = launcher.bootApplication("application", applicationScdlURL);
        component.start();
        context = new CompositeContextImpl(component);
        context.start();
    }

    /**
     * A TestCase can use this to overide the default SCDL location of "META-INF/sca/default.scdl"
     */
    protected void setApplicationSCDL(String applicationSCDL) {
        this.applicationSCDL = applicationSCDL;
    }

    /**
     * A TestCase can use this to add the SCDL location of an extention to be deployed to the runtime
     */
    protected void addExtension(String extensionName, URL extentionSCDL) {
        extensions.put(extensionName, extentionSCDL);
    }

    protected void deployExtension(CompositeComponent<?> composite, String extensionName, URL scdlURL)
        throws LoaderException {
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        implementation.setScdlLocation(scdlURL);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{scdlURL}, getClass().getClassLoader());
        implementation.setClassLoader(classLoader);

        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(extensionName,
                implementation);

        Deployer deployer = (Deployer) composite.getChild("deployer").getServiceInstance();
        Component<?> component = deployer.deploy(composite, definition);

        component.start();
    }

    protected void tearDown() throws Exception {
        context.stop();
        component.stop();
        super.tearDown();
    }
}
