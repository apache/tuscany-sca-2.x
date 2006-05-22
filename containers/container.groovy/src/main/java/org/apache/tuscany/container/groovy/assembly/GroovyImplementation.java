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
package org.apache.tuscany.container.groovy.assembly;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.AtomicImplementation;

/**
 * Meta-information for the Groovy implementation.
 *
 */
public class GroovyImplementation extends AtomicImplementation<ComponentType>  {

    // Full path of the script file to be executed
    private String script;

    // Resource loader for accessing classpath resources
    private ResourceLoader resourceLoader;

    /**
     * Gets the full path of the script file to be executed.
     * @return Full path of the script file to be executed.
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the full path of the script file to be executed.
     * @param script Full path of the script file to be executed.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Gets the resource loader for accessing classpath resources.
     * @return Resource loader.
     */
    public ResourceLoader getResourceLoader() { return resourceLoader; }

    /**
     * Sets the resource loader for accessing classpath resources.
     * @param resourceLoader Resource loader.
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
