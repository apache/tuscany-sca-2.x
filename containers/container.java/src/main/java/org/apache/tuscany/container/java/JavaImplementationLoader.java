package org.apache.tuscany.container.java;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.loader.AssemblyConstants;
import org.apache.tuscany.core.loader.StAXUtil;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ModelObject;

public class JavaImplementationLoader extends LoaderExtension {
    public static final QName IMPLEMENTATION_JAVA = new QName(AssemblyConstants.SCA_NAMESPACE, "implementation.java");

    @Override
    protected QName getXMLType() {
        return IMPLEMENTATION_JAVA;
    }

    public ModelObject load(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert IMPLEMENTATION_JAVA.equals(reader.getName());
        JavaImplementation implementation = new JavaImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = StAXUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(implementation, deploymentContext);
        StAXUtil.skipToEndElement(reader);
        return implementation;
    }

}
