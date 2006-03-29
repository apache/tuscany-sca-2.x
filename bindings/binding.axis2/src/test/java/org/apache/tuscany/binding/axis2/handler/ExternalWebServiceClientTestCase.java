package org.apache.tuscany.binding.axis2.handler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.builder.ExternalWebServiceBuilder;
import org.apache.tuscany.binding.axis2.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalWebServiceClient;
import org.apache.tuscany.binding.axis2.loader.WebServiceSCDLModelLoader;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.java.loader.JavaSCDLModelLoader;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 * Tests the ExternalWebServiceClient class by making a call to the helloworld WS
 * TODO: commented out. How to do run this as part of the build
 */
public class ExternalWebServiceClientTestCase extends TestCase {

    private ExternalWebServiceClient ewsc;

    public void testInvoke() {
//        String operation = "getGreetings";
//        Object s = "Petra";
//        Object o = ewsc.invoke(operation, new Object[] { s });
//
//        assertEquals("Hello " + s, o);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initExternalWebServiceClient();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void initExternalWebServiceClient() {
        ResourceLoader resourceLoader = new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader());
        WebServiceSCDLModelLoader wsLoader = new WebServiceSCDLModelLoader();
        JavaSCDLModelLoader javaLoader = new JavaSCDLModelLoader();
        List<SCDLModelLoader> scdlLoaders = new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(javaLoader);
        scdlLoaders.add(wsLoader);
        AssemblyModelLoader assemblyLoader = new SCDLAssemblyModelLoaderImpl(scdlLoaders);
        AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
        AssemblyModelContext modelContext = new AssemblyModelContextImpl(assemblyFactory, assemblyLoader, resourceLoader);

        Module module = assemblyLoader.loadModule(getClass().getResource("sca.module").toString());
        module.initialize(modelContext);

        Assert.assertTrue(module.getName().equals("org.apache.tuscany.binding.axis2.handler.helloworld"));

        Component component = module.getComponent("HelloWorldServiceComponent");
        Assert.assertTrue(component != null);

        ExternalService externalService = module.getExternalService("HelloWorldService");
        Assert.assertTrue(externalService != null);

        Binding binding = externalService.getBindings().get(0);
        Assert.assertTrue(binding instanceof WebServiceBinding);

        ExternalWebServiceBuilder mewsb = new ExternalWebServiceBuilder();
        mewsb.setProxyFactoryFactory(new JDKProxyFactoryFactory());
        mewsb.build(externalService);

        ExternalWebServiceContextFactory f = (ExternalWebServiceContextFactory) externalService.getConfiguredService().getContextFactory();
        ExternalServiceContext context = f.createContext();
        this.ewsc = (ExternalWebServiceClient) context.getImplementationInstance();
        assertNotNull(ewsc);
    }

}
