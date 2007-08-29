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

package org.apache.tuscany.sca.data.engine;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;
import org.apache.tuscany.sca.data.engine.config.ConnectionProperties;

/**
 * Implements a STAX artifact processor for ConnectionInfo.
 * This processor is shared between implementation.das and implementation.data component type  implementations,
 * and can be used for other applications that require database connectivity information.
 * 
 * The artifact processor is responsible for processing <ConnectionInfo>
 * elements in SCA assembly XML composite files.
 * 
 * @version $Rev$ $Date$
 */
public class ConnectionInfoArtifactProcessor implements StAXArtifactProcessor<ConnectionInfo> {
    private static final QName CONNECTION_INFO = new QName(Constants.SCA10_TUSCANY_NS, "connectionInfo");
    private static final QName CONNECTION_PROPERTIES = new QName(Constants.SCA10_TUSCANY_NS, "connectionProperties");
    
    public ConnectionInfoArtifactProcessor(ModelFactoryExtensionPoint modelFactories) {

    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return CONNECTION_INFO;
    }

    public Class<ConnectionInfo> getModelType() {
        // Returns the type of model processed by this processor
        return ConnectionInfo.class;
    }

    /*
     * <component name="CompanyDataComponent">
     *   <implementation.data table="company">
     *
     *      <connectionInfo>
     *         <connectionProperties 
     *          driverClass="org.apache.derby.jdbc.EmbeddedDriver" 
     *          databaseURL="jdbc:derby:target/test-classes/dastest; create = true" 
     *          loginTimeout="600000"/>
     *      </connectionInfo>
     *
     *   </implementation.data>
     * </component>
     */
    public ConnectionInfo read(XMLStreamReader reader) throws ContributionReadException {
        assert CONNECTION_INFO.equals(reader.getName());

        // Create a ConnectionInfo from the component type model
        ConnectionInfo connectionInfo = new ConnectionInfo();

        /* 
         *  <connectionInfo dataSource="jdbc:derby:target/test-classes/dastest; create = true"/>
         */
        String dataSource = reader.getAttributeValue(null, "datasource"); // exclusive with connection properties
        if (dataSource != null && dataSource.length() > 0) {
            connectionInfo.setDataSource(dataSource);
        } else {
            try {
                int event = reader.next();
                while (event == XMLStreamConstants.CHARACTERS) {
                    event = reader.next();
                }
            } catch (XMLStreamException e) {
                throw new ContributionReadException(e);
            }

            QName element = reader.getName();
            
            assert CONNECTION_PROPERTIES.equals(element);
            
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

            // FIXME: validation sending info to monitor....
            ConnectionProperties connectionProperties = new ConnectionProperties();
            connectionProperties.setDriverClass(driverClass);
            connectionProperties.setDatabaseURL(databaseURL);
            connectionProperties.setUsername(username);
            connectionProperties.setPassword(password);
            connectionProperties.setLoginTimeout(loginTimeout);

            connectionInfo.setConnectionProperties(connectionProperties);
        }
        
        return connectionInfo;
    }

    public void resolve(ConnectionInfo impl, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(ConnectionInfo model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
