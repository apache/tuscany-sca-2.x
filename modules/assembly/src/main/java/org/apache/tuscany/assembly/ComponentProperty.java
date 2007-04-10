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

/**
 * Represents a configured property of a component.
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentProperty extends Property {

    /**
     * Returns the property defined by the component implementation.
     * 
     * @return the property defined by the component implementation
     */
    Property getProperty();

    /**
     * Sets the property type that defines this property.
     * 
     * @param type the property that defines this property
     */
    void setProperty(Property property);

    /**
     * Returns an xpath expression referencing a property of the enclosing
     * composite.
     * 
     * @return an xpath expression referencing a property of the enclosing
     *         composite
     */
    String getSource();

    /**
     * Sets an xpath expression referencing a property of the enclosing
     * composite.
     * 
     * @param source an xpath expression referencing a property of the enclosing
     *            composite
     */
    void setSource(String source);

    /**
     * Returns a URI to a file containing the property value.
     * 
     * @return a URI to a file containing the property value
     */
    String getFile();

    /**
     * Sets a URI to a file containing the property value.
     * 
     * @param file a URI to a file containing the property value
     */
    void setFile(String file);
}
