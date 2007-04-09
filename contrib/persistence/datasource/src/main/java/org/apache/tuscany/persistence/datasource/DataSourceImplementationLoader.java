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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loads a DataSource component type from an assembly. This component implementation type provides a
 * <code>DataSource</code> system service in the runtime. The actual DataSource provider is pluggable, for example,
 * Commons DBCP could be used. DataSource components are configured as follows:
 * <pre>
 * <p/>
 *      <component name="MyDataSource">
 *          <system:implementation.ds provider="org.foo.FooProvider"/>
 *          <property name="driverClassName">com.mysql.jdbc.Driver</property>
 *          <property name="url">jdbc:mysql://localhost:3306/mydb</property>
 *          <property name="login">foo</property>
 *          <property name="password">bar</property>
 *      </component>
 * <p/>
 * </pre>
 * In the above example, <code>org.foo.FooProvider</code> is responsible for bootstrapping the actual DataSource
 * implementation. It may implement <code>javax.sql.DataSource</code> directly or the {@link DataSourceProvider}
 * interface and must have a public no-args constructor. Configuration parameters are simple types specified as
 * properties. Configuration parameters, i.e. properties, will vary are introspected from the provider class. A
 * component type containing thse properties is dynamically generated and consists of all JavaBean setter methods that
 * take a single simple type parameter.
 *
 * @version $Rev$ $Date$
 */
public class DataSourceImplementationLoader extends LoaderExtension {
    private static final QName DATASOURCE =
        new QName("http://tuscany.apache.org/xmlns/system/1.0-SNAPSHOT", "implementation.ds");
    private static final String PROVIDER = "provider";

    public DataSourceImplementationLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return DATASOURCE;
    }

    public ModelObject load(
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
        LoaderUtil.skipToEndElement(reader);
        return implementation;
    }
}
