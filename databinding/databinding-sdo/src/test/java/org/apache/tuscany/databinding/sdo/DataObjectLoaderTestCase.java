package org.apache.tuscany.databinding.sdo;

import java.io.StringReader;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import commonj.sdo.helper.XSDHelper;

public class DataObjectLoaderTestCase extends TestCase {

    private XSDHelper xsdHelper = XSDHelper.INSTANCE;

    private QName name = new QName("http://www.osoa.org/xmlns/mock/0.9", "implementation.mock");

    private String xml = "<module name=\"m\" xmlns=\"http://www.osoa.org/xmlns/sca/0.9\" xmlns:mock=\"http://www.osoa.org/xmlns/mock/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.osoa.org/xmlns/mock/0.9 sca-implementation-mock.xsd http://www.osoa.org/xmlns/sca/0.9 sca-core.xsd \"><component name=\"c\"><mock:implementation.mock myAttr=\"helloworld.HelloWorldImpl\" /></component></module>";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getClassLoader().getResource("model/sca-implementation-mock.xsd");
        // URL url = getClass().getClassLoader().getResource("model/sca-core.xsd");
        xsdHelper.define(url.openStream(), url.toExternalForm());
    }

    public void testLoader() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
        int event = reader.getEventType();
        while (!(event == XMLStreamConstants.START_ELEMENT && reader.getName().equals(name)) && reader.hasNext()) {
            event = reader.nextTag();
        }
        DataObjectLoader loader = new DataObjectLoader(name);
        DeploymentContext context = new RootDeploymentContext(getClass().getClassLoader(), inputFactory, new ModuleScopeContainer());
        ModelDataObject modelObject = (ModelDataObject) loader.load(null, reader, context);
        Assert.assertNotNull(modelObject.getDataObject());
        Assert.assertTrue(modelObject.getDataObject().getString("myAttr").equals("helloworld.HelloWorldImpl"));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
