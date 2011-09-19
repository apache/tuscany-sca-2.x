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

package org.apache.tuscany.sca.node.extensibility;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

/**
 * An extended Node interface to provide more metadata for Tuscany extensions 
 * @version $Rev $Date$
 * @tuscany.spi.extension.asclient
 */
public interface NodeExtension extends Node {

    /**
     * Get the node URI
     * @return The Tuscany node URI
     */
    String getURI();

    /**
     * Get the domain URI
     * @return The SCA domain URI
     */
    String getDomainURI();

    /**
     * Get the node configuration
     * @return The node cofiguration
     */
    NodeConfiguration getConfiguration();

    /**
     * Get the domain composite
     * @return The domain composite
     */
    Composite getDomainComposite();
    
    /**
     * Get the extension point registry
     */
    ExtensionPointRegistry getExtensionPointRegistry();
}
