package org.apache.tuscany.container.javascript.utils;

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.launcher.Launcher;
import org.apache.tuscany.spi.TuscanyException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;

public class JavaScriptTestCase extends TestCase {
    private Launcher launcher;
    private CompositeComponent<?> component;
    private CompositeContextImpl context;

    protected void setUp() throws Exception {
        super.setUp();
        ClassLoader cl = getClass().getClassLoader();
        launcher = new Launcher();
        launcher.setApplicationLoader(cl);
        CompositeComponent<?> composite = launcher.bootRuntime(cl.getResource(Launcher.METAINF_SYSTEM_SCDL_PATH));
        
        addJavaScriptExtension(composite);
        
        component = launcher.bootApplication(cl.getResource("org/apache/tuscany/container/javascript/functional/helloworld.scdl"));
        component.start();
        context = new CompositeContextImpl(component);
        context.start();
    }

    protected void addJavaScriptExtension(CompositeComponent<?> composite) {
        Deployer deployer = (Deployer) composite.getChild("deployer").getServiceInstance();
        
        
        URL scdlURL = getClass().getClassLoader().getResource("META-INF/sca/default.scdl");
        URL extensionURL = scdlURL;

        ClassLoader extensionCL = new URLClassLoader(new URL[]{extensionURL}, getClass().getClassLoader());

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        implementation.setScdlLocation(scdlURL);
        implementation.setClassLoader(extensionCL);
        ComponentDefinition<SystemCompositeImplementation> definition =
                new ComponentDefinition<SystemCompositeImplementation>("JS", implementation);

        try {
            CompositeComponent<?> parent = composite;
            Component<?> component = deployer.deploy(parent, definition);
            component.start();
        } catch (LoaderException e) {
            // FIXME handle the exception
        } catch (TuscanyException e) {
            // FIXME handle the exception
        }
        
    }

    protected void tearDown() throws Exception {
        context.stop();
        component.stop();
        super.tearDown();
    }
}
