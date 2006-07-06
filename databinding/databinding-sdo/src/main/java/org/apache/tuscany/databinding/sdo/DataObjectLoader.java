package org.apache.tuscany.databinding.sdo;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sdo.helper.XMLStreamHelper;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ModelObject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * A SDO model-based Loader to load DataObject from the XML stream
 *
 */
public class DataObjectLoader extends LoaderExtension {
    private QName propertyQName;

    public DataObjectLoader(Property property) {
        super();
        this.propertyQName = new QName(XSDHelper.INSTANCE.getNamespaceURI(property), XSDHelper.INSTANCE.getLocalName(property));
    }

    public DataObjectLoader(QName propertyQName) {
        super();
        this.propertyQName = propertyQName;
    }

    @Override
    public QName getXMLType() {
        return propertyQName;
    }

    public ModelObject load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert propertyQName.equals(reader.getName());
        // TODO: We need a way to get TypeHelper from deploymentContext
        TypeHelper typeHelper = TypeHelper.INSTANCE;
        XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(typeHelper);
        DataObject dataObject = streamHelper.loadObject(reader);
        // TODO: Is it required that the object always extends from ModelObject?
        return new ModelDataObject(dataObject);
    }

}
