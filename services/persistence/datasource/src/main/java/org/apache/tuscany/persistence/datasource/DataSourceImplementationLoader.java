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
package org.apache.tuscany.persistence.datasource;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loads a data source component type from an assembly
 * <p/>
 * TODO document format
 *
 * @version $Rev$ $Date$
 */
public class DataSourceImplementationLoader extends LoaderExtension {
    private static final QName DATASOURCE =
        new QName("http://tuscany.apache.org/xmlns/system/1.0-SNAPSHOT", "implementation.ds");
    private static final String PROVIDER = "provider";

    public DataSourceImplementationLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return DATASOURCE;
    }

    public ModelObject load(CompositeComponent parent,
                            ModelObject object,
                            XMLStreamReader reader,
                            DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        String driverName = reader.getAttributeValue(null, PROVIDER);
        if (driverName == null) {
            throw new LoaderException("No provider specified for DataSource");
        }

        DataSourceImplementation implementation = new DataSourceImplementation();
        implementation.setProviderName(driverName);
        implementation.setClassLoader(deploymentContext.getClassLoader());

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    // load configuration paramters
                    String paramName = reader.getName().getLocalPart();
                    String val = reader.getElementText().trim();
                    implementation.addConfigurationParam(paramName, val);
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(DATASOURCE)) {
                        return implementation;
                    }
            }
        }
    }
}
