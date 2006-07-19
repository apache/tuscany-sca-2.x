package org.apache.tuscany.core.implementation.java;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.annotation.Autowire;

public class JavaImplementationLoader extends LoaderExtension {
    public static final QName IMPLEMENTATION_JAVA = new QName(XML_NAMESPACE_1_0, "implementation.java");

    @Constructor({"registry"})
    public JavaImplementationLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    @Override
    public QName getXMLType() {
        return IMPLEMENTATION_JAVA;
    }

    public ModelObject load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert IMPLEMENTATION_JAVA.equals(reader.getName());
        JavaImplementation implementation = new JavaImplementation();
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = LoaderUtil.loadClass(implClass, deploymentContext.getClassLoader());
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(parent, implementation, deploymentContext);
        LoaderUtil.skipToEndElement(reader);
        return implementation;
    }

}
