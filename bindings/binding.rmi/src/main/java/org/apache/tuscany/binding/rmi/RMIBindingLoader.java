/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.binding.rmi;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.osoa.sca.annotations.Scope;

/**
 * Loader for handling <binding.rmi> elements.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class RMIBindingLoader extends LoaderExtension<RMIBinding> {
    public static final QName BINDING_RMI = new QName("http://tuscany.apache.org/xmlns/binding/rmi/1.0-SNAPSHOT",
                                                      "binding.rmi");

    public RMIBindingLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return BINDING_RMI;
    }

    public RMIBinding load(CompositeComponent parent,
                           XMLStreamReader reader,
                           DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        String uri = reader.getAttributeValue(null, "uri");
        LoaderUtil.skipToEndElement(reader);

        RMIBinding binding = new RMIBinding();
        binding.setURI(uri);
        return binding;
    }
}
