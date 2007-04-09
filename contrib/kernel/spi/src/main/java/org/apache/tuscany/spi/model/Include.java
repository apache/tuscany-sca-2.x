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
package org.apache.tuscany.spi.model;

import java.net.URL;

/**
 * Model object that represents the include of a composite by value.
 * 
 * @version $Rev$ $Date$
 */
public class Include extends ModelObject {
    private String name;
    private URL scdlLocation;
    private CompositeComponentType included;

    /**
     * Returns the name of the composite that is being included.
     * @return the name of the composite that is being included
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the composite that is being included.
     * @param name the name of the composite that is being included
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the location of the SCDL for composite being included.
     * @return the location of the SCDL for composite being included
     */
    public URL getScdlLocation() {
        return scdlLocation;
    }

    /**
     * Sets the location of the SCDL for composite being included.
     * @param scdlLocation the location of the SCDL for composite being included
     */
    public void setScdlLocation(URL scdlLocation) {
        this.scdlLocation = scdlLocation;
    }

    /**
     * Returns the composite that was included.
     * @return the composite that was included
     */
    public CompositeComponentType getIncluded() {
        return included;
    }

    /**
     * Sets the composite that was included.
     * @param included the composite that was included
     */
    public void setIncluded(CompositeComponentType included) {
        this.included = included;
    }
}
