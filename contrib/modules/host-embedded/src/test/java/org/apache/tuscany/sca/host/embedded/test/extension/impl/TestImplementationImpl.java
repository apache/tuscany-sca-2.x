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
package org.apache.tuscany.sca.host.embedded.test.extension.impl;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.host.embedded.test.extension.TestService;
import org.apache.tuscany.sca.host.embedded.test.extension.TestImplementation;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;


/**
 * The model representing a test implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class TestImplementationImpl implements TestImplementation {

    private Service testService;
    private String greeting;
    
    /**
     * Constructs a new test implementation.
     */
    public TestImplementationImpl(AssemblyFactory assemblyFactory,
                              JavaInterfaceFactory javaFactory) {

        // Test implementations always provide a single service exposing
        // the TestService interface, and have no references and properties
        testService = assemblyFactory.createService();
        testService.setName("Test");
        JavaInterface javaInterface;
        try {
            javaInterface = javaFactory.createJavaInterface(TestService.class);
        } catch (InvalidInterfaceException e) {
            throw new IllegalArgumentException(e);
        }
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        interfaceContract.setInterface(javaInterface);
        testService.setInterfaceContract(interfaceContract);
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public ConstrainingType getConstrainingType() {
        // The test implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The test implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The test implementation provides a single fixed Test service
        return Collections.singletonList(testService);
    }
    
    public List<Reference> getReferences() {
        // The test implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        // The test implementation does not have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The test implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        // The test implementation does not have a URI
    }

    public boolean isUnresolved() {
        // The test implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The test implementation is always resolved
    }
}
