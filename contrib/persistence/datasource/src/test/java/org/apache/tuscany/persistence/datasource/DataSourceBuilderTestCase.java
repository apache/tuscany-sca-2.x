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

import java.net.URI;
import javax.sql.DataSource;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DataSourceBuilderTestCase extends TestCase {

    public void testBuild() throws Exception {
        ScopeContainer scope = EasyMock.createMock(ScopeContainer.class);
        EasyMock.replay(scope);
        DeploymentContext ctx = EasyMock.createMock(DeploymentContext.class);
        EasyMock.replay(ctx);

        DataSourceImplementation implementation = new DataSourceImplementation();
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        JavaServiceContract serviceContract = new JavaServiceContract(DataSource.class);
        ServiceDefinition service = new ServiceDefinition(URI.create("DataSource"), serviceContract, false);
        componentType.add(service);
        componentType.setInitLevel(1);
        implementation.setComponentType(componentType);
        implementation.setProviderName(MockProvider.class.getName());
        implementation.setClassLoader(getClass().getClassLoader());

        ComponentDefinition<DataSourceImplementation> def =
            new ComponentDefinition<DataSourceImplementation>(URI.create("MyDS"), implementation);

        DataSourceBuilder builder = new DataSourceBuilder();

        DataSourceComponent component = (DataSourceComponent) builder.build(def, ctx);
        assertEquals(URI.create("MyDS"), component.getUri());

        DataSource ds = (DataSource) component.createInstance();
        assertNotNull(ds);
        EasyMock.verify(ctx);
        EasyMock.verify(scope);
    }

    private static class MockProvider implements DataSourceProvider {

        public MockProvider() {
        }

        public void init() throws ProviderException {

        }

        public void close() throws ProviderException {

        }

        public DataSource getDataSource() throws ProviderException {
            return EasyMock.createNiceMock(DataSource.class);
        }
    }


}
