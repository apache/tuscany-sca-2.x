/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.model;

/**
 * Model object that represents the include of a composite by value.
 * 
 * @version $Rev$ $Date$
 */
public class Include extends ModelObject {
    private String name;

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
}
