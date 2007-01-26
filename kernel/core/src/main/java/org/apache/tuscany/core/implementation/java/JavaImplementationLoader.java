/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
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

    public ModelObject load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
                            DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert IMPLEMENTATION_JAVA.equals(reader.getName());
        String implClass = reader.getAttributeValue(null, "class");
        Class<?> implementationClass = LoaderUtil.loadClass(implClass, deploymentContext.getClassLoader());

        JavaImplementation implementation = new JavaImplementation();
        implementation.setClassName(implClass);
        implementation.setImplementationClass(implementationClass);
        registry.loadComponentType(parent, implementation, deploymentContext);
        LoaderUtil.skipToEndElement(reader);
        return implementation;
    }

}
