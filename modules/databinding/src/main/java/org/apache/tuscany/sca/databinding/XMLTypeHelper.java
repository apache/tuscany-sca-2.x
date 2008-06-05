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

package org.apache.tuscany.sca.databinding;

import java.util.List;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;

/**
 * XML and schema helper for Java types
 */
public interface XMLTypeHelper {

    // FIXME: This method seems to require the XMLHelper instance to be stateful
    /**
     * Convert a Java type into XML type information 
     * @param javaType the physical Java class
     * @param logical the logical type information
     * @return the XML type information
     */
    TypeInfo getTypeInfo(Class javaType, Object logical);

    /**
     * Get schema definitions for the Java types known to this helper
     * @return A list of schema definitions
     */
    // List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver);
    
    /**
     * Get a list of XML schemas for the given data types
     * @param factory
     * @param resolver
     * @param dataTypes
     * @return
     */
    List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, List<DataType> dataTypes);
    /**
     * Get a list of XML schemas for the given interface
     * @param factory
     * @param resolver
     * @param intf
     * @return
     */
    List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, Interface intf);

}
