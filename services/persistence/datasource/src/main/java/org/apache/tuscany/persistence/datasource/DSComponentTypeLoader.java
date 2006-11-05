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

import javax.sql.DataSource;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class DSComponentTypeLoader extends ComponentTypeLoaderExtension<DataSourceImplementation> {
    public DSComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    protected Class<DataSourceImplementation> getImplementationClass() {
        return DataSourceImplementation.class;
    }

    public void load(CompositeComponent parent, DataSourceImplementation implementation, DeploymentContext ctx)
        throws LoaderException {
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        JavaServiceContract serviceContract = new JavaServiceContract(DataSource.class);
        ServiceDefinition service = new ServiceDefinition("DataSource", serviceContract, false);
        componentType.add(service);
        componentType.setInitLevel(1);
        implementation.setComponentType(componentType);
    }
}
