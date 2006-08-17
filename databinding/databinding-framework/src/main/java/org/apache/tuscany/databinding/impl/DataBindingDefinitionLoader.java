/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.databinding.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.model.DataBindingDefinition;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.osoa.sca.annotations.Constructor;

/**
 * The StAX loader for data binding
 */
public class DataBindingDefinitionLoader extends LoaderExtension<DataBindingDefinition> {
    public static final QName DATA_BINDING = new QName("http://tuscany.apache.org/xmlns/databinding/1.0", "databinding");

    @Constructor( { "registry" })
    public DataBindingDefinitionLoader(@Autowire
    LoaderRegistry registry) {
        super(registry);
    }

    @Override
    public QName getXMLType() {
        return DATA_BINDING;
    }

    public DataBindingDefinition load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
            throws XMLStreamException, LoaderException {
        assert DATA_BINDING.equals(reader.getName());
        DataBindingDefinition definition = new DataBindingDefinition();
        String name = reader.getAttributeValue(null, "name");
        definition.setName(name);
        String javaType = reader.getAttributeValue(null, "javaType");
        if (javaType != null) {
            Class<?> cls;
            try {
                cls = Class.forName(javaType, false, deploymentContext.getClassLoader());
                definition.setJavaType(cls);
            } catch (ClassNotFoundException e) {
                LoaderException ex = new LoaderException(e);
                ex.addContextName(javaType);
                throw ex;
            }
        }
        String xmlType = reader.getAttributeValue(null, "xmlType");
        if (xmlType != null) {
            definition.setXmlType(new QName(xmlType));
        }
        return definition;
    }
}
