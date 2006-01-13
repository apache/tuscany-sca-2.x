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

import java.util.List;

import org.apache.tuscany.model.assembly.sdo.OverrideOptions;


/**
 * A representation of the model object '<em><b>External Service</b></em>'.
 */
public interface ExternalService extends Part, Port, ExtensibleModelObject {

    /**
     * Returns the value of the '<em><b>Bindings</b></em>' containment reference list.
     */
    List<Binding> getBindings();

    /**
     * Returns the value of the '<em><b>Overridable</b></em>' attribute.
     */
    OverrideOptions getOverridable();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.ExternalService#getOverridable <em>Overridable</em>}' attribute.
     */
    void setOverridable(OverrideOptions value);

    /**
     * Returns the service value for this external service.
     *
     * @return
     */
    ConfiguredService getConfiguredService();

} // ExternalService
