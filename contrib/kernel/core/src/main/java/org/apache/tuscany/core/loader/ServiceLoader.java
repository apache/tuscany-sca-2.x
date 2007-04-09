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
package org.apache.tuscany.core.loader;

import java.net.URI;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Loads a service definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ServiceLoader extends LoaderExtension<ServiceDefinition> {
    private static final QName SERVICE = new QName(SCA_NS, "service");

    @Constructor
    public ServiceLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return SERVICE;
    }

    public ServiceDefinition load(ModelObject object, XMLStreamReader reader, DeploymentContext context)
        throws XMLStreamException, LoaderException {
        assert SERVICE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        URI compositeId = context.getComponentId();
        URI componentBase = URI.create(compositeId + "/");
        ServiceDefinition def = new ServiceDefinition();
        def.setUri(compositeId.resolve('#' + name));

        URI targetUri = null;
        String promote = reader.getAttributeValue(null, "promote");
        if (promote != null) {
            QualifiedName qName = new QualifiedName(promote);
            targetUri = componentBase.resolve(qName.getFragment());
        }
        while (true) {
            int i = reader.next();
            switch (i) {
                case START_ELEMENT:
                    ModelObject o = registry.load(null, reader, context);
                    if (o instanceof ServiceContract) {
                        def.setServiceContract((ServiceContract) o);
                    } else if (o instanceof BindingDefinition) {
                        def.addBinding((BindingDefinition) o);
                    } else {
                        throw new UnrecognizedElementException(reader.getName());
                    }
                    break;
                case END_ELEMENT:
                    if (SERVICE.equals(reader.getName())) {
                        if (targetUri != null) {
                            def.setTarget(targetUri);
                        }
                        return def;
                    }
                    break;
            }
        }
    }
}
