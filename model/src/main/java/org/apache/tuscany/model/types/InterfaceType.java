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
package org.apache.tuscany.model.types;

import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * Represents a service interface.
 */
public interface InterfaceType extends AssemblyModelObject {

    /**
     * Returns the list of operation types on the interface.
     *
     * @return
     */
    List<OperationType> getOperationTypes();

    /**
     * Returns the named operation type.
     *
     * @param name
     * @return
     */
    OperationType getOperationType(String name);

    /**
     * Returns the interface URI.
     *
     * @return
     */
    String getURI();

    /**
     * Returns the Java class that this interface type represents.
     *
     * @return
     */
    Class getInstanceClass();

    /**
     * Sets the Java class that this interface represents.
     * @param instanceClass
     */
    void setInstanceClass(Class instanceClass);
	
}
