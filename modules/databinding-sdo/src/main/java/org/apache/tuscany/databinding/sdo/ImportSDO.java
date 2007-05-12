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

package org.apache.tuscany.databinding.sdo;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.impl.BaseImpl;

import commonj.sdo.helper.HelperContext;

/**
 * The model object for import.sdo
 * 
 * @version $Rev$ $Date$
 */
public class ImportSDO extends BaseImpl {
    public static final QName IMPORT_SDO =
        new QName("http://tuscany.apache.org/xmlns/sca/databinding/sdo/1.0", "import.sdo");

    private HelperContext helperContext;
    private String factoryClassName;
    private String schemaLocation;

    public ImportSDO(HelperContext helperContext) {
        super();
        this.helperContext = helperContext;
        setUnresolved(true);
    }

    public HelperContext getHelperContext() {
        return helperContext;
    }

    /**
     * @return the factoryClassName
     */
    public String getFactoryClassName() {
        return factoryClassName;
    }

    /**
     * @param factoryClassName the factoryClassName to set
     */
    public void setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
    }

    /**
     * @return the schemaLocation
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * @param schemaLocation the schemaLocation to set
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
}