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
package org.apache.tuscany.core.deprecated.sdo.util;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * A Factory for creating SDO DataObjects.
 * @deprecated replace with usage of SDO2 equivalent
 */
public interface DataFactory {

    /**
     * Creates a DataObject supporting the given interface.
     * InterfaceClass is the interface for the DataObject's Type.
     *
     * @param interfaceClass is the interface for the DataObject's Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the instanceClass does
     *                                  not correspond to a Type this factory can instantiate.
     */
    DataObject create(Class interfaceClass);

    /**
     * Creates a DataObject of the Type specified by typeName with the given package uri.
     *
     * @param uri      The uri of the Package.
     * @param typeName The name of the Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the uri and typeName does
     *                                  not correspond to a Type this factory can instantiate.
     */
    DataObject create(String uri, String typeName);

    /**
     * Creates a DataObject of the Type specified.
     * @param type The Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the Type
     *   cannot be instantiaed by this factory.
     */
    DataObject create(Type type);

}