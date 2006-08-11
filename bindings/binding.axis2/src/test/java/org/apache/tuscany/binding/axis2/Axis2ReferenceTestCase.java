package org.apache.tuscany.binding.axis2;

import java.lang.reflect.Method;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.xml.sax.InputSource;

import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

public class Axis2ReferenceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
        Axis2Reference axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
        Method operation = Greeter.class.getMethod("sayHi");
        TargetInvoker targetInvoker = axis2Reference.createTargetInvoker(operation);
        assertNotNull(targetInvoker);
    }

    private Axis2Reference createAxis2Reference(String webAppName, String serviceName) throws Exception {
        //Create WebServiceBinding
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
        WebServiceBinding wsBinding = new WebServiceBinding(wsdlDef, port, "uri", "portURI", wsdlService);
        wsBinding.setWebAppName(webAppName);
        //Create a mocked WireService, make the call of ServiceExtension.getServiceInstance() returns a proxy instance.
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        EasyMock.replay(wireService);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        // TODO figure out what to do with the service contract
        ServiceContract contract = new WSDLServiceContract();
        contract.setInterfaceClass(Greeter.class);
        return new Axis2Reference(serviceName, parent, wireService, wsBinding, contract);
    }
}
