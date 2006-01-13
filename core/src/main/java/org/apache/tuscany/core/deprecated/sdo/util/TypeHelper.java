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

import commonj.sdo.Type;

/**
 * Look up a Type given the uri and typeName or interfaceClass.
 * @deprecated replace with usage of SDO2 equivalent
 */
public interface TypeHelper {

    /**
     * Return the Type specified by typeName with the given uri,
     * or null if not found.
     *
     * @param uri      The uri of the Type - type.getURI();
     * @param typeName The name of the Type - type.getName();
     * @return the Type specified by typeName with the given uri,
     *         or null if not found.
     */
    Type getType(String uri, String typeName);

    /**
     * Return the Type for this interfaceClass or null if not found.
     *
     * @param interfaceClass is the interface for the DataObject's Type -
     *                       type.getInstanceClass();
     * @return the Type for this interfaceClass or null if not found.
     */
    Type getType(Class interfaceClass);

}
