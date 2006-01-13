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
package org.apache.tuscany.model.types.java;

import org.eclipse.emf.ecore.EDataType;

public interface JavaTypeHelper {

    /**
     * Returns the SDO Type from a Java type.
     * FIXME reference to emf
     */
    public EDataType getBuiltinDataType(Class javaType);

    /**
     * Returns the Java type from an SDO type.
     */
    public Class getBuiltinJavaType(EDataType dataType);

    /**
     * Returns an EDataType for a Java type
     * FIXME reference to emf
     *
     * @param fullyQualifiedClassName
     */
    public EDataType getDataType(String fullyQualifiedClassName);

    /**
     * Returns the named interface type.
     *
     * @param interfaceName
     */
    JavaInterfaceType getJavaInterfaceType(String interfaceName);

}
