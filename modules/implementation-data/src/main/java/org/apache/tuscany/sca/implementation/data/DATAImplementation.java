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

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.data.config.ConnectionInfo;
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

    private Service dataService;
    private ConnectionInfo connectionInfo;
    private String table;

    /**
     * Constructs a new DAS implementation.
     */
    public DATAImplementation(AssemblyFactory assemblyFactory,
                              JavaInterfaceFactory javaFactory) {

        // DATA implementation always provide a single service exposing
        // the DATA interface, and have no references and properties
        dataService = assemblyFactory.createService();
        dataService.setName("DATA");
        JavaInterface javaInterface;
        try {
            javaInterface = javaFactory.createJavaInterface(DATA.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        dataService.setInterfaceContract(interfaceContract);
    }

    public ConnectionInfo getConnectionInfo() {
        return this.connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
    
    public String getTable() {
        return this.table;
    }

    public void setTable(String config) {
        this.table = config;
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
        // The sample DATA implementation provides a single fixed CRUD service
        return Collections.singletonList(dataService);
    }
    
    public List<Reference> getReferences() {
        // The sample DATA implementation does not support properties
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


    public List<Object> getExtensions() {
        // The sample DATA implementation does not support extensions
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        // The sample DATA implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample DATA implementation is always resolved
    }      
}
