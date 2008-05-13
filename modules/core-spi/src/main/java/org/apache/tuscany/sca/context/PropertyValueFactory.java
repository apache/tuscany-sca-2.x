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
package org.apache.tuscany.sca.context;

import org.apache.tuscany.sca.assembly.ComponentProperty;

/**
 * Interface implemented by the provider of the property values
 *
 * @version $Rev$ $Date$
 */
public interface PropertyValueFactory {

    /**
     * This method will create an instance of the value for the specified Property.
     * 
     * @param property The Property from which to retrieve the property value
     * @param type The type of the property value being retrieved from the Property
     * @param <B> Type type of the property value being looked up
     * 
     * @return the value for the Property
     */
    <B> B createPropertyValue(ComponentProperty property, Class<B> type);
}
