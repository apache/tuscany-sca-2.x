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

package org.apache.tuscany.sca.databinding.sdo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;

import commonj.sdo.Type;

/**
 * The model object for sdo.types
 *
 * @version $Rev$ $Date$
 *
 * This extends the META-INF/sca-contribution.xml to register SDO types from static factory classes or WSDL/XSD files
 *
 * &lt;contribution xmlns=&quot;http://tuscany.apache.org/xmlns/sca/1.1&quot;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;xmlns:tuscany=&quot;http://tuscany.apache.org/xmlns/sca/1.0&quot;&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;tuscany:sdo.types namespace=&quot;http://helloworld&quot; location=&quot;wsdl/helloworld.wsdl&quot;/&gt;<br>
 * &lt;/contribution&gt;
 *
 */
public class SDOTypes {
    public static final QName SDO_TYPES = new QName(Base.SCA11_TUSCANY_NS, "sdo.types");

    private String factory;
    private String schemaLocation;
    private String namespace;

    private List<Type> types = new ArrayList<Type>();

    private boolean unresolved;

    public SDOTypes() {
        super();
        setUnresolved(true);
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    /**
     * @return the factoryClassName
     */
    public String getFactory() {
        return factory;
    }

    /**
     * @param factoryClassName the factoryClassName to set
     */
    public void setFactory(String factoryClassName) {
        this.factory = factoryClassName;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<Type> getTypes() {
        return types;
    }
}
