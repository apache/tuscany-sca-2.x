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
 * A representation of the model object '<em><b>Property</b></em>'.
 */
public interface Property extends ExtensibleModelObject {
    /**
     * Returns the value of the '<em><b>Default</b></em>' attribute.
     */
    String getDefault();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Property#getDefault <em>Default</em>}' attribute.
     */
    void setDefault(String value);

    /**
     * Returns the value of the '<em><b>Many</b></em>' attribute.
     */
    boolean isMany();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Property#isMany <em>Many</em>}' attribute.
     */
    void setMany(boolean value);

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     */
    String getName();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Property#getName <em>Name</em>}' attribute.
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Required</b></em>' attribute.
     */
    boolean isRequired();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Property#isRequired <em>Required</em>}' attribute.
     */
    void setRequired(boolean value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     */
    Object getType_();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.Property#getType_ <em>Type</em>}' attribute.
     */
    void setType(Object value);

} // Property
