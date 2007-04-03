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
package org.apache.tuscany.assembly;

import javax.xml.namespace.QName;

import org.apache.tuscany.policy.IntentAttachPoint;

/**
 * A property allows for the configuration of an implementation with externally
 * set data values. An implementation can have zero or more properties. Each
 * property has a data type, which may be either simple or complex. An
 * implementation may also define a default value for a property.
 */
public interface AbstractProperty extends Base, IntentAttachPoint {

    /**
     * Returns the property name.
     * 
     * @return the property name
     */
    String getName();

    /**
     * Sets the property name.
     * 
     * @param name the property name
     */
    void setName(String name);

    /**
     * Returns the default value of the property.
     * 
     * @return the default value of ths property
     */
    Object getDefaultValue();

    /**
     * Sets the default value of the property.
     * 
     * @param defaultValue the default value of ths property
     */
    void setDefaultValue(Object defaultValue);

    /**
     * Returns true if the property allows multiple values.
     * 
     * @return true if the property allows multiple values
     */
    boolean isMany();

    /**
     * Sets whether or not the property allows multiple values.
     * 
     * @param many true if the property should allow multiple values
     */
    void setMany(boolean many);

    /**
     * Returns true if a value must be supplied for the property.
     * 
     * @return true is a value must be supplied for the property
     */
    boolean isMustSupply();

    /**
     * Sets whether a value must be supplied for the property.
     * 
     * @param mustSupply set to true to require that a value be supplied for
     *            uses of this property
     */
    void setMustSupply(boolean mustSupply);

    /**
     * Returns the data type of this property. This is the qualified name of an
     * XML schema type.
     * 
     * @return the type of this property
     */
    QName getXSDType();

    /**
     * Sets the data type of this property. This is the qualified name of an XML
     * schema type.
     * 
     * @param type the type of this property
     */
    void setXSDType(QName type);

    /**
     * Returns the element defining the data type of this property. This is the
     * qualified name of an XML schema element.
     * 
     * @return the element defining the type of this property
     */
    QName getXSDElement();

    /**
     * Sets the element defining the data type of this property. This is the
     * qualified name of an XML schema element.
     * 
     * @param type the element defining the type of this property
     */
    void setXSDElement(QName element);

}
