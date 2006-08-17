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
package org.apache.tuscany.databinding;


/**
 * DataBinding interface defines a data binding. 
 */
public interface DataBinding {

    /**
     * Get the name of the data binding. For example, "sdo" for Service Data Object (SDO).
     * The name is NOT case sensitive. We recommond a pattern as "xxx.yyy", for example, "sdo.dataObject"
     * 
     * 
     * 
     * @return The name which uniquely idetifies the data binding
     */
    public String getName();
    
    /**
     * Check if the given java type is supported by this data type
     * @param javaClass
     * @return
     */
    public boolean isSupported(Class javaClass);
    
    /**
     * Check if the data binding is for writing only (for example, Writer, OutputStream and ContentHandler)
     * @return true if the data type if for writing only 
     */
    public boolean isSinkOnly();    
    
    public Object getAttribute(String name);
    public void setAttribute(String name, Object value);

}
