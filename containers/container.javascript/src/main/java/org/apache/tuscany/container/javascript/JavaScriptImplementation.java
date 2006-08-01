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
package org.apache.tuscany.container.javascript;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.spi.model.AtomicImplementation;
import org.apache.tuscany.spi.model.ComponentType;

/**
 * Model object for a JavaScript implementation.
 */
public class JavaScriptImplementation extends AtomicImplementation<ComponentType> {

    private RhinoScript rhinoScript;

    private ClassLoader cl;

    private ComponentType componentType;

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ClassLoader getClassLoader() {
        return cl;
    }

    public void setClassLoader(ClassLoader cl) {
        this.cl = cl;
    }

    public RhinoScript getRhinoScript() {
        return rhinoScript;
    }

    public void setRhinoScript(RhinoScript rhinoScript) {
        this.rhinoScript = rhinoScript;
    }
}
