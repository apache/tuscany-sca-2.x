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

package org.apache.tuscany.sca.extensibility;

import java.io.IOException;
import java.util.Collection;

/**
 * A SPI that allows different implementations of discovering service declarations
 */
public interface ServiceDiscoverer {
    
    /**
     * Get all service declarations for this interface
     * 
     * @param name
     * @return set of service declarations
     * @throws IOException
     */
    public Collection<ServiceDeclaration> getServiceDeclarations(String name) throws IOException;
    
    /**
     * Get first service declaration class for the given interface
     * 
     * @param name
     * @return service implementation class
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ServiceDeclaration getServiceDeclaration(String name) throws IOException;
    
    /**
     * Get a classloader that can be used for thread context loader
     * @return A classloader that can provide access to public classes and resources
     */
    public ClassLoader getContextClassLoader();
}
