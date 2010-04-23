/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.policy;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Definition of extension type: binding or implementation
 * @tuscany.spi.extension.asclient
 */
public interface ExtensionType {
    QName BINDING_BASE = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "binding");
    QName IMPLEMENTATION_BASE = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "implementation");

    /**
     * The name of the extension type (binding or implementation). The extension type name 
     * attribute MUST be the QName of an XSD global element definition used for 
     * binding/implementation elements of that type
     * 
     * @return The QName of this type
     */
    QName getType();

    /**
     * Set the QName for this type
     * @param type The QName of this type
     */
    void setType(QName type);
    
    QName getBaseType();

    /**
     * A set of intents. The intents in the alwaysProvides set are always 
     * provided by this extension type, whether the intents are attached 
     * to the using component or not.
     * 
     * @return A list of intents that are always provided by this type
     */
    List<Intent> getAlwaysProvidedIntents();

    /**
     * a set of intents. The intents in the mayProvide set are provided by this 
     * extension type if the intent in question is attached to the using 
     * component.
     * 
     * @return A list of intents that may be provided by this type
     */
    List<Intent> getMayProvidedIntents();
    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);
    
}
