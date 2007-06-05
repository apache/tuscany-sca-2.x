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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.spi.utils.AbstractImplementation;


/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 */
public class CRUDImplementation extends AbstractImplementation {

    private Service crudService;
    private String directory;

    public CRUDImplementation(AssemblyFactory assemblyFactory,
                              JavaInterfaceIntrospectorExtensionPoint visitors) {

            // CRUD implementation always provide a single service exposing
            // the CRUD interface, and have no references and properties
            crudService = assemblyFactory.createService();
            crudService.setName("CRUD");

            JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
            JavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, visitors);

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
    
    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<Service> getServices() {
        // The sample CRUD implementation provides a single fixed CRUD service
        return Collections.singletonList(crudService);
    }
}
