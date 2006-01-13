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

import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @deprecated replace with usage of SDO2 equivalent
 */
public interface XSDHelper {
    /**
     * Returns the local name as declared in the XSD.
     *
     * @param type to return local name for.
     * @return the local name as declared in the XSD.
     */
    String getLocalName(Type type);

    /**
     * Returns the local name as declared in the XSD.
     *
     * @param property to return local name for.
     * @return the local name as declared in the XSD.
     */
    String getLocalName(Property property);

    /**
     * Returns the namespace URI as declared in the XSD.
     * @param property to return namespace URI for.
     * @return the namespace URI as declared in the XSD.
     */
    String getNamespaceURI(Property property);
}
