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

package org.apache.tuscany.sca.core;

import java.util.Map;

/**
 * ModuleActivator represents a module that plugs into the Tuscany system. Each module should
 * provide an implementation of this interface and registry the implementation class by defining 
 * a file named as "META-INF/services/org.apache.tuscany.spi.bootstrp.ModuleActivator". The
 * content of the file is the class name of the implementation. The implementation class must
 * have a no-arg constructor. The same instance will be used to invoke all the methods during
 * different phases of the module activation.
 * 
 * @version $Rev$ $Date$
 */
public interface ModuleActivator {
    /**
     * Get a map of the extension points defined by this module. The key is the
     * java interface to represent the extension point and the the value is the
     * instance of the implementation of the interface.
     * 
     * @return All the extension points defined by this module
     */
    Map<Class, Object> getExtensionPoints();

    /**
     * This method is invoked when the module is started by the Tuscany system.
     * It can be used by this module to registr extensions against extension
     * points.
     * 
     * @param registry The extension point registry
     */
    void start(ExtensionPointRegistry registry);

    /**
     * This method is invoked when the module is stopped by the Tuscany system.
     * It can be used by this module to unregister extensions against the
     * extension points.
     * 
     * @param registry The extension point registry
     */
    void stop(ExtensionPointRegistry registry);
}
