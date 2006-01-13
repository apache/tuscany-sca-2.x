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
package org.apache.tuscany.core.system.config;

import org.apache.tuscany.core.builder.SimpleComponentRuntimeConfiguration;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.system.context.SystemComponentContextImpl;

/**
 * @author delfinoj
 */
public class SystemRuntimeConfigurationImpl implements SimpleComponentRuntimeConfiguration {

    private Class instanceClass;
    private Object instance;

    /**
     * Constructor
     */
    public SystemRuntimeConfigurationImpl(Class instanceClass, Object instance) {
        super();
        this.instanceClass=instanceClass;
        this.instance = instance;
    }
    
    /**
     * @return Returns the instanceClass.
     */
    public Class getInstanceClass() {
        return instanceClass;
    }

    /**
     * @see org.apache.tuscany.core.builder.SimpleComponentRuntimeConfiguration#createComponentContext()
     */
    public SimpleComponentContext createComponentContext() {
        return new SystemComponentContextImpl(instance);
    }

}
