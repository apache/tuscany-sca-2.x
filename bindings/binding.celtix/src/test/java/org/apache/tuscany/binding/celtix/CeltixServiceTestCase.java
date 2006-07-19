package org.apache.tuscany.binding.celtix;

import java.net.URL;

import org.xml.sax.InputSource;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.Service;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.InboundWire;

import org.objectweb.celtix.Bus;
import org.objectweb.celtix.bindings.DataBindingCallback;
import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bindings.ServerBindingEndpointCallback;
import org.objectweb.celtix.bus.bindings.WSDLMetaDataCache;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.bus.bindings.soap.SOAPServerBinding;
import org.objectweb.celtix.bus.bindings.soap.SOAPBindingFactory;
import org.objectweb.celtix.bindings.BindingManager;
import org.objectweb.celtix.context.ObjectMessageContextImpl;
import org.objectweb.celtix.ws.addressing.EndpointReferenceType;
import org.apache.tuscany.binding.celtix.io.SCADataBindingCallback;

/**
 * @version $Rev$ $Date$
 */
public class CeltixServiceTestCase extends TestCase {

    public void testGetDataBindingCallback() throws Exception {
        //Make following call to return a mocked SOAPClientBinding:
        //bus.getBindingManager().getBindingFactory(bindingId).createClientBinding(reference)
        SOAPServerBinding serverBinding = EasyMock.createMock(SOAPServerBinding.class);
        serverBinding.activate();
        EasyMock.replay(serverBinding);

        ServerBindingEndpointCallback callback = EasyMock.createNiceMock(ServerBindingEndpointCallback.class);

        SOAPBindingFactory bindingFactory = EasyMock.createNiceMock(SOAPBindingFactory.class);
        bindingFactory.createServerBinding(EasyMock.isA(EndpointReferenceType.class),
                                           EasyMock.isA(ServerBindingEndpointCallback.class));
        EasyMock.expectLastCall().andReturn(serverBinding);
        EasyMock.replay(bindingFactory);

        BindingManager bindingManager = EasyMock.createNiceMock(BindingManager.class);
        String bindingId = "http://schemas.xmlsoap.org/wsdl/soap/";
        bindingManager.getBindingFactory(bindingId);
        EasyMock.expectLastCall().andReturn(bindingFactory);

        Bus bus = EasyMock.createNiceMock(Bus.class);
        bus.getBindingManager();
        EasyMock.expectLastCall().andReturn(bindingManager);
        EasyMock.replay(bindingManager);
        EasyMock.replay(bus);

        WireService wireService = EasyMock.createNiceMock(WireService.class);
        wireService.createProxy(EasyMock.isA(InboundWire.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(wireService);

        //Create WSDL Definition
        String wsdlLocation = "/wsdl/hello_world_doc_lit.wsdl";
        URL url = getClass().getResource(wsdlLocation);
        assertNotNull("Could not find wsdl " + url.toString(), url);

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        InputSource input = new InputSource(url.openStream());
        Definition wsdlDef = reader.readWSDL(url.toString(), input);
        Service wsdlService = wsdlDef.getService(new QName("http://objectweb.org/hello_world_soap_http",
                                                           "SOAPService"));
        Port port = wsdlService.getPort("SoapPort");

        InboundWire inboundWire = EasyMock.createNiceMock(InboundWire.class);
        inboundWire.getBusinessInterface();
        EasyMock.expectLastCall().andReturn(Greeter.class).anyTimes();

        EasyMock.replay(inboundWire);

        CeltixService celtixService = new CeltixService("name", wsdlDef, port, wsdlService, null, bus, wireService);
        celtixService.setInboundWire(inboundWire);
        celtixService.start();

        QName operationName = new QName("greetMe");
     	ServerDataBindingCallback callback1 = celtixService.getDataBindingCallback(operationName, new ObjectMessageContextImpl(), DataBindingCallback.Mode.PARTS);
        assertNotNull(callback1);
    }

}
