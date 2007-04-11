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
package org.apache.tuscany.interfacedef.java.introspect;

/**
 * A registry of {@link JavaInterfaceIntrospectorExtension}s. Interface processors update a
 * service contract definition based on a Java interface
 * 
 * @version $Rev$ $Date$
 */
public interface JavaInterfaceIntrospectorExtensionPoint extends JavaInterfaceIntrospector {

    /**
     * Registers the given extension.
     */
    void addExtension(JavaInterfaceIntrospectorExtension visitor);

    /**
     * Deregisters the given extension.
     */
    void removeExtension(JavaInterfaceIntrospectorExtension visitor);

}
