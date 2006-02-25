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
 * The association of a port with a requestor.
 */
public interface Reference extends Port {
    /**
     * Returns the multiplicity allowed for wires connected to this reference.
     * @return the multiplicity allowed for wires connected to this reference
     */
    Multiplicity getMultiplicity();

    /**
     * Sets the multiplicity allowed for wires connected to this reference.
     * @param multiplicity the multiplicity allowed for wires connected to this reference
     */
    void setMultiplicity(Multiplicity multiplicity);

}
