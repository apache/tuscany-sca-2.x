package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UndefinedReferenceException;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.isNull;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderReferenceTestCase extends TestCase {
    private static final QName COMPONENT = new QName(XML_NAMESPACE_1_0, "component");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");
    private static final String NAME = "testComponent";
    private ComponentLoader loader;
    private XMLStreamReader reader;

    /**
     * Verifies an error is thrown when an attempt to configure a non-existent reference in SCDL is made
     */
    public void testNoReferenceOnComponentType() throws LoaderException, XMLStreamException {
        try {
            loader.load(null, null, reader, null);
            fail();
        } catch (UndefinedReferenceException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        JavaImplementation impl = new JavaImplementation(null, type);
        reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(COMPONENT);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn(null);
        EasyMock.expect(reader.getAttributeValue(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(null);
        EasyMock.expect(reader.nextTag()).andReturn(0);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(REFERENCE);
        EasyMock.expect(reader.getAttributeValue((String) isNull(), EasyMock.eq("name")))
            .andReturn("noreference");
        EasyMock.expect(reader.getElementText()).andReturn("text");

        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        LoaderRegistry mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        mockRegistry.loadComponentType((CompositeComponent) isNull(),
            EasyMock.isA(Implementation.class),
            (DeploymentContext) isNull());
        EasyMock.expect(mockRegistry.load((CompositeComponent) isNull(),
            (ModelObject) isNull(),
            EasyMock.isA(XMLStreamReader.class),
            (DeploymentContext) isNull())).andReturn(impl);
        EasyMock.replay(mockRegistry);
        loader = new ComponentLoader(mockRegistry, null);

    }

}
