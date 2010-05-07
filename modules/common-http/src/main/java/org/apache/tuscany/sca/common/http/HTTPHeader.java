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

package org.apache.tuscany.sca.common.http;

/**
 * Wrapper for HTTP header name/value pair
 * 
 * @version $Rev$ $Date$
 */

public class HTTPHeader {
    private String name;
    private String value;

    public HTTPHeader() {
        super();
    }
    
    public HTTPHeader(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Get header name
     * @return the header name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set header value
     * @param name the header name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get header value
     * @return the header value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Set header value
     * @param value the header value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
