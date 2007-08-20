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
package crud2.extension;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

import crud2.CRUD;


/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 */
public class CRUDImplementation implements Implementation {

    private Service crudService;
    private String directory;

    /**
     * Constructs a new CRUD implementation.
     */
    public CRUDImplementation() {

        // CRUD implementation always provide a single service exposing
        // the CRUD Java interface, create the model representing that
        // fixed service here
        
        // Create a default service named CRUD
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        crudService = assemblyFactory.createService();
        crudService.setName("CRUD");
        
        // Create a Java interface model for the CRUD Java interface
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterface javaInterface;
        try {
            javaInterface = javaFactory.createJavaInterface(CRUD.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        
        // Create a Java interface contract model and set it
        // into the service
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        crudService.setInterfaceContract(interfaceContract);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ConstrainingType getConstrainingType() {
        // The sample CRUD implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The sample CRUD implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The sample CRUD implementation provides a single fixed CRUD service
        return Collections.singletonList(crudService);
    }
    
    public List<Reference> getReferences() {
        // The sample CRUD implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        // The sample CRUD implementation does not have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The sample CRUD implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        // The sample CRUD implementation does not have a URI
    }

    public List<Object> getExtensions() {
        // The sample CRUD implementation does not support extensions
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        // The sample CRUD implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample CRUD implementation is always resolved
    }

}
