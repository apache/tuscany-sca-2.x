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

package org.apache.tuscany.sca.databinding.json;


import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.codehaus.jettison.json.JSONObject;

/**
 * JAXB DataBinding
 *
 * @version $Rev$ $Date$
 */
public class JSONDataBinding extends BaseDataBinding {
    public static final String NAME = "JSON";

    public static final String ROOT_NAMESPACE = "http://tuscany.apache.org/xmlns/sca/databinding/json/1.0";
    public static final QName ROOT_ELEMENT = new QName(ROOT_NAMESPACE, "root");

    public JSONDataBinding() {
        super(NAME, org.json.JSONObject.class);
    }

    @Override
    public boolean introspect(DataType type, Operation operation) {
        assert type != null;
        Class cls = type.getPhysical();
        if (JSONObject.class.isAssignableFrom(cls) || org.json.JSONObject.class.isAssignableFrom(cls)) {
            type.setDataBinding(getName());
            if (type.getLogical() == null) {
                type.setLogical(XMLType.UNKNOWN);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object copy(Object arg, DataType dataType, Operation operation) {
        if (arg == null) {
            return null;
        }
        try {
            Class type = arg != null ? arg.getClass() : null;
            return JSONHelper.toJSON(arg.toString(), type);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
