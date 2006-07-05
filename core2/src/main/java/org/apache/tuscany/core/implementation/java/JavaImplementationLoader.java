package org.apache.tuscany.core.implementation.java;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.loader.AssemblyConstants;
import org.apache.tuscany.core.loader.StAXUtil;

public class JavaImplementationLoader extends LoaderExtension {
    public static final QName IMPLEMENTATION_JAVA = new QName(AssemblyConstants.SCA_NAMESPACE, "implementation.java");

    @Override
    public QName getXMLType() {
        return IMPLEMENTATION_JAVA;
    }

    public ModelObject load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert IMPLEMENTATION_JAVA.equals(reader.getName());
        JavaImplementation implementation = new JavaImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = StAXUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(parent, implementation, deploymentContext);
        StAXUtil.skipToEndElement(reader);
        return implementation;
    }

}
