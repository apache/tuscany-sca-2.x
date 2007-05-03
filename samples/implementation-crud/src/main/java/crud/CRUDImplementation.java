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
package crud;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;

/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 * The sample CRUD implementation is not a full blown implementation, it only
 * supports a subset of what a component implementation can support: - a single
 * fixed service (as opposed to a list of services typed by different
 * interfaces) - a directory attribute used to specify where a CRUD component is
 * going to persist resources - no references or properties - no policy intents
 * or policy sets
 * 
 * @version $$Rev$$ $$Date: 2007-04-23 19:18:54 -0700 (Mon, 23 Apr
 *          2007) $$
 */
public class CRUDImplementation implements Implementation {

    private Service crudService;
    private String directory;

    /**
     * Constructs a new CRUD implementation.
     */
    public CRUDImplementation(AssemblyFactory assemblyFactory,
                              JavaFactory javaFactory,
                              JavaInterfaceIntrospector introspector) {

        // CRUD implementation always provide a single service exposing
        // the CRUD interface, and have no references and properties
        crudService = assemblyFactory.createService();
        crudService.setName("CRUD");
        JavaInterface javaInterface;
        try {
            javaInterface = introspector.introspect(CRUD.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        crudService.setInterfaceContract(interfaceContract);
    }

    /**
     * Returns the directory used by CRUD implementations to persist resources.
     * 
     * @return the directory used to persist resources
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the directory used by CRUD implementations to persist resources.
     * 
     * @param directory the directory used to persist resources
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ConstrainingType getConstrainingType() {
        // CRUD implementations do not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // CRUD implementations do not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // CRUD implementations provide a single fixed CRUD service
        return Collections.singletonList(crudService);
    }
    
    public List<Reference> getReferences() {
        // CRUD implementations do not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        // CRUD implementations don't have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // CRUD implementations do not support constrainingTypes
    }

    public void setURI(String uri) {
        // CRUD implementations don't have a URI
    }

    public List<PolicySet> getPolicySets() {
        // CRUD implementations do not support policy sets
        return Collections.emptyList();
    }

    public List<Intent> getRequiredIntents() {
        // CRUD implementations do not support intents
        return Collections.emptyList();
    }

    public List<Object> getExtensions() {
        // CRUD implementations do not support extensions
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        // CRUD implementations are always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // CRUD implementations are always resolved
    }

}
