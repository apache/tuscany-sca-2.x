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

package org.apache.tuscany.sca.core.databinding.transformers;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;

/**
 *
 * @version $Rev$ $Date$
 */
public class CallableReferenceTypeHelper implements XMLTypeHelper {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String ANYTYPE_NAME = "anyType";
    private static final QName ANYTYPE_QNAME = new QName(SCHEMA_NS, ANYTYPE_NAME);

    public CallableReferenceTypeHelper() {
        super();
    }

    public TypeInfo getTypeInfo(Class javaType, Object logical) {
        QName xmlType = JavaXMLMapper.getXMLType(javaType);
        if (xmlType != null) {
            return new TypeInfo(xmlType, true, null);
        } else if (javaType.isInterface()) {
            return new TypeInfo(ANYTYPE_QNAME, true, null);
        } else {
            if (logical instanceof XMLType) {
                xmlType = ((XMLType)logical).getTypeName();
            }
            if (xmlType == null) {
                xmlType = new QName(Introspector.decapitalize(javaType.getSimpleName()));
            }
            return new TypeInfo(xmlType, false, null);
        }
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, Interface intf) {
        return new ArrayList<XSDefinition>();
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, List<DataType> dataTypes) {
        return new ArrayList<XSDefinition>();
    }

}
