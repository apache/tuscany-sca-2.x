package org.apache.tuscany.binding.celtix.loader;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.easymock.EasyMock;

public class WebServiceBindingLoaderTestCase extends TestCase {

    @SuppressWarnings("deprecation")
    public void testLoad() throws Exception {
        
        WebServiceBindingLoader loader = new WebServiceBindingLoader();
        StAXLoaderRegistry reg = EasyMock.createNiceMock(StAXLoaderRegistry.class);
        reg.getContext();
        EasyMock.expectLastCall().andReturn(EasyMock.createNiceMock(AssemblyContext.class));
        EasyMock.replay(reg);
            
        loader.setRegistry(reg);
        
        XMLStreamReader reader = EasyMock.createNiceMock(XMLStreamReader.class);
        reader.getAttributeValue(null, "uri");
        EasyMock.expectLastCall().andReturn("http://objectweb.org/hello_world_soap_http");
        reader.getAttributeValue(null, "port");
        EasyMock.expectLastCall().andReturn("SoapPort");
        EasyMock.replay(reader);
        
        LoaderContext loaderContext = new LoaderContext(null);
        
        assertNotNull("Did not load binding", loader.load(reader, loaderContext));
        
    }

}
