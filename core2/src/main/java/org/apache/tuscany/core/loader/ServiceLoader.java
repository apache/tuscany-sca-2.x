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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class ServiceLoader extends LoaderExtension<ServiceDefinition> {
    public ServiceLoader() {
    }

    public ServiceLoader(LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return AssemblyConstants.SERVICE;
    }

    public ServiceDefinition load(XMLStreamReader reader, DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert AssemblyConstants.SERVICE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        Binding binding = null;
        ServiceContract serviceContract = null;
        while (true) {
            int i = reader.next();
            switch (i) {
                case START_ELEMENT:
                    ModelObject o = registry.load(reader, deploymentContext);
                    if (o instanceof ServiceContract) {
                        serviceContract = (ServiceContract) o;
                    } else if (o instanceof Binding) {
                        binding = (Binding) o;
                    }
                    break;
                case END_ELEMENT:
                    if (binding != null) {
                        return new BoundServiceDefinition<Binding>(name, serviceContract, binding, null);
                    } else {
                        return new ServiceDefinition(name, serviceContract);
                    }
            }
        }
    }
}
