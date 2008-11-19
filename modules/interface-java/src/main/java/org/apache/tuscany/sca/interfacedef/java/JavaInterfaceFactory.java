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
package org.apache.tuscany.sca.interfacedef.java;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * Factory for the Java interface model
 * 
 * @version $Rev$ $Date$
 */
public interface JavaInterfaceFactory {

    /**
     * Creates a new Java interface model.
     * 
     * @return
     */
    JavaInterface createJavaInterface();
    
    /**
     * Creates a new Java interface model from an interface class.
     * @param interfaceClass the interface class to introspect.
     * @return
     */
    JavaInterface createJavaInterface(Class<?> interfaceClass) throws InvalidInterfaceException;
    
    /**
     * Creates the contents of a Java interface model from an interface class.
     * @param javaInterface the Java interface model
     * @param interfaceClass the interface class to introspect.
     * @return
     */
    void createJavaInterface(JavaInterface javaInterface, Class<?> interfaceClass) throws InvalidInterfaceException;
    
    /**
     * Creates a new Java interface contract.
     * 
     * @return
     */
    JavaInterfaceContract createJavaInterfaceContract();

    /**
     * Registers the given visitor.
     * 
     * @param visitor
     */
    void addInterfaceVisitor(JavaInterfaceVisitor visitor);

    /**
     * Deregisters the given visitor.
     * 
     * @param visitor
     */
    void removeInterfaceVisitor(JavaInterfaceVisitor visitor);

    /**
     * Returns a list of interface visitors.
     * 
     * @return
     */
    List<JavaInterfaceVisitor> getInterfaceVisitors();
}
