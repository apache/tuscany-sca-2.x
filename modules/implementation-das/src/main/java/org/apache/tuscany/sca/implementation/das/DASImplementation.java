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
package org.apache.tuscany.sca.implementation.das;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;


/**
 * The model representing a sample DAS implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class DASImplementation implements Implementation {

    private Service dasService;
    private String dasConfig;
    private String dataAccessType;

    /**
     * Constructs a new DAS implementation.
     */
    public DASImplementation(AssemblyFactory assemblyFactory,
                              JavaInterfaceFactory javaFactory) {

        // DAS implementation always provide a single service exposing
        // the DAS interface, and have no references and properties
        dasService = assemblyFactory.createService();
        dasService.setName("DAS");
        JavaInterface javaInterface;
        try {
            javaInterface = javaFactory.createJavaInterface(DAS.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        dasService.setInterfaceContract(interfaceContract);
    }

    public String getConfig() {
        return this.dasConfig;
    }

    public void setConfig(String config) {
        this.dasConfig = config;
    }
    
    public String getDataAccessType() {
        return this.dataAccessType;
    }
    
    public void setDataAccessType (String dataAccessType) {
        this.dataAccessType = dataAccessType;
    }

    public ConstrainingType getConstrainingType() {
        // The sample DAS implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The sample DAS implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The sample DAS implementation provides a single fixed CRUD service
        return Collections.singletonList(dasService);
    }
    
    public List<Reference> getReferences() {
        // The sample DAS implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        // The sample DAS implementation does not have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The sample DAS implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        // The sample DAS implementation does not have a URI
    }

    public List<Object> getExtensions() {
        // The sample DAS implementation does not support extensions
        return Collections.emptyList();
    }
    
    public boolean isUnresolved() {
        // The sample DAS implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample DAS implementation is always resolved
    }
}
