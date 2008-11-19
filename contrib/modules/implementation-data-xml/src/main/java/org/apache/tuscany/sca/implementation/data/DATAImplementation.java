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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;
import org.apache.tuscany.sca.implementation.data.jdbc.JDBCHelper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;


/**
 * The model representing a sample DATA implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class DATAImplementation implements Implementation {
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    
    private ConnectionInfo connectionInfo;
    private List<Service> services = new ArrayList<Service>();
    
    /**
     * Constructs a new DAS implementation.
     */
    public DATAImplementation(AssemblyFactory assemblyFactory,
                              JavaInterfaceFactory javaFactory) {

        // DATA implementation provides one service per database table
        // exposing the DATA interface, and have no references and properties
        this.assemblyFactory = assemblyFactory;
        this.javaFactory = javaFactory;
    }
    
    private void introspectServices( AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        Connection connection = null;
        try {
            connection = JDBCHelper.getConnection(connectionInfo);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, "%", null);
            while(tables.next()) {
                //create the SCA service for the table
                Service dataService = assemblyFactory.createService();
                Service dataCollectionService = assemblyFactory.createService();
                
                dataService.setName(tables.getString(3)+"_DATA");
                dataCollectionService.setName(tables.getString(3));
                
                JavaInterface dataInterface;
                JavaInterface dataCollectionInterface;
                
                try {
                    dataInterface = javaFactory.createJavaInterface(DATA.class);
                    dataCollectionInterface = javaFactory.createJavaInterface(DATACollection.class);
                } catch (InvalidInterfaceException e) {
                    throw new IllegalArgumentException(e);
                }
                JavaInterfaceContract dataInterfaceContract = javaFactory.createJavaInterfaceContract();
                JavaInterfaceContract dataCollectionInterfaceContract = javaFactory.createJavaInterfaceContract();
                
                dataInterfaceContract.setInterface(dataInterface);
                dataCollectionInterfaceContract.setInterface(dataCollectionInterface);
                
                dataService.setInterfaceContract(dataInterfaceContract);
                dataCollectionService.setInterfaceContract(dataCollectionInterfaceContract);  
               
                services.add(dataService);
                services.add(dataCollectionService);           
                
            }
        } catch(SQLException e) {
            
        } finally {
            JDBCHelper.cleanupResources(connection, null, null);
        }
    }

    public ConnectionInfo getConnectionInfo() {
        return this.connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
        
    public ConstrainingType getConstrainingType() {
        // The sample DATA implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The sample DATA implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        if(services == null || services.size() == 0) {
            introspectServices(assemblyFactory, javaFactory);
        }
        return services;
    }
    
    public List<Reference> getReferences() {
        // The sample DATA implementation does not support references
        return Collections.emptyList();
    }

    public String getURI() {
        // The sample DATA implementation does not have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The sample DATA implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        // The sample DATA implementation does not have a URI
    }

    public boolean isUnresolved() {
        // The sample DATA implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample DATA implementation is always resolved
    }      
}
