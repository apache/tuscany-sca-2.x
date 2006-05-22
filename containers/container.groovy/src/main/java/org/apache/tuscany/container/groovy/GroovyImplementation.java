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
package org.apache.tuscany.container.groovy;

import java.net.URI;

import org.apache.tuscany.model.AtomicImplementation;
import org.apache.tuscany.model.ComponentType;

/**
 * Meta-information for the Groovy implementation.
 */
public class GroovyImplementation extends AtomicImplementation<ComponentType> {

    // Full path of the script file to be executed
    private URI script;

    /**
     * Returns the path of the script file to be executed.
     */
    public URI getScript() {
        return script;
    }

    /**
     * Sets the path of the script file to be executed.
     */
    public void setScript(URI script) {
        this.script = script;
    }

}
