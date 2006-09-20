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

package org.apache.tuscany.databinding.idl;

import org.apache.ws.commons.schema.XmlSchemaSimpleType;

/**
 * Type Mapper between XML schema simple data types and java objects
 */
public interface SimpleTypeMapper {
    /**
     * Parse the XML lexical representation into a java object 
     * @param schemaSimpleType The XSD simple type
     * @param value the XML lexical representation
     * @return A java object for the XML value
     */
    Object parse(XmlSchemaSimpleType schemaSimpleType, String value);
    /**
     * Create the XML lexical representation for a java object
     * @param schemaSimpleType The XSD simple type
     * @param obj The java object
     * @return The XML lexical representation
     */
    String toString(XmlSchemaSimpleType schemaSimpleType, Object obj);
}
