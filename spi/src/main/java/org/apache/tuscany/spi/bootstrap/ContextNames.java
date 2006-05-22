/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.spi.bootstrap;

/**
 * Class that defines the names of well known contexts.
 *
 * @version $Rev$ $Date$
 */
public final class ContextNames {
    private ContextNames() {
    }

    /**
     * The name of the context that forms the root of the application context tree.
     */
    public static final String TUSCANY_ROOT = "tuscany.root";

    /**
     * The name of the context that form the root of the system context tree.
     */
    public static final String TUSCANY_SYSTEM = "tuscany.system";

    /**
     * The name of the context that contains the deployer.
     */
    public static final String TUSCANY_DEPLOYER = "tuscany.deployer";
}
