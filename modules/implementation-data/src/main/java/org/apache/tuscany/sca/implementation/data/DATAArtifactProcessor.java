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
package org.apache.tuscany.sca.implementation.data;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.data.config.ConnectionInfo;
import org.apache.tuscany.sca.implementation.data.config.ConnectionProperties;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;


/**
 * Implements a STAX artifact processor for DATA implementations.
 * 
 * The artifact processor is responsible for processing <implementation.data>
 * elements in SCA assembly XML composite files and populating the DATA
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML.
 * 
 * @version $Rev$ $Date$
 */
public class DATAArtifactProcessor implements StAXArtifactProcessor<DATAImplementation> {
    protected static final QName IMPLEMENTATION_DATA = new QName(Constants.SCA10_NS, "implementation.data");
    
    private static final QName CONNECTION_INFO = new QName(Constants.SCA10_NS, "connectionInfo");
    private static final QName CONNECTION_PROPERTIES = new QName(Constants.SCA10_NS, "connectionProperties");

    
    private DATAImplementationFactory dataFactory;
    
    public DATAArtifactProcessor(ModelFactoryExtensionPoint modelFactories) {
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        this.dataFactory = new DATAImplementationFactory(assemblyFactory, javaFactory);
    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return IMPLEMENTATION_DATA;
    }

    public Class<DATAImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return DATAImplementation.class;
    }

    /*
     * <component name="CompanyDataComponent">
     *   <implementation.data table="company">
     *      <connectionInfo>
     *         <connectionProperties 
     *          driverClass="org.apache.derby.jdbc.EmbeddedDriver" 
     *          databaseURL="jdbc:derby:target/test-classes/dastest; create = true" 
     *          loginTimeout="600000"/>
     *      </connectionInfo>
     *   </implementation.data>
     * </component>
     */
    public DATAImplementation read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPLEMENTATION_DATA.equals(reader.getName());

        // Read an <implementation.data> element
        try {

            // Create an initialize the DAS implementation model
            DATAImplementation implementation = dataFactory.createDASImplementation();

            //FIXME: validation sending info to monitor....
            String table = reader.getAttributeValue(null, "table");
            implementation.setTable(table); //required                        

            while (true) {
                int event = reader.next();
                switch (event) {

                case START_ELEMENT:
                    QName element = reader.getName();
                    
                    if (CONNECTION_INFO.equals(element)) {
                        /* 
                         *  <connectionInfo dataSource="jdbc:derby:target/test-classes/dastest; create = true"/>
                         */
                        String dataSource = reader.getAttributeValue(null, "datasource"); //exclusive with connection properties
                        if (dataSource != null && dataSource.length() > 0) {
                            ConnectionInfo connectionInfo = new ConnectionInfo();
                            connectionInfo.setDataSource(dataSource);
                            implementation.setConnectionInfo(connectionInfo);
                        }
                    
                    } else if (CONNECTION_PROPERTIES.equals(element)) {
                        /*
                         * <connectionProperties 
                         *  driverClass="org.apache.derby.jdbc.EmbeddedDriver" 
                         *  databaseURL="jdbc:derby:target/test-classes/dastest; create = true"
                         *  username=""
                         *  password="" 
                         *  loginTimeout="600000"/>
                         */
                        String driverClass = reader.getAttributeValue(null, "driverClass");
                        String databaseURL = reader.getAttributeValue(null, "databaseURL");
                        String username = reader.getAttributeValue(null, "username");
                        String password = reader.getAttributeValue(null, "password");
                        int loginTimeout = Integer.parseInt(reader.getAttributeValue(null, "loginTimeout"));

                        //FIXME: validation sending info to monitor....
                        ConnectionInfo connectionInfo = new ConnectionInfo();
                        ConnectionProperties connectionProperties = new ConnectionProperties();
                        connectionProperties.setDriverClass(driverClass);
                        connectionProperties.setDatabaseURL(databaseURL);
                        connectionProperties.setUsername(username);
                        connectionProperties.setPassword(password);
                        connectionProperties.setLoginTimeout(loginTimeout);

                        connectionInfo.setConnectionProperties(connectionProperties);
                        implementation.setConnectionInfo(connectionInfo);
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (IMPLEMENTATION_DATA.equals(reader.getName())) {
                        return implementation;
                    }                    
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }

    }

    public void resolve(DATAImplementation impl, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(DATAImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
