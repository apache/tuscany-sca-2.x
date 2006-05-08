package org.apache.tuscany.binding.celtix.assembly;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import junit.framework.TestCase;

import org.apache.tuscany.binding.celtix.assembly.impl.WebServiceAssemblyFactoryImpl;
import org.apache.tuscany.binding.celtix.assembly.impl.WebServiceBindingImpl;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.easymock.EasyMock;

public class WebServiceAssemblyFactoryTestCase extends TestCase {

    
    private void setupMocks(AssemblyContext modelContext,
                            AssemblyModelLoader loader,
                            List<Definition> wsdlList) {
        EasyMock.reset(loader);
        EasyMock.reset(modelContext);
        modelContext.getAssemblyLoader();        
        EasyMock.expectLastCall().andReturn(loader);
        loader.loadDefinitions("http://objectweb.org/hello_world_soap_http");
        EasyMock.expectLastCall().andReturn(wsdlList);
        EasyMock.replay(loader);
        EasyMock.replay(modelContext);        
    }
    
    public void testCreate() throws Exception {
        WebServiceAssemblyFactoryImpl impl = new WebServiceAssemblyFactoryImpl();
        WebServiceBinding bind = impl.createWebServiceBinding();
        assertNotNull("Did not create the binding", bind);
        assertTrue("bind object wrong class: " + bind.getClass(),
                   bind instanceof WebServiceBindingImpl);
        
        assertNull("Should be initialized with null WSDL", bind.getWSDLDefinition());
        assertNull("Should be initialized with null port", bind.getWSDLPort());
        assertNull("Should be initialized with null service", bind.getWSDLService());
        assertNull("Should be initialized with null URI", bind.getURI());
        assertNull("Should be initialized with null TypeHelper",
                   bind.getTypeHelper());
        assertNull("Should be initialized with null ResourceLoader",
                   bind.getResourceLoader());
        
        bind.setURI("http://objectweb.org/hello_world_soap_http");
        bind.setPortURI("http://objectweb.org/hello_world_soap_http#SoapPort");
        
        AssemblyContext modelContext = EasyMock.createNiceMock(AssemblyContext.class);
        AssemblyModelLoader loader = EasyMock.createNiceMock(AssemblyModelLoader.class);
        
        WSDLReader reader =  WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        URL url = getClass().getResource("/wsdl/hello_world.wsdl");
        Definition definition = reader.readWSDL(url.toString());

        List<Definition> wsdlList = new ArrayList<Definition>();
        
        setupMocks(modelContext, loader, wsdlList);
        try {
            bind.initialize(modelContext);
            fail("Should have failed getting the wsdl");
        } catch (IllegalArgumentException ex) {
            //expected
        }
        
        setupMocks(modelContext, loader, wsdlList);

        
        wsdlList.add(definition);
        bind = impl.createWebServiceBinding();
        bind.setURI("http://objectweb.org/hello_world_soap_http");
        bind.setPortURI("http://objectweb.org/hello_world_soap_http#SoapPort");
        bind.initialize(modelContext);
    
        setupMocks(modelContext, loader, wsdlList);
        
        wsdlList.add(definition);
        bind = impl.createWebServiceBinding();
        bind.setURI("http://objectweb.org/hello_world_soap_http");
        bind.setPortURI("http://objectweb.org/hello_world_soap_http#FooPort");
        
        try {
            bind.initialize(modelContext);
            fail("Should have failed finding the port");
        } catch (IllegalArgumentException ex) {
            //expected
        }
    }
}
