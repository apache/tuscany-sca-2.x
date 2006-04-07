/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.context.webapp;

import org.apache.tuscany.core.context.CompositeContext;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;

/**
 * An implementation of the SCA runtime for use in a Web app
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyWebAppRuntime extends SCA {
    private CompositeContext moduleComponentContext;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public TuscanyWebAppRuntime(CompositeContext moduleComponentContext) {
        this.moduleComponentContext = moduleComponentContext;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    /**
     * Returns the module component context associated with this runtime
     */
    public CompositeContext getModuleComponentContext() {
        return moduleComponentContext;
    }

    public void start() {
        // Associate it with the current thread
        setModuleContext((ModuleContext) moduleComponentContext);
    }

    public void stop() {
        setModuleContext(null);
    }

}
