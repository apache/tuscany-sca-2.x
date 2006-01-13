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
package org.apache.tuscany.model.assembly;

/**
 * A representation of the model object '<em><b>Module Component</b></em>'.
 */
public interface ModuleComponent extends Component {

    /**
     * Returns the module implementing this ModuleComponent.
     *
     * @return
     */
    Module getModuleImplementation();

    /**
     * Set the module implementing this ModuleComponent.
     *
     * @param module
     */
    void setModuleImplementation(Module module);

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     */
    String getURI();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.ModuleComponent#getUri <em>Uri</em>}' attribute.
     */
    void setURI(String value);

} // ModuleComponent
