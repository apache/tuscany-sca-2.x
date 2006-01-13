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
 * A representation of the model object '<em><b>Reference</b></em>'.
 */
public interface Reference extends Port {

    /**
     * Returns the value of the '<em><b>Multiplicity</b></em>' attribute.
     */
    String getMultiplicity();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Reference#getMultiplicity <em>Multiplicity</em>}' attribute.
     */
    void setMultiplicity(String value);

    /**
     * Returns true if the reference has an N multiplicity.
     */
    boolean isMultiplicityN();

} // Reference
