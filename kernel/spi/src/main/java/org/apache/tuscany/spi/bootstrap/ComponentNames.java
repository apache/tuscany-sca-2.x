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
package org.apache.tuscany.spi.bootstrap;

/**
 * Class that defines the names of well known component
 *
 * @version $Rev$ $Date$
 */
public final class ComponentNames {
    /**
     * The name of the component that forms the root of the runtime tree.
     */
    public static final String TUSCANY_RUNTIME = "tuscany.runtime";

    /**
     * The name of the component that is the root of the application composite tree.
     */
    public static final String TUSCANY_APPLICATION_ROOT = "tuscany.root.application";

    /**
     * The name of the component that is the root of the system composite tree.
     */
    public static final String TUSCANY_SYSTEM_ROOT = "tuscany.root.system";

    /**
     * The name of the top-level component in the system composite tree.
     */
    public static final String TUSCANY_SYSTEM = "tuscany.system";

    /**
     * The name of the component that contains the deployer.
     */
    public static final String TUSCANY_DEPLOYER = "deployer";

    /**
     * The name of the component that contains the deployer.
     */
    public static final String TUSCANY_WIRE_SERVICE = "wireService";

    private ComponentNames() {
    }

}
